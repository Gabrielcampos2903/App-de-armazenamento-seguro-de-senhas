# SUPERID - Gerenciador de Senhas Pessoais

**SUPERID** é um aplicativo seguro para armazenamento e gerenciamento de senhas pessoais, desenvolvido com foco em usabilidade, desempenho e segurança.

---

## Funcionalidades Principais

- Armazenamento criptografado de senhas pessoais
- Interface intuitiva e responsiva
- Autenticação segura com Firebase Authentication
- Sincronização em tempo real com Cloud Firestore
- Integração entre aplicativo mobile e interface web

---

## Tecnologias Utilizadas

### Aplicativo Mobile

| Tecnologia     | Finalidade                                 |
|----------------|---------------------------------------------|
| Kotlin         | Desenvolvimento nativo Android              |
| Android Studio | Ambiente de desenvolvimento para Android    |
| Firebase Auth  | Autenticação de usuários                    |
| Firestore      | Banco de dados                              |

### Interface Web

| Tecnologia | Finalidade                                               |
|------------|-----------------------------------------------------------|
| HTML       | Estrutura da interface web                                |
| CSS        | Estilização e layout responsivo                           |
| JavaScript | Aplicação de funcionalidades e integração com firebase    |
| Firebase   | Criação do Qrcode                                         |

### Backend (Firebase Functions)

| Tecnologia    | Finalidade                                        |
|---------------|----------------------------------------------------|
| Node.js       | Execução de funções                                |
| TypeScript    | Desenvolvimento das firebade functions             |
| Firebase CLI  | Deploy e gerenciamento das funções                 |

# Configuração das Firebase Functions (Node.js + TypeScript)

##  Pré-requisitos

Antes de iniciar, instale e configure os seguintes itens:

| Requisito       | Descrição                                         |
|-----------------|---------------------------------------------------|
| Node.js         | Versão 16 ou superior ([Site oficial](https://nodejs.org)) |
| Firebase CLI    | Interface de linha de comando do Firebase         |
| Conta Firebase  | Projeto configurado no [Console do Firebase](https://console.firebase.google.com) |

### Instalação da Firebase CLI

```bash
npm install -g firebase-tools
```




Etapas de Configuração
1. Faça login no Firebase
   
```bash
firebase login
```
2. Inicialize o projeto Firebase
Navegue até a raiz do seu projeto e execute:
```bash
firebase init
```

Durante a configuração:

- Selecione Functions
- Escolha seu projeto existente
- Linguagem: TypeScript
- Permita que o Firebase instale as dependências automaticamente

3. Acesse o diretório de funções

```bash
cd functions
```
4. Instale as dependências (caso necessário)

```bash
npm install
```

5. Faça o deploy das funções para o Firebase
```bash
firebase deploy --only functions
```
### Estrutura Esperada do Diretório

```plaintext
functions/
├── src/
│   └── index.ts          # Código-fonte principal das funções
├── lib/                  # Código compilado (JavaScript)
├── package.json          # Gerenciador de dependências e scripts
├── tsconfig.json         # Configurações do TypeScript
└── .eslintrc.json        # Configurações do ESLint (opcional)

