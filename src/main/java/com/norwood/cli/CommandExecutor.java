package com.norwood.cli;

import com.norwood.Client;

public class CommandExecutor {

    private Client client;

    public void requestRoomsList() {
        client.requestRoomsList();
    }

    public void joinRoom(String roomName) {
        client.joinRoom(roomName);
    }

    public void setClient(Client client) {
        if (this.client != null) {
            throw new RuntimeException("Client is alredy set.");
        }
        this.client = client;
    }
}
