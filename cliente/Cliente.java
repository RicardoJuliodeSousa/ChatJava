package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class Cliente {

    public static void main(String[] args) {


        String host = args.length > 0 ? args[0] : "localhost";

        int porta = 5000;

        BufferedReader teclado = new BufferedReader(
                new InputStreamReader(
                        System.in,
                        StandardCharsets.UTF_8
                )
        );

        try {

            System.out.print("Digite seu nome: ");
            String nome = teclado.readLine();

            if (nome == null || nome.trim().isEmpty()) {
                System.out.println("Nome invalido.");
                return;
            }

            try (
                    Socket socket = new Socket(host, porta);

                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

                    PrintWriter saida = new PrintWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream(),
                                    StandardCharsets.UTF_8
                            ),
                            true
                    )
            ) {


                saida.println(nome.trim());


                Thread receptor = new Thread(() -> {

                    try {
                        String mensagem;

                        while (
                                (mensagem = entrada.readLine()) != null
                        ) {
                            System.out.println(mensagem);
                        }

                    } catch (IOException e) {

                    }
                });

                receptor.setDaemon(true);
                receptor.start();

                System.out.println("Conectado ao chat!");
                System.out.println(
                        "Digite uma mensagem ou /sair para desconectar."
                );

                String mensagem;


                while ((mensagem = teclado.readLine()) != null) {

                    if ("/sair".equalsIgnoreCase(mensagem.trim())) {
                        break;
                    }

                    if (!mensagem.trim().isEmpty()) {
                        saida.println(mensagem);
                    }
                }

            }

        } catch (IOException e) {
            System.err.println(
                    "Nao foi possivel conectar ao servidor: "
                            + e.getMessage()
            );
        }
    }
}