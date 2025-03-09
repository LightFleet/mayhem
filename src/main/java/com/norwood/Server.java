package com.norwood;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.norwood.communication.Command;
import com.norwood.communication.FunctionType;

public class Server
{
    static private class Journal {
        static private record Record(String timestamp, String user, String context, String content) {}
        
        List<Record> records = new ArrayList<>();
        
        private void addRecord(Record record) {
            records.add(record);
        }

        public void render() {
            records.forEach(System.out::println);
        }

        public void renderRoom(Room room) {
            throw new RuntimeException("Non impl.");
        }

        public void addRoomRecord(String user, String context, String content) {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MM/dd/yyyy/HH:ss:SSS")
            );
            addRecord(new Record(timestamp, user, context, content));
        }
 
        public void addServerRecord(String content) {
            String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MM/dd/yyyy/HH:ss:SSS")
            );
            addRecord(new Record(timestamp, "Server", "Server", content));
        }
   }

    static private class ServerInfo {
        public static final int MAX_ROOMS_PER_CLIENT = 5;

        public final Map<String, Integer> roomsByClient = new HashMap<>();
    }

    class CommandExecutor {
        public void execute(Command command) {
            switch (command.type) {
                case MESSAGE -> handleMessage(command);
                case REGISTER -> register(command);
                case FUNCTION -> function(command);
                default -> Server.journal.addServerRecord("Unknown command. How did it happen?");
            }
        }

        private void handleMessage(Command command) {
            String[] commandElements = command.toString().split("\\|");

            String message = commandElements[1];
            String user = commandElements[2];
            String room = commandElements[3];
            
            Server.journal.addRoomRecord(user, room, message);
        }

        private void function(Command command) {
            Server.journal.addServerRecord("content:" + command.content);
            String[] commandElements = command.toString().split("\\|");
            String typeStr = commandElements[1];
            FunctionType type = FunctionType.from(typeStr);

            switch (type) {
                case CREATE_ROOM -> createRoom(commandElements[2]);
            }
        }

        private void createRoom(String user) {
            Integer roomsByClient = serverInfo.roomsByClient.getOrDefault(user, 0);
            Server.journal.addServerRecord(user + " has created " + roomsByClient + " rooms");
            if (roomsByClient >= ServerInfo.MAX_ROOMS_PER_CLIENT) {
                Server.journal.addServerRecord("Room creation rejected..");
                return;
            }
            roomsByClient++;
            serverInfo.roomsByClient.put(user, roomsByClient);
            rooms.add(
                new Room(String.format("%s room %s", user, roomsByClient), user)
            );
            Server.journal.addServerRecord("Created room!");
        }

        private void register(Command command) {
            throw new UnsupportedOperationException("Unimplemented method 'register'");
        }
    }

    private static ServerInfo serverInfo = new ServerInfo();
    public static Journal journal = new Journal();
    private CommandExecutor executor = new CommandExecutor();

    private List<Room> rooms = new ArrayList<>();

    public void run() {
        try (ServerSocket socket = new ServerSocket(8001)) {
            Server.journal.addServerRecord("Started server. Listening to 8001");
            while (true) {
                try {
                    Socket clientSocket = socket.accept(); 
                    Server.journal.addServerRecord("New connection from a client: " + clientSocket.getInetAddress());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (Exception e) {
                    Server.journal.addServerRecord("Error during client handling: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Server.journal.addServerRecord("Error starting server: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                executor.execute(Command.from(message));
            }
        } catch (Exception e) {
            Server.journal.addServerRecord("Error while reading or writing from a socket.");
            Server.journal.addServerRecord(e.getMessage());
        } finally {
        }
    }

    public void printJournal() {
        journal.render();
    }
}
