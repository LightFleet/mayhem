package com.norwood.communication;

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
