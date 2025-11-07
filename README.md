# Sistema de viagens tolerante a falha

## Visão Geral

Este projeto implementa um sistema distribuído de compra de passagens, desenvolvido com foco em tolerância a falhas. A solução adota uma arquitetura de microsserviços, na qual os componentes se comunicam por meio de serviços REST.

## Características Principais

- Arquitetura de microsserviços desenvolvida com Spring Boot.
- Comunicação entre serviços por meio de APIs REST.
- Containerização e orquestração utilizando Docker.

## Integrantes

- Alexandre Dantas dos Santos
- Antonio Higino Bisneto Leite Medeiros
- Ignacio Saglio Rossini

## Arquitetura

### Serviços

1. **ImdTravel** (`/buyTicket`)
   - Atua como ponto central de entrada do sistema
   - Coordena e gerencia a comunicação entre os microsserviços
   - Endpoint:
     - POST `/buyTicket`: Processa compra com parâmetros:
       - flight: número do voo a ser comprado
       - day: data do voo a ser comprado  
       - user: id do usuário que está executando a compra
   - Porta: 8081

2. **AirlinesHub** (`/flight`, `/sell`)
   - Gerencia compra do vôo
   - Endpoints:
     - GET `/flight`: Retorna dados do vôo com os parâmetros:
     - POST `/sell`: Processa venda e retorna ID único da transação
   - Porta: 8084

3. **Exchange** (`/convert`)
   - Fornece taxa de conversão de moeda
   - Endpoint:
     - GET `/exchange`: Retorna taxa de conversão (número real positivo)
   - Porta: 8083

4. **Fidelity** (`/bonus`)
   - Gerencia programa de fidelidade
   - Endpoint:
     - POST `/bonus`: Registra pontos de bônus com parâmetros:
       - user: ID do usuário
       - bonus: valor inteiro do bônus
   - Porta: 8082

## Execução do Sistema

Para execução do projeto em ambiente Docker, executar:

```bash
# Na raiz do projeto
docker-compose up --build
```

## Testes

Para testar o sistema após a inicialização:

```bash
curl -i -X POST "http://localhost:8081/buyTicket?flight=1001&day=2025-12-01&user=3"
```
