package com.norwood.cli;

import java.util.Scanner;

public class MayhemCli {
    enum Option {
        // LIST_ROOMS,
        JOIN_ROOM(1),
        // SEND_DM,
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

    public void run() {
        renderControls();
        outer: while (true) {
            // clean();
            System.out.print(">:");

            Option input;
            try {
                input = Option.from(sc.nextInt());
            } catch (Exception e) {
                System.out.println("Invalid control option.");
                continue;
            }

            switch (input) {
                case JOIN_ROOM:
                    System.out.println("lalala");
                    break;
                case EXIT:
                    System.out.println("Bye-bye!");
                    break outer;
                default:
                    break outer;
            }
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
