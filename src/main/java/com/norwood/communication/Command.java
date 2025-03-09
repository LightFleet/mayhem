package com.norwood.communication;

import lombok.Builder;

@Builder
public final class Command 
{
    public CommandType type;
    public String content;

    public static Command from(String messageStr) {
        System.out.println("building command: " + messageStr);
        String[] commandElements = messageStr.split("\\|");
        String content = commandElements[1];
        
        // intentionally left ugly (?); need to come up with a better protocol
        if (commandElements.length == 3) {
            content += "|" + commandElements[2];
        }
        if (commandElements.length == 4 ) {
            content += "|" + commandElements[2] + "|" + commandElements[3];
        }

        return builder()
            .type(CommandType.from(commandElements[0]))
            .content(content)
            .build();
    }

    @Override
    public String toString() {
        return type.toString().toString().toLowerCase() + "|" + content;
    }
    
    public String print() {
        return "Command [type=" + type + ", content=" + content + "]";
    }
}
