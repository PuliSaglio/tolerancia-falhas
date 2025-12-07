import http from "k6/http";
import { check, sleep } from "k6";
import { Trend, Counter, Rate } from "k6/metrics";

// =======================================
// MÉTRICAS PERSONALIZADAS
// =======================================
export const mtbf = new Trend("mtbf");                 // tempo desde última falha
export const recovery_time = new Trend("recovery_time"); // tempo para recuperar
export const failureCount = new Counter("failureCount");

export const successRate = new Rate("successRate");    // taxa de sucesso
export const errorRate = new Rate("errorRate");        // taxa de erro

// Variáveis internas de controle
let lastFailureTime = null;
let failureInProgress = false;

// =======================================
// CONFIGURAÇÃO DO TESTE (stages + thresholds)
// =======================================
export const options = {
    stages: [
        { duration: '3m', target: 50 },    // carga normal
        { duration: '3m', target: 100 },   // acima do normal
        { duration: '3m', target: 200 },   // perto do limite
        { duration: '3m', target: 300 },   // estresse real
        { duration: '2m', target: 0 },     // redução e recuperação
    ],

    thresholds: {
        // Disponibilidade
        http_req_failed: ["rate<0.30"],

        // Desempenho
        http_req_duration: ["p(95)<5000"],

        // Resiliência
        successRate: ["rate>0.70"],
    },
};

// FT configurável via variável de ambiente
const ft = __ENV.FT ?? "true";

// =======================================
// CENÁRIO DE TESTE
// =======================================
export default function () {
    const url = `http://localhost:8081/buyTicket?flight=1001&day=2025-12-01&user=3&ft=${ft}`;

    const res = http.post(url);

    const ok = check(res, {
        "status é 200": (r) => r.status === 200,
    });

    // Atualiza métricas de sucesso/erro
    successRate.add(ok);
    errorRate.add(!ok);

    const failed = !ok;

    // =======================================
    // LÓGICA DE FALHA / RECUPERAÇÃO
    // =======================================
    if (failed) {
        failureCount.add(1);

        // Início de um ciclo de falha
        if (!failureInProgress) {
            lastFailureTime = Date.now();
            failureInProgress = true;
        }

    } else {
        // Recuperação após o ciclo de falha
        if (failureInProgress) {
            const now = Date.now();
            recovery_time.add(now - lastFailureTime);
            failureInProgress = false;
        }
    }

    // =======================================
    // MTBF (tempo desde a última falha)
    // =======================================
    if (lastFailureTime) {
        mtbf.add(Date.now() - lastFailureTime);
    }

    sleep(1);
}

