package advancedsystemsmanager.commands;

import advancedsystemsmanager.gui.TextColour;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class CommandColour implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "colour";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments)
    {
        String colour = arguments[1];
        sender.addChatMessage(new ChatComponentText(TextColour.getClosestColour(Integer.decode(colour)).toString() + colour));
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return true;
    }
}
