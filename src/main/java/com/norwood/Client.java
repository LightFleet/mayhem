package com.norwood;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.norwood.communication.Command;
import com.norwood.communication.CommandType;
import com.norwood.communication.FunctionType;

public class Client 
{
    private String name;
    private Socket socket;
    private BlockingQueue<Command> commands = new LinkedBlockingQueue<>();
    private PrintWriter out;
    private BufferedReader reader;

    public Client(String name) {
        this.name = name;
        try {
            this.socket = new Socket("127.0.0.1", 8001);
            socket.setKeepAlive(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            this.reader = reader;
            this.out = out;
            // sendRegistration();

            while (true) {
                handleCommand(commands.take());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } 
    }

    private void handleCommand(Command command) {
        this.out.println(command.toString());
    }

    public void sendFunction(CommandType type, String content) {
        sendCommand(type, content + "|" + name);
    }

    public void sendRegistration() {
        sendCommand(CommandType.REGISTER, name);
    }

    public void sendMessage(String room, String message) {
        sendCommand(CommandType.MESSAGE, message + "|" + name + "|" + room);
    }

    public void sendCommand(CommandType type, String content) {
        commands.add(Command.from(type + "|" + content));
    }
}
