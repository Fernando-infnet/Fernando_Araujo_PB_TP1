#!/usr/bin/env sh
set -eu

base_url="${BASE_URL:-http://localhost:3000}"

curl --fail --silent --show-error "${base_url}/health"
curl --fail --silent --show-error "${base_url}/api/actuator/health/readiness"
curl --fail --silent --show-error "${base_url}/transactions-api/actuator/health/readiness"

echo "Smoke test concluído com sucesso em ${base_url}"
