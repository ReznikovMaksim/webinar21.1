package edu.melikk.bonus;

public class MainClient {

    public static void main(String[] args) {
        EchoClient.connectTo(8788).run();
    }
}
