package advancedfactorymanager.commands;

import net.minecraft.command.ICommandSender;

import java.util.List;

public interface ISubCommand
{

    public int getPermissionLevel();

    public String getCommandName();

    public void handleCommand(ICommandSender sender, String[] arguments);

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args);

    public boolean isVisible(ICommandSender sender);
}
