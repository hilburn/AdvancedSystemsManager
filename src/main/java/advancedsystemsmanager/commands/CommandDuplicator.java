package advancedsystemsmanager.commands;

import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public abstract class CommandDuplicator implements ISubCommand
{
    public static NBTTagCompound defaultTagCompound = new NBTTagCompound();
    public static String[] keys = {"id", "Variables", "Components", "Timer", "ProtocolVersion", "ench"};

    static
    {
        defaultTagCompound.setTag("ench", new NBTTagList());
    }

    public static ItemStack getDuplicator(ICommandSender sender)
    {
        if (sender instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = CommandBase.getCommandSenderAsPlayer(sender);
            ItemStack stack = player.inventory.getCurrentItem();
            if (stack != null && stack.getItem() == ItemRegistry.duplicator) return stack;
        }
        return null;
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments)
    {
        if (!isVisible(sender))
        {
            throw new CommandException("asm.command.noPermission");
        }
        ItemStack duplicator = getDuplicator(sender);
        if (duplicator != null)
        {
            doCommand(duplicator, CommandBase.getCommandSenderAsPlayer(sender), arguments);
        } else
        {
            throw new CommandException("asm.command.noDuplicator");
        }
    }

    public static NBTTagCompound stripBaseNBT(NBTTagCompound tagCompound)
    {
        for (String key : keys)
        {
            if (tagCompound.hasKey(key) && tagCompound.getTag(key).equals(defaultTagCompound.getTag(key)))
                tagCompound.removeTag(key);
        }
        return tagCompound;
    }

    public static NBTTagCompound unstripBaseNBT(NBTTagCompound tagCompound)
    {
        for (String key : keys)
        {
            if (!tagCompound.hasKey(key)) tagCompound.setTag(key, defaultTagCompound.getTag(key));
        }
        return tagCompound;
    }

    public abstract void doCommand(ItemStack duplicator, EntityPlayerMP sender, String[] arguments);

    @Override
    public int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return sender.canCommandSenderUseCommand(getPermissionLevel(), getCommandName());
    }
}
