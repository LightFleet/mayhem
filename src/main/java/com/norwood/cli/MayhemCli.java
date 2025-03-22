package com.norwood.cli;

import java.util.Scanner;

import com.norwood.Client;

public class MayhemCli {
    Scanner sc = new Scanner(System.in);
    CommandExecutor commandExecutor = new CommandExecutor();
    private Client client;

    public void run() {
        setupClient();
        renderControls();
        mainLoop();

        System.out.println("Stopping client..");
        System.exit(0); 
    }

    private void setupClient() {
        this.client = new Client("Alisa");
        client.run();
        commandExecutor.setClient(client);
    }

    private void mainLoop() {
        outer: while (true) {
            System.out.print(">:");

            switch (parseCommand()) {
                case JOIN_ROOM:
                    System.out.print("Enter room name: ");
                    String roomName = sc.nextLine();
                    System.out.println("Entering room " + roomName);
                    commandExecutor.joinRoom(roomName);
                    break;
                case LIST_ROOMS:
                    commandExecutor.requestRoomsList();
                    break;
                case EXIT:
                    System.out.println("Bye-bye!");
                    break outer;
                case INVALID:
                    System.out.println("Invalid control option.");
                    break;
                default:
                    System.out.println("Unreacheable");
                    continue;
            }
        }
    }

    private Option parseCommand() {
        try {
            return Option.from(Integer.parseInt(sc.nextLine()));
        } catch (Exception e) {
            return Option.INVALID;
        }
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");
    } 

    private void renderControls() {
        clear();
        System.out.println("--- Welcome to Mayhem ---");
        System.out.println();
        System.out.println("Enter command:");
        System.out.println("1) Join room");
        System.out.println("2) List rooms");
        System.out.println("3) Exit");
        System.out.println();
    }
}
