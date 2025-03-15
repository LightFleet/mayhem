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
    private boolean running = true;
    private String name;
    private Socket socket;
    private BlockingQueue<String> commands = new LinkedBlockingQueue<>();
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

            while (running) {
                handleCommand(commands.take());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } 
    }

    private void handleCommand(String command) {
        this.out.println(command);
    }

    void stop() {
        running = false;
    }

    // public void sendFunction(CommandType type, String content) {
    //     sendCommand(type, content + "|" + name);
    // }
    //
    // public void sendRegistration() {
    //     sendCommand(CommandType.REGISTER, name);
    // }
    //
    // public void sendMessage(String command) {
    //     sendCommand(CommandType.MESSAGE, message + "|" + name + "|" + room);
    // }

    public void sendCommand(String content) {
        commands.add(content);
    }
}
