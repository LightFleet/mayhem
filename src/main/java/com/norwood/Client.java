package com.norwood;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.norwood.cli.MayhemCli;
import com.norwood.server.Command;
import com.norwood.server.Command.Fields;
import com.norwood.server.CommandFactory;

public class Client 
{
    private boolean running = true;
    private String userName;
    private Socket socket;
    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private PrintWriter out;
    private BufferedReader reader;
    private String currentRoom;
    private boolean registered = false;

    public Client(String userName) {
        this.userName = userName;
        try {
            this.socket = new Socket("127.0.0.1", 8001);
            socket.setKeepAlive(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        new Thread(() -> {
            try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            ) {
                this.out = out;

                while (running) {
                    handleCommand(commands.take());
                }
            } catch (Exception e) {
                System.err.println("Sending Error: " + e.getMessage());
                System.exit(1);
            } 
        }).start();

        new Thread(() -> {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                this.reader = reader;
                while (running) {
                    handleIncomingCommand();
                }
            } catch (Exception e) {
                System.err.println("Reading Error: " + e.getMessage());
                System.exit(1);
            } 
        }).start();
    }

    private void handleIncomingCommand() {
        try {
            String message = reader.readLine();
            if (message == null) {
                return;
            }
            Map<String, String> fields =  CommandFactory.parse(message);
            switch (Command.from(fields.get(Fields.type))) {
                case SROOMS:
                    System.out.println("Available rooms: " + fields.get(Fields.message));
                    break;
                case SMESSAGE:
                    MayhemCli.clear();
                    System.out.println("--- Room log ---");
                    for (String record : fields.get(Fields.message).split("\\|")) {
                        String[] messageParts = record.split(",");
                        String timestamp = messageParts[0];
                        String user = messageParts[1];
                        String context = messageParts[2];
                        String content = messageParts[3];
                        System.out.println(user.split("=")[1] + ": " + content.split("=")[1]);
                        System.out.print(">:");
                    }
                    break;
                default:
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String command) {
        this.out.println(command);
    }

    public void stop() {
        running = false;
    }

    public void sendMessage(String message) {
        sendCommand(CommandFactory.message(userName, message, currentRoom));
    }
 
    public void joinRoom(String roomName) {
        sendCommand(CommandFactory.joinRoom(userName, roomName));
        currentRoom = roomName; // what if something goes wrong? No such room?
    }

    public void createRoom(String roomName) {
        sendCommand(CommandFactory.createRoom(userName, roomName));
        currentRoom = roomName;
    }

    public void sendCommand(String content) {
        if (!registered) {
            commands.add(CommandFactory.register(userName));
            registered = true;
        }
        commands.add(content);
    }

    public void requestRoomsList() {
        sendCommand(CommandFactory.requestRoomsList(userName));
    }
}
