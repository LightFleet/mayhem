package com.norwood;

import com.norwood.communication.CommandType;
import com.norwood.communication.FunctionType;

public class App
{
    public static void main(String[] args) {
        try {
            Server server = new Server();
            Thread serverThread = new Thread(() -> server.run());
            serverThread.start();
 
            // Client client1 = createClient("Bob");
            // client1.sendFunction(CommandType.FUNCTION, FunctionType.CREATE_ROOM.toString());
            //
            // for (int i = 0; i < 1000; i++) {
            //     client1.sendMessage("Bob room 1", "Hi everyone " + i);
            // }

            Thread.sleep(100);
            server.renderJournal();
            // client1.sendFunction();
            // client1.sendFunction();
            // client1.sendFunction();
            // client1.sendFunction();
            // client1.sendFunction();
            // client1.sendFunction();

            // Client client2 = createClient("Alice");
            // client2.sendMessage("Hi everyone!");
        
            serverThread.join();
             
            server.renderJournal();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Client createClient(String name) {
        Client client = new Client(name);
        Thread clientThread = new Thread(() -> client.run());
        clientThread.start();
        return client;
    }
}
