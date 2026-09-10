# Guia Completo Passo a Passo: Publicação do BloqFone no Google Play Console

Este documento contém o passo a passo completo, estruturado e com os textos prontos para copiar e colar para publicar o aplicativo **BloqFone** na **Google Play Store**.

---

## 📋 Índice
1. [Pré-requisitos e Arquivos Prontos](#1-pré-requisitos-e-arquivos-prontos)
2. [Passo 1: Criar o Aplicativo no Console](#passo-1-criar-o-aplicativo-no-console)
3. [Passo 2: Ficha Principal da Loja (Store Listing)](#passo-2-ficha-principal-da-loja-store-listing)
4. [Passo 3: Conteúdo do App (Políticas e Declarações)](#passo-3-conteúdo-do-app-políticas-e-declarações)
   - [A. Política de Privacidade](#a-política-de-privacidade)
   - [B. Acesso ao App](#b-acesso-ao-app)
   - [C. Anúncios](#c-anúncios)
   - [D. Classificação de Conteúdo (Questionário IARC)](#d-classificação-de-conteúdo-questionário-iarc)
   - [E. Público-Alvo e Conteúdo Infantil](#e-público-alvo-e-conteúdo-infantil)
   - [F. Apps de Notícias / COVID-19 / Financeiro](#f-apps-de-notícias--covid-19--financeiro)
   - [G. Segurança dos Dados (Data Safety)](#g-segurança-dos-dados-data-safety)
   - [H. Permissões Sensíveis e Declaração de Triagem de Chamadas](#h-permissões-sensíveis-e-declaração-de-triagem-de-chamadas)
5. [Passo 4: Recursos Gráficos (Imagens e Screenshots)](#passo-4-recursos-gráficos-imagens-e-screenshots)
6. [Passo 5: Criar e Enviar o Lançamento (Release)](#passo-5-criar-e-enviar-o-lançamento-release)
7. [Observação sobre Contas Pessoais Novas do Google Play](#7-observação-sobre-contas-pessoais-novas-do-google-play)

---

## 1. Pré-requisitos e Arquivos Prontos

- **Arquivo do Aplicativo (.aab):**  
  O Google Play exige o formato Android App Bundle (`.aab`). O arquivo de produção mais recente compilado está localizado na raiz do projeto:  
  `BloqFone-v1.0.1.aab` (versão 1.0.1, versionCode 2)
- **URL da Política de Privacidade:**  
  - **Opção 1 (GitHub Pages - Recomendado):** `https://ciceroregis.github.io/BloqFone/` (após ativar o GitHub Pages na pasta `/docs`)
  - **Opção 2 (Markdown direto):** `https://github.com/ciceroregis/BloqFone/blob/main/PRIVACY_POLICY.md`
- **E-mail de suporte:** `ciceroregis25@gmail.com`
- **Site oficial/repositório:** `https://github.com/ciceroregis/BloqFone`

---

## Passo 1: Criar o Aplicativo no Console

1. Acesse o [Google Play Console](https://play.google.com/console).
2. Clique no botão **Criar app** (canto superior direito).
3. Preencha os campos iniciais:
   - **Nome do app:** `BloqFone: Bloqueador de Spam` *(29 caracteres de 30 permitidos)*
   - **Idioma padrão:** `Português (Brasil) – pt-BR`
   - **App ou jogo:** `App`
   - **Gratuito ou pago:** `Gratuito`
4. **Declarações obrigatórias:**
   - [x] Confirmo que este app obedece às leis de exportação dos Estados Unidos.
   - [x] Aceito as Políticas do Programa para Desenvolvedores.
5. Clique em **Criar app**.

---

## Passo 2: Ficha Principal da Loja (Store Listing)

No menu lateral esquerdo, vá em **Crescimento** > **Presença na loja** > **Ficha principal da loja**.

### Detalhes do Aplicativo

#### Nome do app (máx. 30 caracteres):
```text
BloqFone: Bloqueador de Spam
```

#### Breve descrição (máx. 80 caracteres):
```text
Bloqueie chamadas de telemarketing, spam e golpes. 100% seguro, local e offline.
```

#### Descrição completa (máx. 4.000 caracteres):
```text
Livre-se de ligações indesejadas, chamadas robô e telemarketing abusivo com o BloqFone.

O BloqFone é um bloqueador e triador inteligente de chamadas que atua diretamente no seu celular com um diferencial essencial: FUNCIONA 100% OFFLINE E COM TOTAL PRIVACIDADE. Nenhuma informação de chamada, número ou contato é enviada para a internet ou servidores externos.

🛡️ RECURSOS E PROTEÇÃO:
• Bloqueio oficial de Telemarketing: Bloqueia automaticamente chamadas com prefixo 0303 regulamentado pela Anatel.
• Bloqueio de Ligações Robô (Robocalls): Identifica e barra padrões repetitivos utilizados por discadores automáticos em massa.
• Bloqueio de Suspeita de Spam: Bloqueia centrais 3003, chamadas a cobrar (9090) ou finais suspeitos (0000).
• Bloqueio de Chamadas Internacionais: Evite tentativas de fraudes e golpes vindos de fora do Brasil (+DDI).
• Bloqueio de Números Privados e Ocultos: Barre chamadas sem identificação ou confidenciais.
• Modo Foco (Somente Agenda): Permite receber apenas chamadas de contatos salvos na sua agenda, barrando qualquer outro número desconhecido.

📋 GERENCIAMENTO DE LISTAS:
• Lista Negra Pessoal: Adicione manualmente números que você deseja bloquear permanentemente.
• Lista Branca de Exceções: Garanta que números importantes sempre toquem, ignorando as regras de bloqueio.

📊 RELATÓRIO E TRANSPARÊNCIA:
• Histórico detalhado de todas as chamadas barradas pelo app com data, hora e motivo exato do bloqueio.
• Notificações imediatas no sistema sempre que uma chamada for bloqueada.
• Opção de limpar o histórico a qualquer momento.

🔒 100% SEGURO E PRIVADO:
• Não requer criação de conta ou cadastro.
• Não tem anúncios e não rastreia seus hábitos.
• Todo o processamento é feito localmente no seu aparelho através das APIs nativas do Android.
```

### Configurações da Ficha da Loja (Categoria e Contato)
- **Tipo de aplicativo:** `Aplicativo`
- **Categoria:** `Ferramentas` (ou `Comunicação`)
- **Tags / Marcadores:** `Bloqueador de chamadas`, `Ferramentas`, `Privacidade`, `Produtividade`
- **Endereço de e-mail de contato:** `ciceroregis25@gmail.com`
- **Site:** `https://github.com/ciceroregis/BloqFone`

---

## Passo 3: Conteúdo do App (Políticas e Declarações)

No menu lateral esquerdo, role até a seção **Política** e clique em **Conteúdo do app**. Preencha cada um dos tópicos obrigatórios:

### A. Política de Privacidade
- **URL da Política de Privacidade:**
  ```text
  https://github.com/ciceroregis/BloqFone/blob/main/PRIVACY_POLICY.md
  ```
- Clique em **Salvar**.

### B. Acesso ao App
- Selecione: **Todas as funcionalidades estão disponíveis sem restrições especiais**
- *(O app não requer login, senha, assinatura nem credenciais de teste).*
- Clique em **Salvar**.

### C. Anúncios
- Selecione: **Não, meu app não contém anúncios**
- Clique em **Salvar**.

### C.1. ID de Publicidade (Advertising ID / AD_ID)
- Pergunta: *O app usa um ID de publicidade?*
- Selecione: **Não**
- *(O app não usa AdMob, Firebase Analytics, nem qualquer SDK de anúncios ou rastreamento, e não possui a permissão `AD_ID` nem permissão de Internet).*
- Clique em **Salvar**.

### D. Classificação de Conteúdo (Questionário IARC)
1. Clique em **Iniciar questionário**.
2. **E-mail:** `ciceroregis25@gmail.com`
3. **Categoria:** Selecione `Utilitários, Produtividade, Comunicação ou Outros`.
4. Responda às perguntas:
   - O app compartilha a localização física com terceiros? **Não**
   - O app permite que os usuários comprem bens digitais? **Não**
   - O app contém sangue, violência ou mutilação? **Não**
   - O app contém material de natureza sexual ou nudez? **Não**
   - O app contém linguagem ofensiva ou palavrões? **Não**
   - O app promove ou faz referência a drogas, álcool ou tabaco? **Não**
   - O app incentiva apostas ou jogos de azar? **Não**
   - O app permite aos usuários se comunicarem diretamente via texto ou voz? **Não**
5. **Resultado esperado:** Classificação **Livre / Livre para todas as idades (Everyone / PEGI 3)**.
6. Clique em **Salvar** e **Enviar**.

### E. Público-Alvo e Conteúdo Infantil
- **Faixa etária:** Selecione **18 anos ou mais** *(recomendado para evitar exigências burocráticas das políticas de Crianças/Família)*.
- **Apelo para crianças:** O app atrai crianças intencionalmente? Selecione **Não**.
- Clique em **Salvar**.

### F. Apps de Notícias / COVID-19 / Financeiro
- **App de notícias?** Selecione **Não**.
- **Rastreamento de contatos ou status de COVID-19?** Selecione **Não**.
- **Recursos financeiros / empréstimos?** Selecione **Não**.
- **App governamental?** Selecione **Não**.

### G. Segurança dos Dados (Data Safety)
Esta é a seção mais importante para aprovação do app:

1. **O app coleta ou compartilha algum dos tipos de dados de usuários obrigatórios?**
   - Resposta: **NÃO**.
2. **Justificativa / Como os dados são tratados:**
   - O BloqFone opera **100% offline**.
   - O número de telefone de chamadas recebidas e a lista de contatos do aparelho são processados **exclusivamente na memória volátil do próprio dispositivo** para a realização da triagem.
   - Nenhum dado é salvo fora do aparelho, transmitido pela internet ou compartilhado com terceiros.
3. Clique em **Avançar** e **Salvar**.

### H. Permissões Sensíveis e Declaração de Triagem de Chamadas

Quando solicitado a justificar as permissões e serviços de sistema do app:

#### 1. Papel de Triagem de Chamadas (`CallScreeningService` / `ROLE_CALL_SCREENING`)
- **Funcionalidade Principal (Core Feature):** Identificação e bloqueio proativo de chamadas indesejadas, spam e telemarketing.
- **Justificativa para o revisor do Google:**
  ```text
  The app's primary and sole functionality is to screen and block unwanted incoming spam calls, robocalls, and abusive telemarketing. The CallScreeningService API is strictly necessary to intercept incoming phone numbers in real time, evaluate them locally against user-defined rules and lists, and automatically silence or reject malicious calls. All processing is strictly local and offline.
  ```

#### 2. Permissão de Leitura de Contatos (`android.permission.READ_CONTACTS`)
- **Justificativa:**
  ```text
  The READ_CONTACTS permission is used strictly on-device to check if an incoming caller is in the user's saved contacts. This allows the app to implement the "Focus Mode" (only allow calls from contacts) and ensure that known contacts are never accidentally blocked by automated filters. No contact data is stored externally, collected, or transmitted.
  ```

#### 3. Permissão de Notificações (`android.permission.POST_NOTIFICATIONS`)
- **Justificativa:**
  ```text
  Used to notify the user in real time when an incoming call has been screened and blocked, allowing the user to view details in the local blocked calls report.
  ```

---

## Passo 4: Recursos Gráficos (Imagens e Screenshots)

Na aba **Ficha principal da loja**, envie os seguintes arquivos visuais:

1. **Ícone do App:**
   - Tamanho: `512 x 512 pixels`
   - Formato: PNG (32 bits com canal alfa)
   - Tamanho máx.: 1 MB
2. **Recurso Gráfico (Banner / Feature Graphic):**
   - Tamanho: `1024 x 500 pixels`
   - Formato: JPG ou PNG (24 bits, sem transparência)
   - Tamanho máx.: 15 MB
3. **Capturas de Tela do Smartphone (Screenshots):**
   - Quantidade mínima: `2 capturas` (recomendado: 4 capturas cobrindo Início, Regras, Listas e Relatório).
   - Proporção recomendada: 9:16 (ex: `1080 x 1920` ou `1080 x 2400` pixels).

---

## Passo 5: Criar e Enviar o Lançamento (Release)

1. No menu lateral, acesse **Versão** > **Produção** (ou **Teste fechado** se a sua conta for nova).
2. Clique no botão **Criar novo lançamento** (canto superior direito).
3. Na seção **Pacotes de apps**:
   - Faça o upload do arquivo: `BloqFone-v1.0.0.aab`
4. **Nome da versão:** `1.0.0`
5. **Notas da versão (Release Notes - pt-BR):**
   ```text
   Lançamento inicial do BloqFone 1.0.0:
   - Bloqueio inteligente de chamadas de telemarketing (prefixo oficial 0303)
   - Filtro de robocalls com padrões de discagem em massa
   - Bloqueio de suspeitas de spam (centrais 3003, ligações a cobrar 9090)
   - Modo Foco: opção para atender apenas chamadas de contatos salvos na agenda
   - Gerenciamento de Lista Negra e Lista Branca personalizadas
   - Relatório em tempo real de chamadas barradas
   - Notificações de bloqueio instantâneas
   - Privacidade total: funcionamento 100% local e offline
   ```
6. Clique em **Salvar**.
7. Clique em **Revisar lançamento**.
8. Se não houver erros impeditivos, clique em **Iniciar lançamento para Produção**.

---

## 7. Observação sobre Contas Pessoais Novas do Google Play

Se a sua conta de desenvolvedor do Google Play Console for uma conta pessoal criada após **novembro de 2023**, o Google exige uma etapa preliminar antes do lançamento em produção:
- **Teste Fechado com 20 testadores por 14 dias:** Você precisará criar uma faixa de Teste Fechado com pelo menos 20 pessoas inscritas que mantenham o app instalado por 14 dias consecutivos antes de solicitar o acesso para publicação em produção.
- Caso sua conta seja de **Organização (Pessoa Jurídica)** ou anterior a nov/2023, o lançamento pode ser enviado diretamente para Produção.
