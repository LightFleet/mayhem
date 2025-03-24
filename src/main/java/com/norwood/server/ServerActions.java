package com.norwood.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.norwood.server.Command.Fields;

class ServerActions
{
    private static ServerInfo serverInfo = new ServerInfo();
    Map<String, Socket> userToSocket = new HashMap<>();

    static private class ServerInfo {
        public static final int MAX_ROOMS_PER_CLIENT = 5;

        public final Map<String, Integer> roomsByClient = new HashMap<>();
        public final Set<String> knownUsers = new HashSet<>();
    }

    void tryToRegister(String message, Socket socket) throws InterruptedException {
        Map<String, String> fields = CommandFactory.parse(message);
        String user = fields.get(Fields.user);

        if (serverInfo.knownUsers.contains(user)) {
            return;
        }

        Server.journal.addServerRecord(String.format("User %s registered", user));
        userToSocket.put(user, socket);
        serverInfo.knownUsers.add(user);
    }

    void sendMessageToClient(String userName, String content) {
        Socket socket = userToSocket.get(userName);
        if (socket == null || socket.isClosed()) {
            throw new RuntimeException("Can't send message to client. Socket is closed. Client: " + userName);
        }
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(content);
        } catch (IOException e) {
            throw new RuntimeException("Can't send message to client. Error writing. Client: " + userName);
        }
    }
}
