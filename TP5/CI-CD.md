# Integração e entrega contínuas — TP5

## Gatilhos

O workflow `.github/workflows/ci-cd.yml` roda testes em pull requests, executa CI/CD em pushes para `main` e tags `v*`, e permite disparo manual. O deploy só ocorre no disparo manual protegido pelo environment `production`.

## Etapas do pipeline

1. Build dos serviços e do frontend.
2. Testes unitários e de integração.
3. Geração e upload dos relatórios JaCoCo.
4. Validação do Docker Compose e renderização dos manifestos Kubernetes.
5. Build paralelo das três imagens Docker.
6. Publicação no GHCR com tags de SHA, versão e `latest`.
7. Deploy manual no Kubernetes e espera dos três rollouts.

## Segredos e ambientes

- `GITHUB_TOKEN`: fornecido automaticamente para publicação no GHCR.
- `KUBE_CONFIG`: kubeconfig codificado em Base64 para o deploy manual.
- environment `production`: deve exigir aprovação antes do deploy.

Senhas da aplicação devem ser mantidas no gerenciador de Secrets do cluster, nunca em Actions logs ou no repositório.

## Evidências

Após enviar os commits ao GitHub, adicionar aqui o link de uma execução verde e mostrar na gravação os jobs `test`, `images` e, quando houver cluster disponível, `deploy`.
