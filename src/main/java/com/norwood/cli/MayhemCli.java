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
                    commandExecutor.joinRoom();
                    break;
                case EXIT:
                    System.out.println("Bye-bye!");
                    break outer;
                case INVALID:
                    System.out.println("Invalid control option.");
                    continue;
                default:
                    System.out.println("Unreacheable");
                    continue;
            }
        }
    }

    private Option parseCommand() {
        try {
            return Option.from(sc.nextInt());
        } catch (Exception e) {
            return Option.INVALID;
        }
    }

    private void renderControls() {
        System.out.print("\033[H\033[2J");
        System.out.println("--- Welcome to Mayhem ---");
        System.out.println();
        System.out.println("Enter command:");
        System.out.println("1) Join room");
        System.out.println("3) Exit");
        System.out.println();
    }
}
