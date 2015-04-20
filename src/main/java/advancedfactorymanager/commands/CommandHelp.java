package advancedfactorymanager.commands;

import advancedfactorymanager.helpers.LocalizationHelper;
import advancedfactorymanager.registry.CommandRegistry;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class CommandHelp implements ISubCommand
{

    public static CommandHelp instance = new CommandHelp();
    public static final String PREFIX = "\u00A7";//§
    public static final String YELLOW = PREFIX + "e";
    public static final String WHITE = PREFIX + "f";

    /* ISubCommand */
    @Override
    public String getCommandName()
    {

        return "help";
    }

    @Override
    public int getPermissionLevel()
    {

        return -1;
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments)
    {

        switch (arguments.length)
        {
            case 1:
                StringBuilder output = new StringBuilder(LocalizationHelper.translate("stevesaddons.command.info.help.start") + " ");
                List<String> commands = new ArrayList<String>();
                for (ISubCommand command : CommandRegistry.commands.values())
                {
                    if (command.isVisible(sender)) commands.add(command.getCommandName());
                }

                for (int i = 0; i < commands.size() - 1; i++)
                {
                    output.append("/stevesaddons " + YELLOW + commands.get(i) + WHITE + ", ");
                }
                output.delete(output.length() - 2, output.length());
                output.append(" and /stevesaddons " + YELLOW + commands.get(commands.size() - 1) + WHITE + ".");
                sender.addChatMessage(new ChatComponentText(output.toString()));
                break;
            case 2:
                String commandName = arguments[1];
                if (!CommandRegistry.commandExists(commandName))
                {
                    throw new CommandNotFoundException("stevesaddons.command.notFound");
                }
                sender.addChatMessage(new ChatComponentText(LocalizationHelper.translate("stevesaddons.command.info." + commandName)));
                sender.addChatMessage(new ChatComponentText(LocalizationHelper.translate("stevesaddons.command." + commandName + ".syntax")));
                break;
            default:
                throw new WrongUsageException("stevesaddons.command." + getCommandName() + ".syntax");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {

        if (args.length == 2)
        {
            return CommandRegistry.instance.addTabCompletionOptions(sender, new String[]{args[1]});
        }
        return null;

    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return true;
    }

}
