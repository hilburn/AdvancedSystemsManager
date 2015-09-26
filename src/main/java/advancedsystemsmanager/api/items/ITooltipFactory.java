package advancedsystemsmanager.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ITooltipFactory
{
    void addInformation(ItemStack stack, EntityPlayer player, List<String> information, boolean advanced);
}
