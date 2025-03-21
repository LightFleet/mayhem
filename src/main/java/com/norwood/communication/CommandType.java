package com.norwood.communication;

public enum CommandType 
{
    // C2S
    MESSAGE, REGISTER, FUNCTION,
    // S2C
    ROOM_LOG
    ;

    public static CommandType from(String type) {
        return switch (type) {
            // C2S
            case "message" -> CommandType.MESSAGE;
            case "register" -> CommandType.REGISTER;
            case "function" -> CommandType.FUNCTION;

            // S2C
            case "room_log" -> CommandType.ROOM_LOG;
            default -> throw new RuntimeException("No enum of type: " + type);
        };
    }

    @Override
    public String toString() {
        return name().toLowerCase(); 
    }
}
