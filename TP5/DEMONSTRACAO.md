# Roteiro da apresentação final — TP3, TP4 e TP5

## Formato recomendado

**Duração-alvo:** aproximadamente 9 minutos, permanecendo dentro do intervalo de 7 a 10 minutos.

O roteiro intercala falas curtas com pontos de **improviso controlado**. Nesses pontos, descreva o que realmente estiver vendo na tela. Não invente números de pods, mensagens, testes ou resultados do pipeline.

## Preparação antes da gravação

Inicie a plataforma usando as portas alternativas já validadas nesta máquina:

```bash
WALLET_DB_PORT=5435 FRONTEND_PORT=3002 docker compose up -d --build
```

A primeira inicialização do Grafana pode levar cerca de dois minutos. Confirme:

```bash
docker compose ps
curl -f http://localhost:3002/health
curl -f http://localhost:8080/actuator/health/readiness
curl -f http://localhost:8081/actuator/health/readiness
```

Antes de gravar:

1. abra a aplicação em `http://localhost:3002`;
2. abra o dashboard do Grafana em `http://localhost:3001` — `admin` / `admin`;
3. abra o RabbitMQ em `http://localhost:15672` — `wallet` / `wallet`;
4. deixe o GitHub aberto na execução mais recente de **Actions**;
5. deixe `TP4/ARQUITETURA.md` aberto no diagrama;
6. execute uma vez o smoke test e confirme que existem métricas, logs e traces.
7. na aplicação, deixe o filtro do extrato em **All**, a busca vazia e o gráfico em **Last 30 days**;
8. abra o sino, marque as notificações antigas como lidas e feche o painel. Assim, os lançamentos criados durante a gravação aparecerão claramente como novos;
9. confirme que o perfil mostra **Fernando Dev** e que a carteira principal possui saldo suficiente para demonstrar uma saída de `25,00`.

Não mostre tokens, kubeconfigs nem o conteúdo de Secrets.

## 1. Abertura — 0:00 a 0:30

**Mostrar:** seção **Objetivo e evolução** do `README.md`.

**Fala base:**

> Olá, eu sou Fernando Araújo e esta é a apresentação final da Wallet Platform. No TP3, as transações foram extraídas para um microsserviço independente. No TP4, a comunicação interna evoluiu para eventos com RabbitMQ. No TP5, preparei a plataforma para operação com Docker, Kubernetes, observabilidade, CI/CD e testes automatizados.

**Improviso opcional — até 10 segundos:** diga que todo o código e a documentação estão no mesmo repositório. Não explique TP1 e TP2 em detalhes.

## 2. Arquitetura — 0:30 a 1:15

**Mostrar:** `TP5/diagramas/arquitetura-final.svg` aberto no navegador. Use `TP4/ARQUITETURA.md` apenas se quiser mostrar a fonte Mermaid da evolução orientada a eventos.

Antes de começar, coloque o navegador em tela cheia e ajuste o zoom para que o título, os dois bancos e o bloco **Observabilidade** apareçam ao mesmo tempo.

**Fala base:**

> Da esquerda para a direita, o usuário acessa o frontend React. As linhas verdes representam as APIs REST: o Wallet Service gerencia usuários e carteiras em seu PostgreSQL, enquanto o Transaction Service possui outro banco e gerencia lançamentos, histórico e saldo. As linhas laranja mostram os eventos e comandos transportados pelo RabbitMQ. O padrão Outbox protege a publicação, e as falhas seguem a política de retry e dead-letter queue. Na parte inferior está a observabilidade, com métricas no Prometheus, logs no Loki, traces no Tempo e visualização centralizada no Grafana.

Aponte primeiro para o fluxo horizontal **Usuário → Frontend → serviços**, depois para o RabbitMQ e, por último, para o bloco inferior de observabilidade. Não tente seguir todas as setas individualmente.

**Improviso controlado — 10 segundos:** escolha apenas um detalhe do diagrama, como a projeção local das carteiras ou a consistência eventual. Exemplo:

> Isso evita que o Transaction Service dependa de uma consulta síncrona ao Wallet Service para reconhecer uma carteira.

## 3. Docker e estado da plataforma — 1:15 a 1:45

**Mostrar:** terminal.

```bash
docker compose ps
docker images | grep -E 'wallet-platform|REPOSITORY'
```

**Fala base:**

