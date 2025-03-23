package com.norwood.cli;

import java.util.Scanner;

import com.norwood.Client;

public class MayhemCli {
    enum Option {
        INVALID(-1),
        JOIN_ROOM(1),
        LIST_ROOMS(2),
        EXIT(3);

        private final int code;

        Option(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        static Option from(int nextInt) {
            for (Option opt : Option.values()) {
                if (opt.getCode() == nextInt) {
                    return opt;
                }
            }
            throw new IllegalArgumentException("Invalid code: " + nextInt);
        }
    }

    Scanner sc = new Scanner(System.in);
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
    }

    private void mainLoop() {
        outer: while (true) {
            System.out.print(">:");

            switch (parseCommand()) {
                case JOIN_ROOM:
                    client.joinRoom(sc.nextLine());
                    break;
                case LIST_ROOMS:
                    client.requestRoomsList();
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
