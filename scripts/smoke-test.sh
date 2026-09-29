#!/usr/bin/env sh
set -eu

base_url="${BASE_URL:-http://localhost:3000}"

curl --fail --silent --show-error "${base_url}/health" >/dev/null
curl --fail --silent --show-error "${base_url}/api/actuator/health/readiness" >/dev/null
curl --fail --silent --show-error "${base_url}/transactions-api/actuator/health/readiness" >/dev/null

request_id="$(date +%s)-$$"
user_json="$(curl --fail --silent --show-error \
  -X POST "${base_url}/api/users" \
  -H 'Content-Type: application/json' \
  -H "X-Correlation-ID: smoke-${request_id}" \
  -d "{\"name\":\"Smoke Test\",\"email\":\"smoke-${request_id}@example.com\"}")"
user_id="$(printf '%s' "$user_json" | sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p')"
test -n "$user_id"

wallet_json="$(curl --fail --silent --show-error \
  -X POST "${base_url}/api/wallets" \
  -H 'Content-Type: application/json' \
  -H "X-Correlation-ID: smoke-${request_id}" \
  -d "{\"userId\":${user_id},\"currency\":\"BRL\"}")"
wallet_id="$(printf '%s' "$wallet_json" | sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p')"
test -n "$wallet_id"

# A criação da carteira chega ao microsserviço de transações por RabbitMQ.
attempt=0
until curl --fail --silent --show-error \
  "${base_url}/transactions-api/transactions/wallet/${wallet_id}/balance" >/dev/null 2>&1; do
  attempt=$((attempt + 1))
  test "$attempt" -lt 20
  sleep 1
done

transaction_json="$(curl --fail --silent --show-error \
  -X POST "${base_url}/api/transactions" \
  -H 'Content-Type: application/json' \
  -H "X-Correlation-ID: smoke-${request_id}" \
  -d "{\"walletId\":${wallet_id},\"type\":\"CREDIT\",\"amount\":10.00,\"description\":\"Smoke test\"}")"
transaction_id="$(printf '%s' "$transaction_json" | sed -n 's/.*"id":\([0-9][0-9]*\).*/\1/p')"
test -n "$transaction_id"

curl --fail --silent --show-error \
  "${base_url}/api/wallets/${wallet_id}/balance" | grep -q '"balance":10.00'
curl --fail --silent --show-error \
  "${base_url}/api/transactions/${transaction_id}" | grep -q '"description":"Smoke test"'

echo "Smoke test concluído: usuário ${user_id}, carteira ${wallet_id}, transação ${transaction_id}, correlationId smoke-${request_id}."
