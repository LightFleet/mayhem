package com.norwood.cli;

enum Option {
    INVALID(-1),
    JOIN_ROOM(1),
    LIST_ROOMS(2),
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
