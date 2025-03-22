package com.norwood.server;

public class App {

    public static void main(String[] args) {
        Server server = new Server();
        Thread serverThread = new Thread(() -> server.run());
        serverThread.start();
        System.out.println("Started server.");
        // Server.journal.clear();
    }
}
