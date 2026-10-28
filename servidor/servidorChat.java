package servidor;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class servidorChat {
    private static final int PORTA = 5000;

    private static final Set<ClienteHandler> CLIENTES =
            ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Iniciando o servidor ...");

        try (ServerSocket servidor = new ServerSocket((PORTA))){

            System.out.println("Servidor aguardando conexões na porta" + PORTA);

            while (true) {
                Socket socket = servidor.accept();

                System.out.println("Nova conexão:" +
                        socket.getInetAddress().getHostAddress());

                ClienteHandler cliente = new ClienteHandler(socket);
                cliente.start();
            }
        }
        catch (IOException e) {
            System.err.println("Erro no servidor:" + e.getMessage());
        }
    }

    private static void transmitir(String mensagem) {
        for (ClienteHandler cliente : CLIENTES) {
            cliente.enviarMensagem(mensagem);
        }
    }

    private static class ClienteHandler extends Thread {
        private final Socket socket;
        private PrintWriter saida;
        private String nome;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                Socket conexao = socket;

                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(
                                conexao.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );

                PrintWriter escritor = new PrintWriter(
                        new OutputStreamWriter(
                                conexao.getOutputStream(),
                                StandardCharsets.UTF_8
                        ),
                        true
                );

                saida = escritor;

                nome = entrada.readLine();

                if(nome == null || nome.trim().isEmpty()) {
                    return;
                }

                nome = nome.trim();
                CLIENTES.add(this);

                transmitir(">>>" + nome + "entrou no chat");
                System.out.println(nome + "conectado.");

                String mensagem;

                while ((mensagem = entrada.readLine()) != null) {

                    if (!mensagem.trim().isEmpty()) {
                        transmitir(nome + ":" + mensagem);
                    }
                }
            }catch (IOException e) {
                System.out.println("Conexão com cliente encerrada");

            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println(
                            "Erro ao fechar conexão do cliente: " + e.getMessage()
                    );
                }

                if (CLIENTES.remove(this) && nome != null) {
                    transmitir("<<<" + nome + "saiu do chat");

                    System.out.println(nome + "desconectado");
                }
            }
        }

        public void enviarMensagem(String mensagem) {
            PrintWriter escritor = saida;

            if (escritor != null) {
                synchronized (escritor) {
                    escritor.println(mensagem);
                }
            }
        }
    }
}