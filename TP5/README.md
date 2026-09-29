# TP5 — Implantação e manutenção em produção

Esta pasta reúne a documentação, as evidências e o roteiro de apresentação da quinta entrega.

## Vídeo da demonstração

> **Link do vídeo do TP5:** adicionar após a gravação.

O vídeo deverá mostrar a conteinerização, implantação no Kubernetes, escalabilidade, monitoramento, logs, rastreamento, testes e execução do pipeline de CI/CD.

## Documentação

- [Implantação](IMPLANTACAO.md)
- [Kubernetes](KUBERNETES.md)
- [Observabilidade](OBSERVABILIDADE.md)
- [CI/CD com GitHub Actions](CI-CD.md)
- [Plano e resultados dos testes](TESTES.md)
- [Manual de operações](OPERACOES.md)
- [Roteiro da demonstração](DEMONSTRACAO.md)
- [Diagramas](diagramas/README.md)

## Checklist da entrega

- [x] Imagens Docker definidas e documentadas.
- [x] Manifestos Kubernetes com probes, recursos e persistência.
- [x] Escalabilidade e recuperação configuradas.
- [x] Métricas, logs e traces configurados.
- [x] Pipeline de CI/CD versionado.
- [x] Testes automatizados e smoke test implementados.
- [ ] Vídeo gravado, publicado e vinculado acima.

## Validação mais recente

Em 28/09/2026, os testes Java e frontend, o build, os 24 recursos Kubernetes, a pilha Docker completa, o fluxo integrado do smoke test e os três targets do Prometheus foram validados localmente. A primeira execução completa do workflow revelou uma referência inválida do Trivy; o patch está documentado em [CI-CD.md](CI-CD.md) e requer uma nova execução no GitHub após o push.
