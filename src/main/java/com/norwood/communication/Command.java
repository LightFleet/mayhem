package com.norwood.communication;

import java.util.HashMap;
import java.util.Map;

public final class Command 
{
    public String content;

    // ---- CLIENT TO SERVER COMMANDS -----

    public static String message(String user, String message, String room) {
        return Command.from(Map.of(
            Fields.type, CommandType.MESSAGE.toString(),
            Fields.message, message,
            Fields.user, user,
            Fields.room, room
        ));
    } 
    
    public static String register(String user) {
        return Command.from(Map.of(
            Fields.type, CommandType.REGISTER.toString(),
            Fields.user, user
        ));
    } 

    public static String createRoom(String user, String name) {
        return function(FunctionType.CREATE_ROOM, user, name);
    } 

    public static String joinRoom(String user, String roomName) {
        return function(FunctionType.JOIN_ROOM, user, roomName);
    }

    public static String function(FunctionType functionType, String user, String room) {
        return Command.from(Map.of(
            Fields.type, CommandType.FUNCTION.toString(),
            Fields.functionType, functionType.toString(),
            Fields.room, room,
            Fields.user, user
        ));
    } 
    // ---- END CLIENT TO SERVER COMMANDS -----
    
    // ---- SERVER TO CLIENT COMMANDS -----
    public static String roomLog(String user, String message) {
        return Command.from(Map.of(
            Fields.type, CommandType.ROOM_LOG.toString(),
            Fields.user, user,
            Fields.message, message
        ));
    } 
    // ---- ENDSERVER TO CLIENT COMMANDS -----

    public static String from(Map<String, String> fields) {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String,String> e : fields.entrySet()) {
            content.append(e.getKey() + ":" + e.getValue());
            content.append("|");
        }
        return content.toString();
    }

    public static Map<String, String> parse(String command) {
        Map<String, String> fields = new HashMap<>();
         
        String[] pairs = command.toString().split("\\|");
        for (String pair : pairs) {
            String[] field = pair.split(":", 2);
            fields.put(field[0], field[1]);
        }

        return fields;
    }

    @Override
    public String toString() {
        return content;
    }
    
    public String print() {
        return "Command [content=" + content + "]";
    }

}
