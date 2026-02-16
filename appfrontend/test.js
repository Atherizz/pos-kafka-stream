import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
    vus: 10,
    duration: '1s',
};

export default function () {
    const url = 'http://localhost:8080/api/transactions';
    const payload = JSON.stringify({
        items: [{ qty: 1, product_id: 4 }],
        transaction_date: '2026-02-16',
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJlbWFpbEBleGFtcGxlLmNvbSIsImlhdCI6MTc3MTI0Mzk2NiwiZXhwIjoxNzcxMjQ3NTY2fQ.sLZ7oS3nxRpiiKLB1RtEpUnXGvjAjJssCRRf8ApmjZ9V4CbXZcpWuzecPGvF-H4NIv6ZBhEK2fBJE3XANNGIsA',
        },
    };

    http.post(url, payload, params);
}