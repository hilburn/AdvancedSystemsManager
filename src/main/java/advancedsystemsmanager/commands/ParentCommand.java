package advancedsystemsmanager.commands;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.gui.TextColour;
import advancedsystemsmanager.network.ASMPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParentCommand extends CommandBase
{
    public static Map<String, ISubCommand> commands = new LinkedHashMap<String, ISubCommand>();
    public static ParentCommand instance = new ParentCommand();
    public static List<IPacketSync> packetCommands = new ArrayList<IPacketSync>();

    static
    {
        register(CommandHelp.instance);
        register(CommandSave.instance);
        register(CommandLoad.instance);
        register(CommandClear.instance);
        register(CommandPastebin.instance);
        register(new CommandTheme());
    }

    public static void register(ISubCommand command)
    {
        commands.put(command.getCommandName(), command);
        if (command instanceof IPacketSync)
        {
            ((IPacketSync)command).setId(packetCommands.size());
            packetCommands.add((IPacketSync)command);
        }
    }

    public static void handlePacket(ASMPacket packet)
    {
        try
        {
            int id = packet.readByte();
            packetCommands.get(id % packetCommands.size()).readData(packet);
        } catch (CommandException e)
        {
            String message = e.getMessage();
            Object[] objects = e.getErrorOjbects();
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(TextColour.DARK_RED + (objects.length == 0? StatCollector.translateToLocal(message) : StatCollector.translateToLocalFormatted(message, objects))));
        }
    }

    public static boolean commandExists(String name)
    {
        return commands.containsKey(name);
    }

    @Override
    public String getCommandName()
    {
        return "asm";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + getCommandName() + " help";
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {

        if (args.length == 1)
        {
            String subCommand = args[0];
            List result = new ArrayList();
            for (ISubCommand command : commands.values())
            {
                if (command.isVisible(sender) && command.getCommandName().startsWith(subCommand))
                    result.add(command.getCommandName());
            }
            return result;
        } else if (commands.containsKey(args[0]) && commands.get(args[0]).isVisible(sender))
        {
            return commands.get(args[0]).addTabCompletionOptions(sender, args);
        }
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        if (args.length < 1)
        {
            args = new String[]{"help"};
        }
        ISubCommand command = commands.get(args[0]);
        if (command != null)
        {
            if (sender.canCommandSenderUseCommand(command.getPermissionLevel(), "asm " + command.getCommandName()) ||
                    (sender instanceof EntityPlayerMP && command.getPermissionLevel() <= 0))
            {
                command.handleCommand(sender, args);
                return;
            }
            throw new CommandException("commands.generic.permission");
        }
        throw new CommandNotFoundException("asm.command.notFound");
    }
}
