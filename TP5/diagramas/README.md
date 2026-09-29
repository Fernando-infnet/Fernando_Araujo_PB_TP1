# Diagramas do TP5

Os diagramas executáveis estão incorporados nos documentos Markdown para permanecerem versionados e fáceis de abrir no link da entrega:

- arquitetura do cluster: [KUBERNETES.md](../KUBERNETES.md);
- implantação e componentes Docker: [IMPLANTACAO.md](../IMPLANTACAO.md);
- fluxo de métricas, logs e traces: [OBSERVABILIDADE.md](../OBSERVABILIDADE.md);
- pipeline de CI/CD: [CI-CD.md](../CI-CD.md).

Os diagramas podem ser mantidos em Mermaid dentro dos documentos ou exportados como SVG/PNG para a apresentação.

## Arquitetura final consolidada

O diagrama usado na apresentação reúne a aplicação orientada a eventos e toda a pilha de observabilidade:

- [SVG vetorial](arquitetura-final.svg), recomendado para abrir no navegador durante a gravação;
- [PNG](arquitetura-final.png), para slides e visualizadores que não aceitam SVG;
- [fonte Graphviz](arquitetura-final.dot), para edição e nova exportação.

Para regenerar os arquivos:

```bash
dot -Tsvg TP5/diagramas/arquitetura-final.dot -o TP5/diagramas/arquitetura-final.svg
dot -Tpng -Gdpi=180 TP5/diagramas/arquitetura-final.dot -o TP5/diagramas/arquitetura-final.png
```
