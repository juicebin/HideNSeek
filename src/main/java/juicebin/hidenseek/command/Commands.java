package juicebin.hidenseek.command;

import juicebin.hidenseek.HideNSeek;

import java.util.List;

public final class Commands {
    private final List<RegisteredCommand> commandList;

    public Commands(RegisteredCommand... commands) {
        this.commandList = List.of(commands);
    }

    public void register(HideNSeek instance) {
        for (RegisteredCommand command : commandList) {
            command.register(instance);
        }
    }

}
