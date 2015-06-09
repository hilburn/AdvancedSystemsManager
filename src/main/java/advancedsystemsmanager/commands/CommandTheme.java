package advancedsystemsmanager.commands;

import advancedsystemsmanager.network.PacketHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;

public class CommandTheme implements ISubCommand
{
    @Override
    public int getPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "theme";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments)
    {
        if (sender instanceof EntityPlayerMP)
        {
            //TODO: Send a packet to the client.
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return Arrays.asList("list", "load", "save");
    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return true;
    }
}
