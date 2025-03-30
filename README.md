# Projeto de Gestão Bancária

**Descrição:**  
Este é um sistema de gestão bancária desenvolvido com **Spring Boot**, utilizando uma arquitetura modular e transações seguras. A API permite realizar operações como criação de contas, depósitos, saques e transferências via Pix.

## Tecnologias Utilizadas
- **Java 21**
- **Spring Boot**
- **Maven**
- **Banco de Dados** (Definir: MySQL, PostgreSQL, H2, etc.)
- **Swagger (OpenAPI) para documentação**
- **Docker** para containerização

---

## Configuração e Instalação

### 🔹 **Pré-requisitos**
Antes de começar, instale os seguintes itens:
- [Java 21+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Docker](https://www.docker.com/get-started)

---

### **1. Clonar o Repositório**
```sh
git clone https://github.com/IgorEdu/bank-management.git
cd seu-repositorio
```

### **2. Configurar o Banco de Dados**

O projeto utiliza **variáveis de ambiente** para definir as configurações do banco de dados. Isso permite maior flexibilidade e segurança ao evitar expor credenciais no código.

#### 🔹 **Definição das Variáveis de Ambiente**
Antes de iniciar o projeto, defina as variáveis de ambiente necessárias, informando banco de dados, usuário e senha:

```sh
export DB_URL=jdbc:postgresql://localhost:5432/ngbilling
export DB_USER=postgres
export DB_PASSWORD=postgresql
```

No **Windows (cmd.exe)**:
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/ngbilling
set DB_USER=postgres
set DB_PASSWORD=postgresql
```

No **Windows (PowerShell)**:
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/ngbilling"
$env:DB_USER="postgres"
$env:DB_PASSWORD="postgresql"
```

---

### **3. Construir a Aplicação**
Para compilar e gerar o JAR:
```sh
mvn clean package
```

---

### **4. Criar a Imagem Docker com o Script `build.sh`**
Se preferir automatizar o processo de criação da imagem Docker, utilize o script `build.sh`. Este script irá compilar o projeto e gerar a imagem Docker.

#### **4.1 Executar o Script `build.sh`**
Antes de rodar o script, **certifique-se de que o arquivo `build.sh` tem permissões de execução**:
```sh
chmod +x build.sh
```

Agora, execute o script para compilar e criar a imagem Docker:
```sh
./build.sh
```

O script irá realizar os seguintes passos:
1. **Compilar o projeto** com `mvn clean package`.
2. **Criar a imagem Docker** com o nome `ngbilling-gestao-bancaria:latest`.

---

### **5. Rodar com Docker**

#### **5.1 Construir a Imagem Docker Manualmente**
Caso queira construir manualmente a imagem Docker, use o seguinte comando:
```sh
docker build -t ngbilling-gestao-bancaria .
```

#### **5.2 Executar o Container**
Após criar a imagem, você pode rodar o container:
```sh
docker run -p 8080:8080 ngbilling-gestao-bancaria
```

A API estará disponível em:  
**`http://localhost:8080`**

Caso esteja usando o **Swagger**, acesse:  
**`http://localhost:8080/swagger-ui/index.html`**

---

### **6. Rodar com Docker Compose**

Se preferir rodar o projeto usando **Docker Compose**, siga os seguintes passos:

#### **6.1 Iniciar o Banco e a Aplicação com Docker Compose**
Com o `docker-compose.yml` já configurado, basta rodar:
```sh
docker-compose up --build
```

Esse comando irá:
1. **Construir e iniciar os containers** para o banco de dados e a aplicação.
2. O **banco de dados PostgreSQL** será iniciado e a aplicação Spring Boot também.

A API estará disponível em:  
**`http://localhost:8080`**

---

## **Testando a API**
Após iniciar o serviço, você pode testar as rotas da API usando:
- **Postman**
- **Swagger UI (`http://localhost:8080/swagger-ui/index.html`)**
- **cURL**, exemplo:
  ```sh
  curl -X GET http://localhost:8080/transacao
  ```