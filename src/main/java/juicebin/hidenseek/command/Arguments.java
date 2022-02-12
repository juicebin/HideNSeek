package juicebin.hidenseek.command;

import juicebin.hidenseek.util.MessageLevel;
import juicebin.hidenseek.util.MessageUtils;
import org.bukkit.command.CommandSender;

public class Arguments {

    public static int requireInt(CommandSender sender, String[] args, int index) {
        int i = 0;

        try {
            i = Integer.parseInt(args[index]);
        } catch (NumberFormatException exception) {
            sendErrorMessage(sender, index, "integer", "0");
        }

        return i;
    }

    private static void sendErrorMessage(CommandSender sender, int index, String type, String defaultValue) {
        MessageUtils.sendMessage(sender, MessageLevel.ERROR,"Argument " + index + " needs to be an " + type + " type... defaulting to " + defaultValue);
    }

}
