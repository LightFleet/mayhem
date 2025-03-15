package com.norwood.communication;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;

@Builder
public final class Command 
{
    // public CommandType type;
    public String content;

    public static Command fromStr(String messageStr) {
        return builder()
            .content(messageStr)
            .build();
    } 

    public static String message(String message, String user, String room) {
        return Command.from(Map.of(
            "type", CommandType.MESSAGE.toString(),
            "message", message,
            "user", user,
            "room", room
        ));
    } 
    
    public static String createRoom(String user, String name) {
        return function(FunctionType.CREATE_ROOM, user, name);
    } 

    public static String function(FunctionType functionType, String user, String name) {
        return Command.from(Map.of(
            "type", CommandType.FUNCTION.toString(),
            "function_type", functionType.toString(),
            "name", name,
            "user", user
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

    public static Map<String, String> parse(String command) {
        Map<String, String> fields = new HashMap<>();
         
        String[] pairs = command.toString().split("\\|");
        for (String pair : pairs) {
            String[] field = pair.split(":");
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
