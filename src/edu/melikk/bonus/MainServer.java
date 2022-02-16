package edu.melikk.bonus;

public class MainServer {

    public static void main(String[] args) {
        EchoServer.bindToPort(8788).run();
    }
}
