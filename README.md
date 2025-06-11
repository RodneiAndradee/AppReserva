# AppReserva

[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://github.com/GitRael/AppReserva/blob/main/LICENSE) Um aplicativo móvel Android desenvolvido em Kotlin para gerenciamento de reservas de salas de informática em uma faculdade. O aplicativo permite que professores e administradores agendem e gerenciem o uso das salas, com um fluxo de aprovação/rejeição de agendamentos.

## Sumário

* [Funcionalidades](#funcionalidades)
* [Arquitetura](#arquitetura)
* [Pré-requisitos](#pré-requisitos)
* [Configuração do Ambiente](#configuração-do-ambiente)
* [Instalação e Execução](#instalação-e-execução)
* [Fluxo de Uso](#fluxo-de-uso)
* [Testes](#testes)
* [Contribuição](#contribuição)
* [Licença](#licença)

## Funcionalidades

O AppReserva oferece as seguintes funcionalidades principais:

* **Autenticação de Usuários:**
    * Login com e-mail e senha para diferentes perfis (professor e administrador).
* **Gestão de Salas:**
    * Visualização de um calendário para datas de reserva.
    * Criação de novas solicitações de reserva para salas de informática, especificando data, horário, material e anotações.
* **Painel do Professor:**
    * Visualiza suas próprias solicitações de reserva.
    * Opções de menu: Perfil, Configurações, Sair do APP.
* **Painel do Administrador:**
    * Visualiza e gerencia solicitações de reserva **pendentes**.
    * **Aprova** ou **Rejeita** solicitações de reserva.
    * Acessa uma tela de **histórico** para visualizar todas as reservas (pendentes, aprovadas e rejeitadas).
    * Opções de menu: Perfil, Configurações, Reservas (Histórico), Sair do APP.
* **Feedback Visual:**
    * Snackbars e Toasts para feedback de sucesso/erro nas operações.
    * Indicadores de carregamento (ProgressBar) durante operações assíncronas.

## Arquitetura

O projeto segue a arquitetura **MVVM (Model-View-ViewModel)**, utilizando componentes do Android Architecture Components para garantir um código limpo, testável e de fácil manutenção.

* **`View`**: Activities e layouts XML.
* **`ViewModel`**: Gerencia o estado da UI e a lógica de negócios, interagindo com o Repositório.
* **`Model`**: Classes de dados (ex: `Reservation`, `User`).
* **`Repository`**: Abstrai a fonte de dados (Firebase Firestore e Firebase Authentication).

### Dependências Principais:

* **Kotlin:** Linguagem de programação.
* **AndroidX:** Bibliotecas de suporte e componentes de arquitetura.
* **Firebase Authentication:** Para autenticação de usuários.
* **Firebase Firestore:** Banco de dados NoSQL em nuvem para armazenar reservas e dados de usuários.
* **LiveData:** Observáveis para comunicação entre ViewModel e View.
* **ViewModel:** Ciclo de vida consciente para lógica da UI.
* **RecyclerView:** Para exibir listas de solicitações/reservas.
* **Material Design:** Componentes de UI modernos.
* **JUnit:** Framework para testes unitários.
* **Mockito/Mockito-Kotlin:** Frameworks para criação de mocks em testes unitários.
* **JaCoCo:** Ferramenta para relatórios de cobertura de código.

## Pré-requisitos

Para configurar e executar o projeto, você precisará:

* **Android Studio Dolphin | 2021.3.1** ou superior.
* **Java Development Kit (JDK) 11** ou superior.
* **SDK Android** (versão 35 ou superior).
* Uma conta no **Firebase** e um projeto Firebase configurado.

## Configuração do Ambiente

1.  **Clone o seu Fork:**
    ```bash
    git clone [https://github.com/GitRael/AppReserva.git](https://github.com/GitRael/AppReserva.git)
    cd AppReserva
    ```
2.  **Configure o Firebase:**
    * No [Console do Firebase](https://console.firebase.google.com/), crie um novo projeto (ou use um existente).
    * Adicione um aplicativo Android ao seu projeto Firebase, seguindo as instruções fornecidas pelo Firebase.
    * Quando solicitado, baixe o arquivo `google-services.json`.
    * Coloque o arquivo `google-services.json` dentro do diretório `app/` do seu projeto AppReserva (ex: `AppReserva/app/google-services.json`).
    * Habilite a autenticação por **E-mail/senha** no Firebase Console (`Build > Authentication > Sign-in method`).
    * Habilite o **Firestore Database** (`Build > Firestore Database`). Inicie no modo de produção ou de teste, conforme sua necessidade.
3.  **Configurações do Gradle:**
    * Verifique seus arquivos `build.gradle.kts` (nível do projeto e do módulo `app`) e `settings.gradle.kts` para garantir que as versões do Gradle e do Android Gradle Plugin (AGP) estão corretas e que os plugins do Firebase e JaCoCo estão aplicados.
    * O projeto usa **Gradle 8.1.1** e **AGP 8.x.x**. Se seu Android Studio sugerir uma atualização, siga o assistente.
4.  **Configurar Exclusões do Antivírus (Windows Defender):**
    * Adicione as pastas `C:\Users\seu_usuario\.gradle` e `C:\Users\seu_usuario\OneDrive - Inmetrics\Área de Trabalho\AppReserva` às exclusões do seu Microsoft Defender (ou outro antivírus) para evitar problemas de sincronização do Gradle.

## Instalação e Execução

1.  **Abra o Projeto no Android Studio:**
    * No Android Studio, vá em `File > Open` e selecione a pasta raiz do projeto `AppReserva`.
2.  **Sincronize o Gradle:**
    * Aguarde a sincronização inicial do Gradle. Se houver falhas, verifique a janela `Build Output` para detalhes.
    * Faça `Build > Clean Project` e `Build > Rebuild Project`.
3.  **Crie um Emulador (AVD) ou Conecte um Dispositivo Físico:**
    * No Android Studio, vá em `Tools > Device Manager` para criar ou gerenciar emuladores.
4.  **Execute o Aplicativo:**
    * Clique no botão verde **"Run App"** (o ícone de play) na barra de ferramentas do Android Studio.

## Fluxo de Uso

1.  **Login:**
    * Crie usuários no Firebase Authentication ou use as credenciais de teste fornecidas pela equipe.
    * Faça login na tela principal do aplicativo.
2.  **Tela do Professor (Calendário):**
    * Visualize o calendário e suas reservas.
    * Clique no botão flutuante `+` para criar uma nova reserva.
    * Use o menu no canto superior esquerdo para `Sair do APP`.
3.  **Tela de Reserva:**
    * Preencha os detalhes da sala, horário, material e anotações.
    * O professor é preenchido automaticamente com o e-mail do usuário logado.
    * Salve a reserva, que será enviada para aprovação.
4.  **Tela do Administrador:**
    * Faça login com uma conta de administrador (com permissão de leitura/escrita no Firestore).
    * Visualize as solicitações de reserva pendentes.
    * Use os botões `Aprovar` ou `Rejeitar` para gerenciar as solicitações.
    * Clique no menu no canto superior esquerdo para acessar `Reservas` (histórico completo), `Perfil`, `Configurações` ou `Sair do APP`.

## Testes

O projeto inclui testes unitários para garantir a qualidade do código e a funcionalidade das principais features.

* **Testes Unitários:** Localizados em `app/src/test/java/...`.
    * Para `ViewModels`, `Repositories` e `Models`.
    * Utiliza JUnit, Mockito e `core-testing`.
* **Cobertura de Código (JaCoCo):** Configurado para gerar relatórios de cobertura.

### Executando os Testes e Gerando Relatório de Cobertura:

1.  **Executar Testes Unitários:**
    ```bash
    ./gradlew testDebugUnitTest
    ```
2.  **Gerar Relatório de Cobertura (JaCoCo):**
    ```bash
    ./gradlew jacocoTestReport
    ```
    * O relatório HTML estará disponível em: `app/build/reports/jacoco/jacocoTestReport/html/index.html`

## Contribuição

Para contribuir com o projeto, siga o fluxo de trabalho de [Fork & Pull Request](https://docs.github.com/pt/pull-requests/collaborating-with-pull-requests/proposing-changes-with-pull-requests/creating-a-pull-request-from-a-fork):

1.  Faça um **Fork** do repositório original do seu colega (`RodneiAndradee/AppReserva`).
2.  **Clone** o seu Fork para o seu ambiente local.
3.  Crie uma nova **Branch de Feature** (`git checkout -b feature/minha-nova-funcionalidade`).
4.  Faça suas **alterações e commits** de forma concisa e clara.
5.  **Envie** suas alterações para o seu Fork (`git push origin feature/minha-nova-funcionalidade`).
6.  Abra um **Pull Request** do seu Fork para o repositório original.

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes. ```
