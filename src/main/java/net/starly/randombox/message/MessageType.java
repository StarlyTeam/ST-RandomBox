package net.starly.randombox.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MessageType {
    NORMAL("messages.alphachest"), ERROR("errorMessages.alphachest");

    @Getter
    private final String path;
}
