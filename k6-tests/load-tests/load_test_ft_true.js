import http from 'k6/http';
import { check, sleep } from 'k6';


// -----------------------------
// PERFIL DE CARGA
// -----------------------------
export const options = {
    stages: [
        { duration: '3m', target: 50 },  // rampa 0 → 50 - ramp-up suave
        { duration: '10m', target: 50 },  // manter 50 - carga estável
        { duration: '2m', target: 0 },   // rampa 50 → 0 - ramp-down
    ],

    thresholds: {
        // até 10% de falhas permitido com tolerância
        http_req_failed: ['rate<0.10'],

        // 95% das requisições devem responder abaixo de 2000ms
        http_req_duration: ['p(95)<2000'],      
    },
};

// -----------------------------
// MÉTRICAS CUSTOMIZADAS
// -----------------------------
import { Trend, Rate, Counter } from 'k6/metrics';

const mtbf = new Trend('mtbf'); // tempo entre falhas
const recoveryTime = new Trend('recovery_time'); // tempo até recuperar
const successRate = new Rate('success_rate'); // taxa de sucesso
const errorRate = new Rate('error_rate');     // taxa de erro
const failureCount = new Counter('failure_count');

let lastFailureTime = 0;
let lastRecoveryTime = 0;

export default function () {
    const url = 'http://localhost:8081/buyTicket?flight=1001&day=2025-12-01&user=3&ft=true';

    const res = http.post(url);

    const ok = check(res, {
        'status é 200': (r) => r.status === 200,
    });

    // Taxas de sucesso/falha
    successRate.add(ok);
    errorRate.add(!ok);

    // -----------------------------
    // Cálculo MTBF e Resiliência
    // -----------------------------
    if (!ok) {
        failureCount.add(1);
        const now = Date.now();

        if (lastFailureTime > 0) {
            mtbf.add(now - lastFailureTime);
        }

        lastFailureTime = now;
        lastRecoveryTime = now;
    }

    if (ok && lastRecoveryTime > 0) {
        const now = Date.now();
        recoveryTime.add(now - lastRecoveryTime);
        lastRecoveryTime = 0;
    }

    sleep(1);
}

