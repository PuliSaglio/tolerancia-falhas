import http from "k6/http";
import { check, sleep } from "k6";
import { Trend, Counter, Rate } from "k6/metrics";

// =======================================
// MÉTRICAS PERSONALIZADAS
// =======================================
export const mtbf = new Trend("mtbf");                 // tempo desde última falha
export const recovery_time = new Trend("recovery_time"); // tempo para recuperar
export const failureCount = new Counter("failureCount");

export const successRate = new Rate("successRate");    // taxa de sucesso (check)
export const errorRate = new Rate("errorRate");        // taxa de erro (check)

// Variáveis internas
let lastFailureTime = null;
let failureInProgress = false;

// =======================================
// CONFIGURAÇÃO DO TESTE DE ESTRESSE
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
        http_req_duration{status:200}: ["p(95)<5000"],

        // Resiliência
        successRate: ["rate>0.70"],
    },
};

// Variável FT configurável
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

    // Sucesso e erro
    successRate.add(ok);
    errorRate.add(!ok);

    const failed = !ok;

    // =======================================
    // FALHAS E RECUPERAÇÃO
    // =======================================
    if (failed) {
        failureCount.add(1);

        // início de um ciclo de falha
        if (!failureInProgress) {
            lastFailureTime = Date.now();
            failureInProgress = true;
        }

    } else {
        // recuperou após falhas
        if (failureInProgress) {
            const now = Date.now();
            recovery_time.add(now - lastFailureTime);
            failureInProgress = false;
        }
    }

    // =======================================
    // MTBF (tempo sem falhar)
    // =======================================
    if (lastFailureTime) {
        mtbf.add(Date.now() - lastFailureTime);
    }

    sleep(1);
}

