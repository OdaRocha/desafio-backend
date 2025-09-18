# Desafio Backend - Requisitos

## 1. Validações

Você deve ajustar as entidades (model e sql) de acordo com as regras abaixo: 

- `Product.name` é obrigatório, não pode ser vazio e deve ter no máximo 100 caracteres.
- `Product.description` é opcional e pode ter no máximo 255 caracteres.
- `Product.price` é obrigatório deve ser > 0.
- `Product.status` é obrigatório.
- `Product.category` é obrigatório.
- `Category.name` deve ter no máximo 100 caracteres.
- `Category.description` é opcional e pode ter no máximo 255 caracteres.

## 2. Otimização de Performance
- Analisar consultas para identificar possíveis gargalos.
- Utilizar índices e restrições de unicidade quando necessário.
- Implementar paginação nos endpoints para garantir a escala conforme o volume de dados crescer.
- Utilizar cache com `Redis` para o endpoint `/auth/context`, garantindo que a invalidação seja feita em caso de alteração dos dados.

## 3. Logging
- Registrar logs em arquivos utilizando um formato estruturado (ex.: JSON).
- Implementar níveis de log: DEBUG, INFO, WARNING, ERROR, CRITICAL.
- Utilizar logging assíncrono.
- Definir estratégias de retenção e compressão dos logs.

## 4. Refatoração
- Atualizar a entidade `Product`:
  - Alterar o atributo `code` para o tipo inteiro. (Entendo que eh obrigatorio a troca da tipagem)
- Versionamento da API:
  - Manter o endpoint atual (v1) em `/api/products` com os códigos iniciados por `PROD-`.
  - Criar uma nova versão (v2) em `/api/v2/products` onde `code` é inteiro.

## 5. Integração com Swagger
- Documentar todos os endpoints com:
  - Descrições detalhadas.
  - Exemplos de JSON para requisições e respostas.
  - Listagem de códigos HTTP e mensagens de erro.

## 6. Autenticação e Gerenciamento de Usuários
- Criar a tabela `users` com as colunas:
  - `id` (chave primária com incremento automático)
  - `name` (obrigatório)
  - `email` (obrigatório, único e com formato válido)
  - `password` (obrigatório)
  - `role` (obrigatório e com valores permitidos: `admin` ou `user`)
- Inserir um usuário admin inicial:
  - Email: `contato@simplesdental.com`
  - Password: `KMbT%5wT*R!46i@@YHqx`
- Endpoints:
  - `POST /auth/login` - Realiza login.
  - `POST /auth/register` - Registra novos usuários (se permitido).
  - `GET /auth/context` - Retorna `id`, `email` e `role` do usuário autenticado.
  - `PUT /users/password` - Atualiza a senha do usuário autenticado.

## 7. Permissões e Controle de Acesso
- Usuários com `role` admin podem criar, alterar, consultar e excluir produtos, categorias e outros usuários.
- Usuários com `role` user podem:
  - Consultar produtos e categorias.
  - Atualizar apenas sua própria senha.
  - Não acessar ou alterar dados de outros usuários.

## 8. Testes
- Desenvolver testes unitários para os módulos de autenticação, autorização e operações CRUD.

---

# Perguntas

1. **Se tivesse a oportunidade de criar o projeto do zero ou refatorar o projeto atual, qual arquitetura você utilizaria e por quê?**
Provavelmente utilizaria arquitetura hexagonal por sua capacidade de isolar o domínio do negócio das dependências externas, facilitando testes e manutenção.
Essa arquitetura cria uma separação lógica de negócio e as interfaces externas, como bancos de dados(postgres), Apis e cache(Redis), o que é ajuda para projetos que podem crescer em complexidade ao longo do tempo.

2. **Qual é a melhor estratégia para garantir a escalabilidade do código mantendo o projeto organizado?**
DDD - Separar codigo em modulos de negocio(ex: modulo de usuario, modulo de produto) e dentro desses modulos separar em camadas (ex: controller, service, repository).
Solid - Utilizar principios SOLID para manter o codigo flexivel e facil de manter.
Design Patterns - Utilizar padroes de projeto como Factory, Singleton, Strategy para resolver problemas comuns de forma padronizada.
Monitoramento - Metricas, Logs Tracing para identificar gargalos de performance e pontos de falha.

3. **Quais estratégias poderiam ser utilizadas para implementar multitenancy no projeto?**
Acredito que a forma mais facil e pratica seria utilizar Banco de dados compartilhando Tenant ID.
 - Criacao de coluna tenant_id nas tabelas

