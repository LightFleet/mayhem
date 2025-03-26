package com.norwood.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum Command
{
    CREATE_ROOM(CommandExecutor::createRoom),
    JOIN_ROOM(CommandExecutor::joinRoom),
    MESSAGE(CommandExecutor::handleMessage),
    REGISTER(CommandExecutor::handleMessage),
    ROOM_LIST(CommandExecutor::roomList),
    SROOMS(CommandExecutor::noAction),
    SMESSAGE(CommandExecutor::noAction);

    public static class Fields
    {
        public static final String type = "type";
        public static final String message = "message";
        public static final String user = "user";
        public static final String room = "room";
        public static final String functionType = "function_type";
    }

    private static final Map<String, Command> toEnum = new HashMap<>();
    private final BiConsumer<CommandExecutor, Map<String, String>> action;

    static {
        for (Command type : values()) {
            toEnum.put(type.toString(), type);
        }
    }

    Command (BiConsumer<CommandExecutor, Map<String, String>> action) {
        this.action = action;
    }
    
    public static Command from(String type) {
        return toEnum.get(type);
    }

    public void execute(CommandExecutor executor, String message) {
        action.accept(executor, fields(message));
    }

    private static Map<String, String> fields(String message) {
        return CommandFactory.parse(message);
    }

    public static String from(Map<String, String> fields) {
        return CommandFactory.from(fields);
    }
}