> O Docker Compose reproduz a plataforma localmente. Aqui estão o frontend, os dois microsserviços, os bancos, o RabbitMQ e a pilha de observabilidade. As aplicações possuem health checks, imagens com build em múltiplos estágios e execução com usuário não root.

**Improviso controlado — até 10 segundos:** aponte para dois containers com estado `healthy` e diga os nomes que realmente aparecerem. Não leia a tabela inteira.

## 4. Aplicação funcionando — 1:45 a 3:30

**Mostrar:** `http://localhost:3002`.

A interface prepara automaticamente o usuário e a carteira principal. Faça esta sequência sem trocar a ordem:

### Estado e perfil — 1:45 a 2:05

1. aponte o indicador **Transactions: online** e o seletor **Main Wallet · BRL**;
2. clique no avatar **FD**;
3. mostre rapidamente o painel lateral, o status **Active account**, os dados do usuário e a quantidade de carteiras;
4. feche com `Esc` ou no `×`. Não altere o perfil durante a gravação.

**Fala:**

> A tela recupera o usuário e suas carteiras pelo Wallet Service. O indicador confirma que o Transaction Service está online. O perfil também não é apenas visual: nome e e-mail podem ser atualizados pela API, e o painel informa quantas carteiras estão associadas ao usuário.

### Operações financeiras — 2:05 a 2:40

1. clique em **Receive**;
2. informe `100,00` e a descrição `Depósito da apresentação`;
3. confirme;
4. clique em **Send**;
5. use o destinatário `Loja Demo`, informe `25,00` e deixe a descrição vazia;
6. confirme e mostre o saldo e os cartões atualizados.

**Fala:**

> Vou registrar uma entrada de 100 reais e uma saída de 25 reais para a Loja Demo. Os lançamentos são persistidos pelo Transaction Service, que valida o saldo e recalcula o ledger. Após cada operação, a interface consulta novamente o histórico e o saldo.

Não diga que ocorreu uma transferência bancária real. Nesta entrega, **Send** e **PIX** geram lançamentos de débito identificados; integração financeira externa está fora do escopo.

### Gráfico, notificações e extrato — 2:40 a 3:10

1. altere o gráfico de **Last 30 days** para **Last 7 days**;
2. clique no sino;
3. aponte os valores, datas, ícones de crédito/débito e os pontos de não lido;
4. clique em **Mark all as read** e feche o painel;
5. no extrato, selecione **Debits** e busque `Loja Demo`;
6. aponte **Export CSV**, mas não precisa baixar o arquivo;
7. limpe a busca e retorne o filtro para **All** antes de prosseguir.

**Fala:**

> O gráfico e os totais são calculados com os lançamentos reais e podem ser analisados por período. As novas operações também aparecem no centro de notificações, com tipo, valor, horário e controle de leitura. No extrato eu consigo filtrar, pesquisar e exportar exatamente o conjunto exibido.

**Improviso controlado — até 10 segundos:** diga o saldo anterior e o novo saldo que realmente aparecerem. Não prometa saldo de `75,00`, pois a carteira já possui operações anteriores.

### Smoke test rastreável — 3:10 a 3:30

Agora gere uma operação rastreável:

```bash
BASE_URL=http://localhost:3002 ./scripts/smoke-test.sh
```

**Fala curta enquanto executa:**

> O smoke test valida saúde, criação de usuário e carteira, propagação do evento e criação de transação. Vou guardar o correlation ID exibido no final para localizar esta execução no monitoramento.

Copie o `correlationId` impresso.

## 5. RabbitMQ — 3:30 a 4:00

**Mostrar:** `http://localhost:15672`, em **Queues and Streams**.

**Fala base:**

> O RabbitMQ desacopla os microsserviços. Nesta tela aparecem as filas e os consumidores ativos. A criação da carteira gera um evento consumido pelo Transaction Service. Falhas técnicas recebem novas tentativas e, após o limite, seguem para a dead-letter queue. A idempotência impede que uma reentrega produza a mesma operação duas vezes.

**Improviso controlado — 10 segundos:** diga quantas mensagens e consumidores aparecem em uma fila real. Se estiver zerada:

> A fila está sem mensagens pendentes porque o consumidor já processou o evento.

Não provoque uma falha ao vivo; retry e DLQ são comprovados pelos testes de integração.

## 6. Grafana — 4:00 a 6:15

**Mostrar:** **Dashboards > Wallet Platform > Wallet Platform — Operação** em `http://localhost:3001`.

