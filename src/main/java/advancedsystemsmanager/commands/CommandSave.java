package advancedsystemsmanager.commands;

import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.items.ItemDuplicator;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.DimensionManager;

import java.io.File;
import java.util.List;

public class CommandSave extends CommandDuplicator
{
    public static CommandSave instance = new CommandSave();

    @Override
    public String getCommandName()
    {
        return "save";
    }


    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return null;
    }

    @Override
    public void doCommand(ItemStack duplicator, EntityPlayerMP sender, String[] arguments)
    {
        try
        {
            if (ItemDuplicator.validateNBT(duplicator) && duplicator.hasTagCompound())
            {
                String name = arguments.length == 2 ? arguments[1] : sender.getCommandSenderName();
                File file = new File(DimensionManager.getCurrentSaveRootDirectory().getPath() + File.separator + "managers" + File.separator + name + ".nbt");
                if (!file.exists()) file.createNewFile();
                NBTTagCompound tagCompound = (NBTTagCompound)duplicator.getTagCompound().copy();
                tagCompound.removeTag("x");
                tagCompound.removeTag("y");
                tagCompound.removeTag("z");
                tagCompound.setString("Author", sender.getCommandSenderName());
                CompressedStreamTools.write(stripBaseNBT(tagCompound), file);
                CommandBase.getCommandSenderAsPlayer(sender).addChatComponentMessage(new ChatComponentText(LocalizationHelper.translateFormatted("asm.command.savedTo", name + ".nbt")));
            } else
            {
                throw new CommandException("asm.command.nothingToSave");
            }
        } catch (Exception e)
        {
            throw new CommandException("asm.command.saveFailed");
        }
    }
}