pros: praticidade, facil de implementar
contras: pode gerar problemas de performance com muitos tenants

Outra forma seria utilizar multiplos schemas no banco de dados
    - Criacao de schema para cada tenant

pros: melhor isolamento entre tenants
contras: mais complexo de implementar e gerenciar

Tambem eh possivel realizar a separacao por banco de dados
    - Criacao de banco de dados para cada tenant

pros: melhor isolamento e seguranca
contras: mais complexo e caro de gerenciar  


4. **Como garantir a resiliência e alta disponibilidade da API durante picos de tráfego e falhas de componentes?**
Existem algumas estrategias que poder ajudar nessas situacoes:
- Load Balancing: Distribuir o trafego entre multiplas instancias da aplicacao
- Auto Scaling: Configurar auto scaling para aumentar ou diminuir o numero de requests, CPU e memoria
- Circuit Breaker: Implementar circuit breaker para isolar falhas em componentes externos
- Caching: Utilizar caching para reduzir a carga no banco de dados e melhorar a performance
- Retry Backoff: Implementar retry com backoff exponencial para chamadas a servicos externos
- Monitoramento e Alertas: Configurar monitoramento e alertas para identificar e responder rapidamente

5. **Quais práticas de segurança essenciais você implementaria para prevenir vulnerabiliades como injeção de SQL e XSS?**
- Validacao e Sanitizacao de Inputs, "escapes" de caracteres especiais
- Utilizar ORM para interagir com o banco de dados
- Utilizar JWT para autenticação e autorização
- Utilizar HTTPS para comunicação segura
- Configurar CORS para controlar acesso a API
- Implementar Rate Limiting para prevenir ataques de força bruta
- Utilizar Spring Securitu, validar scopes e roles
- Criptografia de dados sensiveis
- Manter dependencias atualizadas
- Ser criterioso no log para nao correr risco de vazar dados sensiveis

5. **Qual a abordagem mais eficaz para estruturar o tratamento de exceções de negócio, garantindo um fluxo contínuo desde sua ocorrência até o retorno da API?
- Criar exceções customizadas para diferentes tipos de erros de negócio
- Utilizar filtros(ExceptionHandler) globais para capturar exceções
- Mapear exceções para respostas HTTP apropriadas
- Saber utilizar checked e unchecked exceptions para diferenciar erros recuperaveis e nao recuperaveis
- Logar exceções com niveis apropriados (ERROR, INFO)

5. **Considerando uma aplicação composta por múltiplos serviços, quais componentes você considera essenciais para assegurar sua robustez e eficiência?**
- Utilizacao de API Gateway para gerenciar e rotear trafego entre servicos
- Dependendo da complexidade avaliar o uso de Orquestracao de container(EKS) com Service Mesh(Istio) para gerenciar comunicacao entre servicos
- Sempre avaliar utilizacao de fluxos assincronos com filas (SQS, Kafka) para desacoplar servicos e melhorar a escalabilidade
- Monitoramento e Logging centralizado (Datadog, Grafana)
- Utilizacao de Circuit Breaker para isolar falhas em servicos externos
- implementacao de CI/CD para automatizar testes e deploy
- Deploys com Blue/Green ou Canary para minimizar impacto de falhas

6. **Como você estruturaria uma pipeline de CI/CD para automação de testes e deploy, assegurando entregas contínuas e confiáveis?**
CI:
 - build: Compilar o codigo e resolver dependencias
 - test: Executar testes unitarios, de integracao e de contrato
 - Testes E2E: Executar testes end-to-end em ambiente de homologacao

CD:
    - Deploy em homologacao: Automatizar deploy em ambiente de homologacao para testes manuais e validacao
    - Aprovação manual: Implementar etapa de aprovacao manual antes do deploy em producao
    - Deploy em producao: Automatizar deploy em producao com estrategias como Blue/Green ou Canary
    - Monitoramento: Configurar monitoramento e alertas para identificar problemas rapidamente
    - rollback: Implementar estrategia de rollback automatizado em caso de falhas

Tambem vale considerar:
1 - Git Flow para organizacao do codigo.
2 - Gerar alguns thresholds de qualidade como: porcentagem de cobertura de testes e analise de vulnerabilidades de seguranca (Dependabot)

Obs: Forneça apenas respostas textuais; não é necessário implementar as perguntas acima.

