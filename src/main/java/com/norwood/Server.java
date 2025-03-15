package com.norwood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import com.norwood.communication.Command;
import com.norwood.communication.CommandType;
import com.norwood.communication.Fields;
import com.norwood.communication.FunctionType;

public class Server
{
    static public class Journal {
        static record Record(String timestamp, String user, String context, String content) {
            public boolean isRoom() { return !context().equals("Server"); }
            public boolean ofRoom(String roomName) { return context.equals(roomName); }
        }
        
        List<Record> records = new CopyOnWriteArrayList<>();
        
        private void addRecord(Record record) {
            records().add(record);
        }

        public List<Record> records() {
            return records;
        }

        public List<Record> roomRecords(String roomName) {
            return records().stream().filter(r -> r.ofRoom(roomName)).toList();
        }

        public void render() {
            records().forEach(System.out::println);
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

        public void clear() {
            records = new CopyOnWriteArrayList<>();
        }
   }

    static private class ServerInfo {
        public static final int MAX_ROOMS_PER_CLIENT = 5;

        public final Map<String, Integer> roomsByClient = new HashMap<>();
        public final Set<String> knownUsers = new HashSet<>();
    }

    class CommandExecutor {
        public void execute(String message) {
            Map<String, String> fields = Command.parse(message);

            CommandType type = CommandType.from(fields.get("type"));

            switch (type) {
                // C2S
                case MESSAGE -> handleMessage(fields);
                case FUNCTION -> function(fields);

                // S2C
                case ROOM_LOG -> roomLog(fields);
                default -> {
                    Server.journal.addServerRecord("Unknown command. How did it happen? Command: " + type);
                }
            }
        }

        private void roomLog(Map<String, String> fields) {
            String userName = fields.get(Fields.user);
            String content = fields.get(Fields.message);
            System.out.println("Content " + content);

            sendMessageToClient(userName, content);
        }

        private void sendMessageToClient(String userName, String content) {
            Socket socket = userToSocket.get(userName);
            if (socket == null || socket.isClosed()) {
                throw new RuntimeException("Can't send message to client. Socket is closed. Client: " + userName);
            }
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(content);
            } catch (IOException e) {
                throw new RuntimeException("Can't send message to client. Error writing. Client: " + userName);
            }
        }

        private void handleMessage(Map<String, String> fields) {
            Server.journal.addRoomRecord(
                fields.get("user"), 
                fields.get("room"),
                fields.get("message")
            );
        }

        private void function(Map<String, String> fields) {
            FunctionType type = FunctionType.from(fields.get("function_type"));

            switch (type) {
                case CREATE_ROOM -> createRoom(fields.get(Fields.user), fields.get(Fields.room));
                case JOIN_ROOM -> joinRoom(fields.get(Fields.user), fields.get(Fields.room));
            }
        }

        private void joinRoom(String user, String name) {
            Room room = rooms.stream()
                .filter(r -> r.getName().equals(name))
                .findFirst().orElseThrow();
            
            room.addUser(user);
            commands.add(Command.roomLog(
                user,
                journal.roomRecords(name).stream()
                    .map(Record::toString)
                    .collect(Collectors.joining())
            ));
        }

        private void createRoom(String user, String name) {
            Integer roomsByClient = serverInfo.roomsByClient.getOrDefault(user, 0);
            Server.journal.addServerRecord(user + " has created " + roomsByClient  + " rooms");
            if (roomsByClient >= ServerInfo.MAX_ROOMS_PER_CLIENT) {
                Server.journal.addServerRecord("Room creation rejected..");
                return;
            }
            roomsByClient++;
            serverInfo.roomsByClient.put(user, roomsByClient);
            rooms.add(new Room(name, user));
            Server.journal.addServerRecord("Created room!");
        }
    }

    private static ServerInfo serverInfo = new ServerInfo();
    public static Journal journal = new Journal();
    private CommandExecutor executor = new CommandExecutor();
    private List<Room> rooms = new ArrayList<>();
    private boolean running = true;
    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    Map<String, Socket> userToSocket = new HashMap<>();

    public void run() {
        try (ServerSocket socket = new ServerSocket(8001)) {
            Server.journal.addServerRecord("Started server. Listening to 8001");
            while (running) {
                try {
                    Socket clientSocket = socket.accept(); 
                    Server.journal.addServerRecord("New connection from a client: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (Exception e) {
                    Server.journal.addServerRecord("Error during client handling: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Server.journal.addServerRecord("Error starting server: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }

    private void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                Map<String, String> fields = Command.parse(message);
                String type = fields.get(Fields.type);

                if (CommandType.REGISTER.toString().equals(type)) {
                    tryToRegister(message, socket);
                    continue;
                } 
                executor.execute(message);

                // after each client command process ALL server commands
                while (!commands.isEmpty()) {
                    executor.execute(commands.take());
                }
            }
        } catch (Exception e) {
            Server.journal.addServerRecord("Error while reading or writing from a socket.");
            Server.journal.addServerRecord(e.getMessage());
        } finally {
        }
    }

    private void tryToRegister(String message, Socket socket) throws InterruptedException {
        Map<String, String> fields = Command.parse(message);
        String user = fields.get(Fields.user);

        if (serverInfo.knownUsers.contains(user)) {
            return;
        }

        String commandType = CommandType.REGISTER.toString();
        if (!fields.get(Fields.type).equals(commandType)) {
            return;
        }

        Server.journal.addServerRecord(String.format("User %s registered", user));
        userToSocket.put(user, socket);
        serverInfo.knownUsers.add(user);
    }

    public void renderJournal() {
        journal.render();
    }
}
