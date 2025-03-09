package com.norwood.communication;

public enum CommandType 
{
    MESSAGE, REGISTER, FUNCTION;
    
    public static CommandType from(String type) {
        return switch (type) {
            case "message" -> CommandType.MESSAGE;
            case "register" -> CommandType.REGISTER;
            case "function" -> CommandType.FUNCTION;
            default -> throw new RuntimeException("No enum of type: " + type);
        };
    }

    @Override
    public String toString() {
        return name().toLowerCase(); 
    }
}
