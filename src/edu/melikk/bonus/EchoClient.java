package edu.melikk.bonus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final int port;
    private final String host;

    private EchoClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public static EchoClient connectTo(int port) {
        return new EchoClient(port, "127.0.0.1");
    }

    public void run() {
        System.out.println("напиши 'bye' чтобы выйти\n\n");
        try (var socket = new Socket(host, port)) {
            try (var scanner = new Scanner(System.in, "UTF-8");
                 var socketScanner = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                 var writer = new PrintWriter(socket.getOutputStream())) {
                var format = "Server response: %s%n";
                while (true) {
                    var message = scanner.nextLine();
                    writer.write(message);
                    writer.write(System.lineSeparator());
                    writer.flush();
                    System.out.printf(format, socketScanner.nextLine());
                    if ("bye".equalsIgnoreCase(message)) return;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Connection dropped!");
        } catch (IOException e) {
            System.out.printf("Cant`t connect to %s:%s%n", host, port);
            e.printStackTrace();
        }
    }
}
