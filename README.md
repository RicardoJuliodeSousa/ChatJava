# Chat Java

Aplicação de chat em terminal desenvolvida em Java, utilizando Sockets,
Streams e Multithreading para permitir a comunicação entre múltiplos
clientes conectados a um servidor.

## Tecnologias utilizadas

- Java (JDK 25 utilizado nos testes).
- Sockets TCP: Socket e ServerSocket.
- Streams: BufferedReader, InputStreamReader, OutputStreamWriter e PrintWriter.
- Multithreading: uma thread de atendimento para cada cliente no servidor.
- Git e GitHub para controle de versão.

## Funcionalidades

- Conexão de múltiplos clientes simultaneamente.
- Identificação do usuário por nome.
- Envio de mensagens para todos os clientes conectados.
- Recebimento de mensagens enquanto o usuário digita no terminal.
- Avisos de entrada e saída de usuários.
- Comando /sair para desconectar.
- Remoção de clientes desconectados e fechamento de suas conexões.
- Continuidade da conversa entre os demais clientes após uma saída.

## Estrutura do projeto

- cliente/Cliente.java: conecta ao servidor, envia e recebe mensagens.
- servidor/servidorChat.java: aceita conexões e distribui as mensagens.
- README.md: documentação e instruções de execução.
- .gitignore: exclui arquivos locais da IDE e arquivos compilados do Git.

## Requisitos para executar

- JDK instalado, com os comandos java e javac disponíveis.
- Terminal aberto na pasta principal do projeto.

Os comandos abaixo podem ser executados no terminal integrado do IntelliJ IDEA.

## Como compilar

Na pasta principal do projeto, execute:

```bash
javac -encoding UTF-8 -d out cliente/Cliente.java servidor/servidorChat.java
```

Os arquivos compilados serão gerados na pasta out.

## Como iniciar o servidor

Execute:

```bash
java -cp out servidor.servidorChat
```

O servidor utiliza a porta TCP 5000.

Mantenha esse terminal aberto durante o uso do chat.

## Como iniciar os clientes

Em outra aba do terminal, execute:

```bash
java -cp out cliente.Cliente
```

Digite seu nome e pressione Enter.

Para conectar mais usuários, abra novas abas e execute o mesmo comando
em cada uma, informando nomes diferentes.

Por padrão, o cliente se conecta a localhost, ou seja, ao servidor
executado no mesmo computador.

Para informar outro endereço de servidor:

```bash
java -cp out cliente.Cliente ENDERECO_DO_SERVIDOR
```

Substitua ENDERECO_DO_SERVIDOR pelo IP ou nome do computador que executa
o servidor. A porta 5000 precisa estar acessível pela rede.

## Como utilizar

Digite uma mensagem e pressione Enter para enviá-la.

O servidor distribui a mensagem a todos os clientes conectados,
incluindo quem a enviou.

Para sair, digite:

```text
/sair
```

## Demonstração com três clientes

1. Inicie o servidor em uma aba do terminal.
2. Abra outras três abas e execute um cliente em cada uma.
3. Utilize os nomes Ana, Bruno e Carlos.
4. Envie uma mensagem como Ana e confira o recebimento nos outros clientes.
5. Digite /sair no cliente Bruno.
6. Envie uma mensagem como Carlos e responda como Ana.
7. Confirme que ambos continuam trocando mensagens.

Esse procedimento foi realizado com sucesso nos testes manuais.

## Como funciona

O servidor utiliza ServerSocket para aguardar conexões. Para cada cliente
aceito, cria um ClienteHandler, que executa em sua própria thread.

Cada conexão utiliza streams de texto com codificação UTF-8.
As mensagens são enviadas com println e recebidas com readLine.

O servidor mantém os clientes conectados em um conjunto concorrente,
criado com ConcurrentHashMap.newKeySet(). O método transmitir percorre
esse conjunto e encaminha as mensagens aos participantes.

No cliente, uma thread recebe as mensagens do servidor enquanto a thread
principal lê o teclado e envia as mensagens digitadas.

Quando o cliente encerra a conexão, o atendimento no servidor termina.
O bloco finally fecha o socket, remove o cliente do conjunto e avisa
os demais participantes, sem encerrar o servidor.