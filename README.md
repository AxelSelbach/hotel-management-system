# Hotel Meia Boca Juniors - Sistema de Gerenciamento de Hotel

Sistema de gerenciamento de hotel desenvolvido em Java com JavaFX, PostgreSQL e JDBC. Tecnologias Utilizadas

## Linguagem e Técnologias

 - Java 21
 - Interface: JavaFX + FXML + Scene Builder
 - Banco de Dados: PostgreSQL Padrões: MVC + DAO
 - Build: Maven

# Funcionalidades: Módulos Principais
#### Hóspedes: 
- Cadastro
- Edição 
- Exclusão
- Busca por CPF

#### Quartos:
- Cadastro
- Edição 
- Exclusão 
- Filtros por tipo (Status)
- Status automático (Available/Occupied)

#### Reservas:
- Criação de reservas 
- Check-In 
- Check-Out 
- Cancelamento de reservas 
- Cálculo automático de valor total

### Dashboard Gráfico de ocupação dos quartos
- Resumo de estatísticas

### Login 
- Autenticação de usuários

## Como Executar
#### Pré-requisitos
- Java 21 
- PostgreSQL 
- Maven

#### Configuração do Banco

Crie o banco hotel_db Execute o script SQL (disponível na pasta database/)
