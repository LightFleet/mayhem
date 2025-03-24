package com.norwood.server;

import java.util.HashMap;
import java.util.Map;

import com.norwood.server.Command.Fields;

public final class CommandFactory 
{
    public static String message(String user, String message, String room) {
        return from(Map.of(
            Fields.type, Command.MESSAGE.toString(),
            Fields.message, message,
            Fields.user, user,
            Fields.room, room
        ));
    } 
    
    public static String register(String user) {
        return from(Map.of(
            Fields.type, Command.REGISTER.toString(),
            Fields.user, user
        ));
    } 

    public static String createRoom(String user, String name) {
        return function(Command.CREATE_ROOM, user, name);
    } 

    public static String joinRoom(String user, String roomName) {
        return function(Command.JOIN_ROOM, user, roomName);
    }

    public static String function(Command command, String user, String room) {
        return from(Map.of(
            Fields.type, command.toString(),
            Fields.room, room,
            Fields.user, user
        ));
    } 

    public static String roomsList(String roomsList) {
        return Command.from(Map.of(
            Fields.type, Command.SROOMS.toString(),
            Fields.message, roomsList 
        ));
    } 

    public static String roomLog(String roomLog) {
        return Command.from(Map.of(
            Fields.type, Command.SMESSAGE.toString(),
            Fields.message, roomLog
        ));
    } 

    public static String from(Map<String, String> fields) {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String,String> e : fields.entrySet()) {
            content.append(e.getKey() + ":" + e.getValue());
            content.append("|");
        }
        return content.toString();
    }

    // Key-value parsing, command-agnostic
    public static Map<String, String> parse(String command) {
        Map<String, String> fields = new HashMap<>();
         
        String[] pairs = command.toString().split("\\|");
        for (String pair : pairs) {
            String[] field = pair.split(":", 2);
            fields.put(field[0], field[1]);
        }

        return fields;
    }

    public static String requestRoomsList(String user) {
        return basic(Command.ROOM_LIST, user);
    }

    public static String basic(Command type, String user) {
        return Command.from(Map.of(
            Fields.user, user,
            Fields.type, type.toString()
        ));
    }
}
