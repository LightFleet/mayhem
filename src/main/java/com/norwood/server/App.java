package com.norwood.server;

import com.norwood.Client;

public class App {

    public static void main(String[] args) {
        run();

        System.out.println("Spawning Bob.");
        Client bob = createClient("Bob");
        bob.createRoom("bobr");
        bob.sendMessage("Hi everyone!");
    }

    private static void run() {
        Server server = new Server();
        Thread serverThread = new Thread(() -> server.run());
        serverThread.start();
        System.out.println("Started server.");
    }

    private static Client createClient(String name) {
        Client client = new Client(name);
        client.run();
        return client;
    }
}
