package advancedfactorymanager.registry;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import advancedfactorymanager.commands.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry extends CommandBase
{
    public static Map<String, ISubCommand> commands = new LinkedHashMap<String, ISubCommand>();
    public static CommandRegistry instance = new CommandRegistry();

    static
    {
        register(CommandHelp.instance);
        register(CommandSave.instance);
        register(CommandLoad.instance);
        register(CommandClear.instance);
        register(CommandPastebin.instance);
    }

    public static void register(ISubCommand command)
    {
        commands.put(command.getCommandName(), command);
    }

    @Override
    public String getCommandName()
    {
        return "stevesaddons";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/" + getCommandName() + " help";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender)
    {
        return true;
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
            if (sender.canCommandSenderUseCommand(command.getPermissionLevel(), "stevesaddons " + command.getCommandName()) ||
                    (sender instanceof EntityPlayerMP && command.getPermissionLevel() <= 0))
            {
                command.handleCommand(sender, args);
                return;
            }
            throw new CommandException("commands.generic.permission");
        }
        throw new CommandNotFoundException("stevesaddons.command.notFound");
    }

    public static boolean commandExists(String name)
    {
        return commands.containsKey(name);
    }

    @Override
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

}
