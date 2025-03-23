package com.norwood.server;

import java.util.Map;

public final class Command 
{
    public static class Fields
    {
        public static final String type = "type";
        public static final String message = "message";
        public static final String user = "user";
        public static final String room = "room";
        public static final String functionType = "function_type";
    }

    public enum FunctionType 
    {
        CREATE_ROOM, JOIN_ROOM;
        
        public static FunctionType from(String type) {
            return switch (type) {
                case "create_room" -> FunctionType.CREATE_ROOM;
                case "join_room" -> FunctionType.JOIN_ROOM;
                default -> throw new RuntimeException("No enum of type: " + type);
            };
        }

        @Override
        public String toString() {
            return name().toLowerCase(); 
        }
    }

    public enum CommandType 
    {
        // C2S
        MESSAGE, REGISTER, FUNCTION,
        ROOM_LIST,
        // S2C
        SMESSAGE, SROOMS
        ;

        public static CommandType from(String type) {
            return switch (type) {
                // C2S
                case "message" -> CommandType.MESSAGE;
                case "register" -> CommandType.REGISTER;
                case "function" -> CommandType.FUNCTION;
                case "room_list" -> CommandType.ROOM_LIST;

                // S2C
                case "smessage" -> CommandType.SMESSAGE;
                case "srooms" -> CommandType.SROOMS;
                default -> throw new RuntimeException("No enum of type: " + type);
            };
        }

        @Override
        public String toString() {
            return name().toLowerCase(); 
        }
    }

    public static String from(Map<String, String> fields) {
        return CommandFactory.from(fields);
    }
}