### Visão geral — 4:00 a 4:40

**Fala base:**

> O Grafana foi provisionado automaticamente com Prometheus, Loki e Tempo. Os três cartões confirmam a disponibilidade do Wallet Service, do Transaction Service e do RabbitMQ. Os demais painéis mostram tráfego, requisições por status, erros 5xx, latência p95, filas, heap e CPU.

**Improviso controlado — 15 segundos:** escolha dois valores visíveis, por exemplo uma latência ou o consumo de heap. Explique o significado, sem tentar ler todos os gráficos.

### Logs no Loki — 4:40 a 5:25

Aponte os painéis de logs. Depois abra **Explore** pelo ícone de bússola e selecione **Loki** no seletor de fonte de dados.

Na tela **A (Loki)** que contém **Label filters** e **Operations**, configure:

1. no seletor de tempo, no canto superior direito, escolha **Last 15 minutes**;
2. mantenha **Type: Range** — não use **Live** durante a apresentação;
3. em **Label filters**, selecione o label `service`;
4. mantenha o operador `=`;
5. em **Select value**, escolha `backend`;
6. em **Operations**, escolha **Line contains**;
7. cole apenas o `correlationId` gerado pelo smoke test, por exemplo `smoke-...`;
8. clique em **Run query**.

O Builder deve montar uma consulta equivalente a:

```logql
{service="backend"} |= "smoke-COLE-O-ID-AQUI"
```

Expanda uma das linhas retornadas para mostrar os campos do log. Depois, se houver tempo, troque o valor de `service` para `transaction-service` e execute novamente. Isso demonstra o mesmo identificador chegando aos dois microsserviços.

Como alternativa, no modo **Code**, consulte os dois serviços de uma vez:

```logql
{service=~"backend|transaction-service"} |= "smoke-COLE-O-ID-AQUI"
```

**Fala base:**

> O Promtail coleta os logs dos containers e os envia ao Loki. Como cada requisição recebe um correlation ID, consigo encontrar os registros da operação sem acessar cada container individualmente. Os logs estruturados também carregam aplicação, nível, trace ID e span ID.

**Improviso controlado — 10 segundos:** abra um resultado e explique o método, rota, status ou duração que estiver visível.

Se não aparecer nenhum resultado, primeiro aumente o período para **Last 1 hour**. Depois confira se foi colado somente o valor `smoke-...`, sem a palavra `correlationId` e sem pontuação do terminal. Não use **Add query**, **Query history** nem **Query inspector** nesta demonstração.

### Trace no Tempo — 5:25 a 6:15

Em **Explore**, selecione **Tempo**, pesquise traces recentes de `wallet-service` ou `transaction-service` e abra um resultado.

**Fala base:**

> O Tempo recebe os spans gerados pelo Micrometer Tracing. Aqui consigo visualizar a duração e a sequência das operações instrumentadas. As métricas ajudam a perceber um problema, o trace mostra onde o tempo foi gasto e os logs ajudam a encontrar a causa.

**Improviso controlado — 10 segundos:** clique em um span e diga o nome e a duração mostrados. Se aparecer **Related logs**, abra-o e mostre a ligação entre trace e logs.

Se o trace não aparecer, não gaste mais de 15 segundos tentando. Mostre os logs, diga que o Tempo está configurado como fonte de traces e prossiga.

## 7. Kubernetes — 6:15 a 7:10

### Se houver cluster ativo

```bash
kubectl get deploy,pods,svc,hpa,ingress,pvc -n wallet-platform
```
Aqui a plataforma está implantada no Kubernetes. Os microsserviços possuem duas réplicas disponíveis, e todos os pods estão em execução. O HPA monitora a utilização de CPU e pode escalar o backend e o serviço de transações de duas até cinco réplicas. O Ingress expõe a aplicação pelo host wallet.local, e os bancos e o RabbitMQ utilizam volumes persistentes em estado Bound.

## 8. Git, testes e CI/CD — 7:10 a 8:40

**Mostrar primeiro:** terminal.

```bash
git log --oneline --decorate -8
```

**Fala base:**

> O projeto e sua documentação são versionados com Git e publicados no GitHub. Os commits registram a evolução da arquitetura, da conteinerização, do monitoramento e do pipeline.

**Improviso controlado — 10 segundos:** escolha um commit relacionado ao TP4 ou TP5 e explique o objetivo dele.

