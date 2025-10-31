# Sistema de viagens tolerante a falha

## Visão Geral

## Características Principais


## Arquitetura

### Serviços


## Execução do Sistema

Para execução do projeto em ambiente Docker, executar:

```bash
# Na raiz do projeto
docker-compose up --build
```

## Testes

Para testar o sistema após a inicialização:

```bash
curl -i -X POST "http://localhost:8081/buyTicket?flight=25222&day=2025-15-30&user=3"
```
