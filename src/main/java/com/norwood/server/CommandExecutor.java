package com.norwood.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.norwood.Room;
import com.norwood.communication.Command;
import com.norwood.communication.CommandType;
import com.norwood.communication.Fields;
import com.norwood.communication.FunctionType;

class CommandExecutor 
{
    Map<ServerCallbackType, Object> serverCallbacks = new HashMap<>();
    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private static ServerInfo serverInfo = new ServerInfo();

    public enum ServerCallbackType {
        SEND_MESSAGE
    }

    static private class ServerInfo {
        private List<Room> rooms = new ArrayList<>();
        public static final int MAX_ROOMS_PER_CLIENT = 5;

        public final Map<String, Integer> roomsByClient = new HashMap<>();
    }

    public void registerCallback(ServerCallbackType type, Object callback) {
        serverCallbacks.put(type, callback);
    }

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

    @SuppressWarnings("unchecked")
    private void sendMessageToClient(String userName, String content) {
        ((BiConsumer<String, String>) serverCallbacks.get(ServerCallbackType.SEND_MESSAGE))
        .accept(userName, content);
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

    public void executeServerCommands() throws InterruptedException {
        while (!commands.isEmpty()) {
            execute(commands.take());
        }
    }

    private void joinRoom(String user, String name) {
        Room room = serverInfo.rooms.stream()
            .filter(r -> r.getName().equals(name))
            .findFirst().orElseThrow();
        
        room.addUser(user);
        commands.add(Command.roomLog(
            user,
            Server.journal.roomRecords(name).stream()
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
        serverInfo.rooms.add(new Room(name, user));
        Server.journal.addServerRecord("Created room!");
    }
}

