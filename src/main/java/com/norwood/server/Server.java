package com.norwood.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import com.norwood.journal.Journal;
import com.norwood.server.Command.Fields;
import com.norwood.server.CommandExecutor.ServerCallbackType;

public class Server
{
    private boolean running = true;

    private CommandExecutor executor = new CommandExecutor();
    private ServerActions serverActions = new ServerActions();
    public static Journal journal = Journal.getInstance();

    Map<String, Socket> userToSocket = new HashMap<>();
    public final Set<String> knownUsers = new HashSet<>();

    public void run() {
        registerCallbacks();
        try (ServerSocket socket = new ServerSocket(8001)) {
            Server.journal.addServerRecord("Started server. Listening to 8001");
            while (running) {
                try {
                    Socket clientSocket = socket.accept(); 
                    Server.journal.addServerRecord("New connection from a client: " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (Exception e) {
                    Server.journal.addServerRecord("Error during client handling: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Server.journal.addServerRecord("Error starting server: " + e.getMessage());
        }
    }

    private void registerCallbacks() {
        executor.registerCallback(
            ServerCallbackType.SEND_MESSAGE,
            (BiConsumer<String, String>) (userName, content) -> {
                serverActions.sendMessageToClient(userName, content);
            });
    }

    public void stop() {
        running = false;
    }

    private void handleClient(Socket socket) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                if (message.isEmpty()) {
                    throw new RuntimeException("Message is empty");
                }

                if (isRegister(message)) {
                    Server.journal.addServerRecord("Trying to register new user.");
                    serverActions.tryToRegister(message, socket);
                    continue;
                } 
                executor.execute(message);
            }
        } catch (Exception e) {
            Server.journal.addServerRecord("Error while reading or writing from a socket: " + e.getMessage());
        } finally {
        }
    }

    private boolean isRegister(String message) {
        String type = CommandFactory.parse(message).get(Fields.type);
        return Command.REGISTER.toString().equals(type);
    }
}
