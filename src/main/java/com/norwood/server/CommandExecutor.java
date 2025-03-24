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
import com.norwood.journal.Record;
import com.norwood.server.Command.Fields;

class CommandExecutor 
{
    Map<ServerCallbackType, Object> serverCallbacks = new HashMap<>();
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
        Map<String, String> fields = CommandFactory.parse(message);

        Command type = Command.from(fields.get("type"));

        type.execute(this, message);
    }

    void noAction(Map<String, String> fields) {
        // Nothing
    }

    void roomList(Map<String, String> fields) {
        if (serverInfo.rooms.isEmpty()) {
            sendMessageToClient(fields.get(Fields.user), "No rooms available");
        }

        String roomsList = serverInfo.rooms.stream()
                .map(Room::getName)
                .collect(Collectors.joining(", "));

        sendMessageToClient(fields.get(Fields.user), CommandFactory.roomsList(roomsList));
    }

    @SuppressWarnings("unchecked")
    void sendMessageToClient(String userName, String content) {
        ((BiConsumer<String, String>) serverCallbacks
            .get(ServerCallbackType.SEND_MESSAGE))
            .accept(userName, content);
    }

    void handleMessage(Map<String, String> fields) {
        Server.journal.addRoomRecord(
            fields.get("user"), 
            fields.get("room"),
            fields.get("message")
        );
    }

    void joinRoom(Map<String, String> fields) {
        String name = fields.get(Fields.user);
        String user = fields.get(Fields.room);
        Room room = serverInfo.rooms.stream()
            .filter(r -> r.getName().equals(name))
            .findFirst().orElseThrow();
        
        room.addUser(user);
        String roomLog = Server.journal.roomRecords(name).stream()
                .map(Record::toString)
                .collect(Collectors.joining("|"));


        Server.journal.addServerRecord("Sending room '" + room.getName() + "' log to " + user);
        Server.journal.addServerRecord("Room log: " + roomLog);

        sendMessageToClient(user, CommandFactory.roomLog(roomLog));
    }

    void createRoom(Map<String, String> fields) {
        String name = fields.get(Fields.user);
        String user = fields.get(Fields.room);
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

