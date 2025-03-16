package com.norwood.journal;

public record Record(String timestamp, String user, String context, String content) {
    public boolean isRoom() { return !context().equals("Server"); }
    public boolean ofRoom(String roomName) { return context.equals(roomName); }
}
