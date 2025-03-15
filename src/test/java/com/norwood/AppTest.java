package com.norwood;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.norwood.Server.Journal.Record;
import com.norwood.communication.Command;

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
        Client client1 = createClient(user);
        client1.sendCommand(Command.createRoom(user, "Bob Room 1"));
        client1.sendCommand(Command.message("Hi everyone!", user, "Bob Room 1"));

        Thread.sleep(10);

        assertJournalHas("Hi everyone!");
    }

    @Test
    @SneakyThrows
    void testManyClients() {
        final String name = "Room 1";
        for (int i = 1; i <= 10; i++) {
            Client client1 = createClient(user + i);
            client1.sendCommand(Command.createRoom(user, name));
            client1.sendCommand(Command.message("Hi everyone!", user + i, name));
        }

        Thread.sleep(10);

        assertJournalHas("Hi everyone!");

        assertTrue(Server.journal.records().stream()
            .filter(Record::isRoom)
            .count() == 10);
    }

    private void assertJournalHas(String message) {
        assertTrue(Server.journal.records()
            .stream()
            .anyMatch(r -> r.content().equals(message))
        );
    }
    
    private static Client createClient(String name) {
        Client client = new Client(name);
        Thread clientThread = new Thread(() -> client.run());
        clientThread.start();
        return client;
    }
}