**Mostrar depois:** execução mais recente em **GitHub Actions**.

**Fala base:**

> O workflow executa os testes Java e frontend, gera cobertura JaCoCo, valida o Docker Compose e os manifestos com Kubeconform. Depois constrói as três imagens em paralelo, verifica vulnerabilidades altas e críticas com Trivy e publica as imagens aprovadas no GHCR. O deploy é manual e protegido pelo ambiente de produção.

Abra os jobs `test` e `images`. Mostre o status real da execução.

**Improviso controlado — 15 segundos:**

- se estiver verde, destaque duas etapas concluídas;
- se estiver executando, diga exatamente qual job está em andamento;
- se houver falha, abra a etapa e explique objetivamente o erro.

Não diga que o pipeline está concluído se a execução estiver vermelha.

Para mencionar a cobertura sem esperar testes ao vivo:

> Além do pipeline, o projeto possui testes de unidade, persistência, API, mensageria real com RabbitMQ, frontend e o smoke test integrado que acabei de executar.

## 9. Encerramento — 8:40 a 9:00

**Mostrar:** dashboard do Grafana com os serviços online.

**Fala base:**

> A Wallet Platform evoluiu para microsserviços integrados por eventos e preparados para operação. A entrega reúne Docker, Kubernetes, escalabilidade, testes, CI/CD e observabilidade por métricas, logs e traces. Isso conclui a apresentação dos TPs 3, 4 e 5. Obrigado.

## Onde improvisar e onde não improvisar

Use improviso para comentar:

- valores de saldo e transações que aparecem na interface;
- nomes e estados dos containers;
- número de consumidores e mensagens nas filas;
- valores atuais de latência, CPU e memória;
- conteúdo de um log ou span;
- status verdadeiro do GitHub Actions.

Mantenha a fala preparada ao explicar:

- responsabilidade de cada microsserviço;
- Outbox, retry, DLQ e idempotência;
- diferença entre Prometheus, Loki e Tempo;
- escalabilidade e probes do Kubernetes;
- etapas do CI/CD.

A regra prática é: **explique o conceito com a fala base e improvise apenas sobre a evidência visível**.

## Plano de corte se passar de 10 minutos

Corte nesta ordem:

1. não mostre `docker images`;
2. no perfil, mostre apenas o cabeçalho com usuário e status, sem comentar cada campo;
3. aponte o botão de CSV sem clicar e use somente o filtro **Debits**;
4. demonstre somente `Receive`, sem executar `Send`;
5. no RabbitMQ, mostre apenas uma fila;
6. no Grafana, escolha heap ou CPU, não os dois;
7. não abra um commit individual;
8. mostre o resultado dos testes no Actions, sem executá-los localmente.

Não corte a aplicação funcionando, o dashboard do Grafana, o Kubernetes nem o GitHub Actions, pois são as evidências centrais da entrega.

## Checklist da gravação

- [ ] Aplicação, Grafana, RabbitMQ e GitHub já estão abertos.
- [ ] Containers e health checks estão ativos.
- [ ] Filtro do extrato está em **All**, busca vazia e notificações antigas estão marcadas como lidas.
- [ ] Perfil mostra **Fernando Dev** e a carteira possui pelo menos `R$ 25,00` disponíveis.
- [ ] O smoke test funciona e o `correlationId` é copiado.
- [ ] Métricas, logs e pelo menos um trace estão visíveis.
- [ ] A opção de Kubernetes escolhida foi ensaiada.
- [ ] A execução mais recente do Actions está aberta.
- [ ] Git e documentação são mencionados.
- [ ] Nenhum token, Secret ou kubeconfig aparece.
- [ ] A gravação fica entre 7 e 10 minutos.
- [ ] O link do vídeo é adicionado aos READMEs depois da publicação.

## Comandos de emergência

```bash
# Estado geral
docker compose ps

# Gerar nova operação rastreável
BASE_URL=http://localhost:3002 ./scripts/smoke-test.sh

# Consultar filas pelo terminal
docker compose exec rabbitmq rabbitmqctl list_queues name messages consumers

# Reiniciar apenas o Grafana
docker compose restart grafana

# Consultar logs recentes
docker compose logs --tail=50 backend transaction-service
```

Após a gravação, publique o vídeo em um local acessível ao avaliador e substitua o marcador em `TP4/README.md` e `TP5/README.md`. Não é necessário gerar PDF.
