package advancedsystemsmanager.commands;

import advancedsystemsmanager.helpers.PastebinHelper;
import advancedsystemsmanager.items.ItemDuplicator;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandPastebin extends CommandDuplicator
{
    public static final Set<String> usernameWhitelist = new HashSet<String>();
    public static CommandPastebin instance = new CommandPastebin();
    private static String apiKey = "367773fafa3565615286cf270e73f3de";
    private static ClipboardOwner clippy = new ClipboardOwner()
    {
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {

        }
    };

    @Override
    public void doCommand(ItemStack duplicator, EntityPlayerMP sender, String[] arguments)
    {
        if (arguments.length < 2)
        {
            throw new WrongUsageException("asm.command." + getCommandName() + ".syntax");
        }
        try
        {

            if (arguments[1].equals("put"))
            {
                if (ItemDuplicator.validateNBT(duplicator) && duplicator.hasTagCompound())
                {
                    new Thread(new PastebinHelper.Put(duplicator, sender, arguments)).start();
                } else
                {
                    throw new CommandException("asm.command.nothingToSave");
                }
            } else if (arguments[1].equals("get"))
            {
                if (arguments.length < 3)
                {
                    throw new WrongUsageException("stevesaddons.command." + getCommandName() + ".syntax");
                }
                new Thread(new PastebinHelper.Set(duplicator, sender, arguments)).start();
            } else
            {
                throw new WrongUsageException("asm.command." + getCommandName() + ".syntax");
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getCommandName()
    {
        return "pastebin";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return isVisible(sender) ? Arrays.asList("put", "get") : null;
    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return usernameWhitelist.contains(sender.getCommandSenderName()) || Minecraft.getMinecraft().isIntegratedServerRunning();
    }
}
