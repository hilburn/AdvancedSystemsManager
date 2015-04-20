package advancedfactorymanager.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CommandClear extends CommandDuplicator
{
    public static CommandClear instance = new CommandClear();

    @Override
    public void doCommand(ItemStack duplicator, EntityPlayerMP sender, String[] arguments)
    {
        duplicator.setTagCompound(null);
    }

    @Override
    public int getPermissionLevel()
    {
        return -1;
    }

    @Override
    public String getCommandName()
    {
        return "clear";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }
}
