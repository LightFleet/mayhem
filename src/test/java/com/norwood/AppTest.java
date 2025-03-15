package com.norwood;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.norwood.Server.Journal.Record;

import lombok.SneakyThrows;

class AppTest 
{
    private String user = "bob";
    private Server server;

    @BeforeEach
    void setUp() {
        server = new Server();
        Thread serverThread = new Thread(() -> server.run());
        serverThread.start();
        Server.journal.clear();
    }

    @Test
    void test() throws InterruptedException {
        // Client bob = createClient(user);
        // bob.createRoom("Bob Room 1");
        // bob.sendMessage("Hi everyone!");
        // Thread.sleep(30);
        //
        // assertJournalHas("Hi everyone!");
    }

    @Test
    @SneakyThrows
    void testJoinRoom() {
        // When client joins room he receives messages log of this room. Other clients don't.
        Client client1 = createClient("Bob");
        Client client2 = createClient("Alice");
        Client client3 = createClient("Eve");

        client1.createRoom("Room1");
        client2.createRoom("Room2");
        client3.createRoom("Room3");

        client1.sendMessage("Hi everyone from Bob!");
        client2.joinRoom("Room1");
        client2.sendMessage("Hi Bob!");

        Thread.sleep(30);

        Server.journal.render();
        assertJournalHas("Hi everyone from Bob!");
        assertTrue(Server.journal.roomRecords("Room1").stream().count() == 2);

        // assertTrue(client2.roomLog().has("Hi everyone from Bob!"));
        // assertTrue(client3.roomLog().doesNotHave("Hi everyone from Bob!"));
    }

    // @Test
    // @SneakyThrows
    // void testManyClients() {
    //     final String roomName = "Room 1";
    //     final String testMessage = "Hi everyone!";
    //
    //     for (int i = 1; i <= 10; i++) {
    //         Client client = createClient(user + i);
    //         client.createRoom(roomName);
    //         client.sendMessage(testMessage);
    //     }
    //
    //     Thread.sleep(10);
    //
    //     assertJournalHas(testMessage);
    //
    //     assertTrue(Server.journal.records().stream()
    //         .filter(Record::isRoom)
    //         .count() == 10);
    // }

    private void assertJournalHas(String message) {
        assertTrue(Server.journal.records()
            .stream()
            .anyMatch(r -> r.content().equals(message))
        );
    }
    
    private static Client createClient(String name) {
        Client client = new Client(name);
        client.run();
        return client;
    }
}
