# Sistema de viagens tolerante a falha

## Visão Geral

Este projeto implementa um sistema distribuído de compra de passagens, desenvolvido com foco em tolerância a falhas. A solução adota uma arquitetura de microsserviços, na qual os componentes se comunicam por meio de serviços REST.

Link para apresentação (Áudio corrigido: [https://www.youtube.com/watch?v=WIYacFB7lfI](https://www.youtube.com/watch?v=4MWWi_Ir0Is)

## Características Principais

- Arquitetura de microsserviços desenvolvida com Spring Boot.
- Comunicação entre serviços por meio de APIs REST.
- Containerização e orquestração utilizando Docker.

## Integrantes

- Alexandre Dantas dos Santos
- Antonio Higino Bisneto Leite Medeiros
- Ignacio Saglio Rossini

## Arquitetura

### Características

Ao executar uma consulta de voo, o sistema retornará o status code 204 (No Content) quando o voo solicitado não for encontrado. Os voos válidos são gerenciados pela classe FlightService, que os mantém armazenados em estruturas de dados internas do projeto.

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
     - Parâmetros (para ambos os endpoints): 
       - flight: número do voo a ser comprado
       - day: data do voo a ser comprado  
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

### Falhas

As falhas podem acontecer em cada uma das requisições da
seguinte forma (Baseado na estrutura: Tipo, Probabilidade, Duração):

- Request 1: Fail (Omission, 0.2, 0s)
- Request 2: Fail (Error, 0.1, 5s)
- Request 3: Fail (Time=5s, 0.1, 10s)
- Request 4: Fail (Crash, 0.02, _ )

## Mecanismos de Tolerância a falhas

### Request 1 /flight (AirlinesHub)

- Timeout + Retry + Fallback com cache local.

### Request 2 /convert (Exchange)

- Fallback com média dos 10 últimos valores + valor padrão.

### Request 3 /sell (AirlinesHub)

- Timeout curto + falha graciosa.

### Request 1 /bonus (Fidelity)

- Fallback com fila + retry agendado.

## Execução do Sistema

Para execução do projeto em ambiente Docker, executar:

```bash
# Na raiz do projeto
docker-compose up --build
```

## Testes

Para testar o sistema após a inicialização:

```bash
# Teste com tolerância a falhas
curl -i -X POST "http://localhost:8081/buyTicket?flight=1001&day=2025-12-01&user=3&ft=true"
```

```bash
# Teste sem tolerância a falhas
curl -i -X POST "http://localhost:8081/buyTicket?flight=1001&day=2025-12-01&user=3&ft=false"
```
