package advancedsystemsmanager.commands;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;

public class CommandTheme implements ISubCommand, IPacketSync
{
    private static List<String> commands = Arrays.asList("list", "load", "save");
    int id;

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
            ASMPacket packet = PacketHandler.getCommandPacket();
            packet.writeByte(id);
            packet.writeByte(commands.indexOf(arguments[1]));
            packet.writeByte(Math.max(arguments.length - 2, 0));
            for (int i = 2; i < arguments.length; i++)
            {
                packet.writeStringToBuffer(arguments[i]);
            }
            packet.sendPlayerPacket((EntityPlayerMP)sender);
        } else
        {
            throw new CommandException("Command can only be used by a player");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return commands;
    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return true;
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        int command = packet.readByte();
        String[] args = new String[packet.readByte()];
        for (int i = 0; i < args.length; i++)
        {
            args[i] = packet.readStringFromBuffer();
        }
        return false;
    }
}
