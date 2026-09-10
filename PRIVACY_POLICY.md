# Política de Privacidade / Privacy Policy — BloqFone

**Última atualização / Last updated:** 10 de Setembro de 2026 / September 10, 2026  
**Aplicativo / Application:** BloqFone: Bloqueador de Spam (`br.com.bloqfone`)  
**Desenvolvedor / Developer:** Cicero Regis (<ciceroregis25@gmail.com>)  
**Repositório / Repository:** [https://github.com/ciceroregis/BloqFone](https://github.com/ciceroregis/BloqFone)

---

## 🇧🇷 Português (Brasil)

### 1. Visão Geral e Princípio de Privacidade Local
O **BloqFone** foi concebido e estruturado com base nos princípios de **Privacidade desde a Concepção (Privacy by Design)** e **Minimização de Dados**. O aplicativo opera **100% offline e localmente** no seu dispositivo Android.

- **Não coletamos dados pessoais:** Nenhuma informação é transmitida para servidores externos.
- **Sem servidores remotos:** O aplicativo não possui infraestrutura em nuvem, bancos de dados externos ou APIs de rastreamento.
- **Sem anúncios ou monetização de dados:** O BloqFone não possui anúncios publicitários, redes de afiliados ou SDKs de monetização.

---

### 2. Permissões Sensíveis e Finalidade de Uso

Para desempenhar sua função de bloqueio e triagem de ligações indesejadas, o BloqFone requer acesso a permissões específicas do sistema operacional Android:

#### a) Serviço de Triagem de Chamadas (`CallScreeningService` / Papel de Triador de Chamadas)
- **Finalidade:** Permitir que o aplicativo receba do sistema Android os metadados da chamada entrante (número do discador) no momento exato do toque para verificar se a ligação se enquadra em critérios de bloqueio (ex.: prefixo 0303 de telemarketing, chamadas a cobrar 9090, finais repetitivos de robocalls, lista negra personalizada ou números fora da agenda no Modo Foco).
- **Tratamento:** A avaliação do número é efetuada estritamente em memória volátil (RAM) local. Se a chamada for bloqueada, uma entrada com o número, motivo e data/hora é registrada exclusivamente no banco de dados local (SQLite/Room) do seu dispositivo para consulta no histórico.
- **Transmissão:** NENHUM registro de chamada é transmitido ou disponibilizado a terceiros.

#### b) Leitura de Contatos (`android.permission.READ_CONTACTS`)
- **Finalidade:** Utilizada exclusivamente para verificar se o número da chamada recebida já consta como um contato salvo na agenda do telefone. Essa verificação é indispensável para:
  1. Garantir que chamadas legítimas de amigos, familiares e contatos conhecidos nunca sejam bloqueadas por engano.
  2. Permitir o funcionamento do **Modo Foco (Somente Agenda)**, no qual apenas pessoas da sua lista de contatos têm permissão para fazer o aparelho tocar.
- **Tratamento:** A leitura é realizada sob demanda e estritamente no próprio aparelho. O aplicativo **não copia, não exporta, não armazena e não envia sua lista de contatos** para nenhum servidor ou serviço externo.

#### c) Notificações (`android.permission.POST_NOTIFICATIONS`)
- **Finalidade:** Necessária a partir do Android 13 (API 33) para emitir notificações no sistema informando imediatamente ao usuário sempre que uma ligação abusiva ou de spam tiver sido bloqueada com sucesso em segundo plano.

---

### 3. Coleta, Armazenamento e Exclusão de Dados

- **Coleta de Dados:** Nenhuma. O desenvolvedor não possui acesso a nenhuma informação trafegada ou armazenada no app.
- **Armazenamento:** Configurações (regras ativas, listas negra e branca) e o relatório de chamadas barradas residem unicamente no diretório de armazenamento privado do aplicativo dentro do dispositivo (`encrypted / sandboxed app storage`).
- **Retenção e Exclusão de Dados:**
  - O usuário tem controle total e pode apagar o histórico de chamadas bloqueadas a qualquer momento diretamente pela interface do aplicativo (botão de limpar histórico).
  - A desinstalação do aplicativo apaga automaticamente e de forma irreversível todos os dados, regras e históricos armazenados localmente.

---

### 4. SDKs de Terceiros e Rastreamento
O BloqFone não utiliza SDKs proprietários de redes sociais, bibliotecas de métricas comportamentais (como Google Analytics para Firebase, Mixpanel ou AppsFlyer) ou redes de anúncios (como AdMob ou Unity Ads). As dependências do projeto são limitadas a bibliotecas oficiais do ecossistema Android Jetpack e Kotlin.

---

### 5. Conformidade Legal (LGPD)
Em conformidade com a Lei Geral de Proteção de Dados Pessoais do Brasil (**LGPD — Lei nº 13.709/2018**):
- O aplicativo não atua como controlador ou operador de dados em ambientes externos, visto que o processamento ocorre integralmente no ambiente controlado pelo titular (seu smartphone).
- O titular pode revogar as permissões a qualquer momento através das configurações do Android (`Configurações > Apps > BloqFone > Permissões`).

---

### 6. Proteção de Crianças e Menores
O BloqFone não é direcionado a crianças, tampouco realiza coleta intencional de dados de qualquer usuário, cumprindo rigorosamente os padrões da COPPA e diretrizes de proteção à infância da Google Play Store.

---

### 7. Contato do Desenvolvedor
Se você tiver qualquer dúvida ou solicitação referente a esta política de privacidade, entre em contato:
- **Responsável:** Cicero Regis
- **E-mail:** [ciceroregis25@gmail.com](mailto:ciceroregis25@gmail.com)
- **Código Fonte:** [https://github.com/ciceroregis/BloqFone](https://github.com/ciceroregis/BloqFone)

---

## 🇺🇸 English

### 1. Overview and Offline-First Privacy Principle
**BloqFone** is built around the fundamental principles of **Privacy by Design** and **Data Minimization**. The application operates **100% locally and offline** on your Android device.

- **No personal data collection:** No user data or phone identifiers are ever collected, uploaded, or transmitted to any external servers.
- **No backend servers:** The app does not maintain or communicate with cloud servers, remote databases, or tracking endpoints.
- **No advertisements or data monetization:** BloqFone contains no ads, no affiliate trackers, and no monetization SDKs.

---

### 2. Sensitive Permissions and Purpose of Use

To provide spam call identification, screening, and blocking capabilities, BloqFone requests the following permissions from Android:

#### a) Call Screening Service (`CallScreeningService` / Default Call Screening Role)
- **Purpose:** Allows BloqFone to receive incoming call metadata (caller phone number) at the moment of the call to check if it matches configured spam criteria (such as telemarketing prefixes like 0303, collect call patterns like 9090, robocall repetitive numbers, custom blacklists, or non-contacts when Focus Mode is enabled).
- **Processing:** The telephone number inspection is performed entirely in local volatile memory (RAM). If a call is blocked, an entry detailing the number, blocking reason, and timestamp is stored exclusively in your device's local database (SQLite/Room) to populate your in-app block report.
- **Transmission:** NO call records are ever shared, uploaded, or transmitted.

#### b) Read Contacts (`android.permission.READ_CONTACTS`)
- **Purpose:** Used strictly to determine whether an incoming call originates from a number already saved in your address book. This ensures:
  1. Known contacts (family, friends, coworkers) are never inadvertently blocked.
  2. The **Focus Mode (Contacts Only)** can successfully allow calls only from saved contacts while silencing all unknown numbers.
- **Processing:** Contacts are accessed on-demand and strictly on the local device. The application **never copies, exports, stores remotely, or shares your contact list**.

#### c) Post Notifications (`android.permission.POST_NOTIFICATIONS`)
- **Purpose:** Required on Android 13 (API level 33) and higher to display immediate system notifications whenever an unwanted spam call has been intercepted and blocked in the background.

---

### 3. Data Collection, Storage, and Deletion

- **Data Collection:** None. The developer has no access to any data handled by the application.
- **Data Storage:** All rules, custom lists (blacklist and whitelist), and blocked call logs are stored exclusively in the application's private, sandboxed local storage on your device.
- **Data Retention and Deletion:**
  - Users can clear their blocked calls history at any time using the in-app "Limpar histórico" (Clear History) option.
  - Uninstalling the app permanently and irreversibly deletes all local preferences, database entries, and settings.

---

### 4. Third-Party SDKs and Tracking
BloqFone does not integrate any third-party advertising, analytics, or behavioral tracking SDKs (such as Firebase Analytics, AdMob, or Facebook SDK). It relies solely on official Google Android Jetpack and standard Kotlin libraries.

---

### 5. Legal Compliance (LGPD & GDPR)
In compliance with the Brazilian General Data Protection Law (**LGPD**) and the European General Data Protection Regulation (**GDPR**):
- The app does not process or transfer personal data outside the user's personal device.
- Users have full sovereignty over their permissions and can revoke them at any time via Android Settings (`Settings > Apps > BloqFone > Permissions`).

---

### 6. Children's Privacy
BloqFone is a general utility tool and does not solicit, collect, or store data from any individual, including children under the age of 13, in strict adherence to COPPA and Google Play Families Policies.

---

### 7. Contact Information
For any inquiries regarding this Privacy Policy or the BloqFone application, please contact:
- **Developer:** Cicero Regis
- **Email:** [ciceroregis25@gmail.com](mailto:ciceroregis25@gmail.com)
- **Source Code & Issue Tracker:** [https://github.com/ciceroregis/BloqFone](https://github.com/ciceroregis/BloqFone)
