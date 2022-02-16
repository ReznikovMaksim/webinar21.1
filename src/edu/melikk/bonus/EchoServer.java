package edu.melikk.bonus;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiConsumer;

public class EchoServer {
    private final int port;
    private boolean isConnected;
    private HashMap<String, BiConsumer<PrintWriter, String>> actions;

    private EchoServer(int port) {
        this.port = port;
        isConnected = true;
        initActions();
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (var server = new ServerSocket(port)) {
            try (var clientSocket = server.accept()) {
                handle(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("port is busy");
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) throws IOException {
        try (var scanner = new Scanner(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             var writer = new PrintWriter(socket.getOutputStream())) {
            while (isConnected) {
                String message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                String firstWord = message.split(" ")[0].toLowerCase();
                String withoutFirstWord = message.replaceFirst(firstWord, "").strip();
                try {
                    actions.get(firstWord).accept(writer, withoutFirstWord);
                } catch (NullPointerException npe) {
                    actions.get(null).accept(writer, message);
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped the connection");
        }
    }

    private void send(PrintWriter writer, String msg) {
        writer.write(msg);
        writer.write(System.lineSeparator());
        writer.flush();
    }

    private void initActions() {
        actions = new HashMap<>();
        actions.put(null, (w, s) -> send(w, s));
        actions.put("date", (w, s) -> send(w, LocalDate.now().toString()));
        actions.put("time", (w, s) -> send(w, LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        actions.put("reverse", (w, s) -> send(w, new StringBuilder().append(s).reverse().toString()));
        actions.put("upper", (w, s) -> send(w, s.toUpperCase()));
        actions.put("bye", ((w, s) -> {
            send(w, "bye bye");
            isConnected = false;
        }));
    }
}
