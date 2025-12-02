import http from 'k6/http';
import { check, sleep } from 'k6';

// -----------------------------
// PERFIL DE CARGA (STRESS TEST)
// -----------------------------
export const options = {
    stages: [
        { duration: '3m', target: 50 },    // carga normal
        { duration: '3m', target: 100 },   // acima do normal
        { duration: '3m', target: 200 },   // perto do limite
        { duration: '3m', target: 300 },   // estresse real
        { duration: '2m', target: 0 },     // recuperação
    ],

    thresholds: {
    
    	// até 30% de falhas permitido com tolerância
        http_req_failed: ['rate<0.30'],

        // 95% das requisições devem responder abaixo de 5000ms
        http_req_duration: ['p(95)<5000'],
    },
};

// -----------------------------
// MÉTRICAS CUSTOMIZADAS
// -----------------------------
import { Trend, Rate, Counter } from 'k6/metrics';

const mtbf = new Trend('mtbf'); 
const recoveryTime = new Trend('recovery_time'); 
const successRate = new Rate('success_rate'); 
const errorRate = new Rate('error_rate');     
const failureCount = new Counter('failure_count');

let lastFailureTime = 0;
let lastRecoveryTime = 0;

export default function () {
    const url = 'http://localhost:8081/buyTicket?flight=1001&day=2025-12-01&user=3&ft=false';

    const res = http.post(url);

    const ok = check(res, {
        'status é 200': (r) => r.status === 200,
    });

    successRate.add(ok);
    errorRate.add(!ok);

    // Calcular MTBF e tempo de recuperação
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

