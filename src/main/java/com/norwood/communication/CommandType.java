package com.norwood.communication;

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
