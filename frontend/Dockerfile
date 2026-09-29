FROM node:22-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN VITE_WALLET_API_URL=/api VITE_TRANSACTION_API_URL=/transactions-api npm run build

FROM nginxinc/nginx-unprivileged:stable-alpine3.24
USER root
RUN apk upgrade --no-cache
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
USER 101
EXPOSE 8080
HEALTHCHECK --interval=10s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -qO- http://127.0.0.1:8080/health || exit 1
