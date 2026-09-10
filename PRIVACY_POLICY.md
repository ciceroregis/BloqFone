# Política de Privacidade / Privacy Policy — BloqFone

**Última atualização / Last updated:** 10 de Setembro de 2026

O **BloqFone** foi desenvolvido com foco total em privacidade e segurança dos seus usuários. Esta Política de Privacidade explica como o aplicativo funciona e assegura que suas informações pessoais e registros telefônicos permanecem estritamente no seu dispositivo.

---

## 1. Princípio Fundamental: 100% Offline e Local
O BloqFone é um aplicativo que opera **100% localmente no seu dispositivo**. O aplicativo **não** possui servidores externos, **não** se comunica com nenhuma API de terceiros, **não** possui sistemas de rastreamento (trackers) e **não** exibe anúncios.

Nenhum dado pessoal, histórico de chamadas ou contato é coletado, transmitido ou compartilhado.

---

## 2. Permissões Utilizadas e Finalidade

Para fornecer seus serviços de triagem e bloqueio de chamadas, o BloqFone solicita as seguintes permissões do sistema Android:

### a) Serviço de Triagem de Chamadas (`CallScreeningService` / Papel de Bloqueador Padrão)
- **Finalidade:** Permite que o aplicativo receba do sistema operacional o número da chamada recebida em tempo real para verificar se ele se enquadra nas regras de bloqueio (telemarketing 0303, chamadas a cobrar/spam 9090, robocalls, lista negra ou fora da agenda).
- **Tratamento dos dados:** A avaliação do número ocorre estritamente na memória volátil do aparelho. Se a chamada for bloqueada, o registro é salvo apenas no banco de dados local do seu próprio aparelho (histórico/relatório).

### b) Leitura de Contatos (`android.permission.READ_CONTACTS`)
- **Finalidade:** Utilizada exclusivamente para verificar se o número que está ligando pertence à sua agenda telefônica pessoal (essencial para o funcionamento do "Modo Foco" e para garantir que seus contatos conhecidos não sejam bloqueados acidentalmente).
- **Tratamento dos dados:** Os contatos são lidos apenas localmente e sob demanda em tempo de execução. Seus contatos **nunca** são enviados para a internet, nem compartilhados ou armazenados em servidores remotos.

### c) Notificações (`android.permission.POST_NOTIFICATIONS`)
- **Finalidade:** Utilizada em dispositivos Android 13 ou superior para exibir uma notificação visual imediata sempre que uma chamada for barrada com sucesso pelo aplicativo.

---

## 3. Coleta e Compartilhamento de Dados (Segurança de Dados)
- **Coleta de dados:** NENHUM dado é coletado pelo desenvolvedor.
- **Compartilhamento de dados com terceiros:** NENHUM dado é compartilhado com terceiros, empresas ou parceiros.
- **Criptografia e Armazenamento:** Todas as preferências de configuração, regras ativas, listas (Negra e Branca) e o relatório de chamadas bloqueadas ficam armazenados exclusivamente na memória privada do aplicativo no próprio smartphone.
- **Exclusão de Dados:** O usuário pode a qualquer momento limpar o relatório de chamadas ou desinstalar o aplicativo, o que removerá permanentemente todos os dados armazenados localmente.

---

## 4. Conformidade com LGPD e GDPR
Em conformidade com a Lei Geral de Proteção de Dados (LGPD - Brasil) e o Regulamento Geral sobre a Proteção de Dados (GDPR - União Europeia), o BloqFone adota o princípio de *Privacy by Design* (privacidade desde a concepção) e *Data Minimization* (minimização de dados), não realizando tratamento de dados além do estritamente necessário no próprio hardware do usuário.

---

## 5. Crianças e Adolescentes
O BloqFone é uma ferramenta de utilidade e gerenciamento de chamadas destinada a usuários gerais e não coleta dados de nenhuma pessoa, incluindo crianças ou adolescentes.

---

## 6. Alterações nesta Política
Esta Política de Privacidade pode ser atualizada ocasionalmente. Quaisquer alterações serão refletidas neste documento com a respectiva data de revisão.

---

## 7. Contato
Caso você tenha quaisquer dúvidas sobre o funcionamento do aplicativo ou sobre esta Política de Privacidade, entre em contato através do e-mail:
- **E-mail do Desenvolvedor:** ciceroregis25@gmail.com
- **Repositório do Projeto:** https://github.com/ciceroregis/BloqFone
