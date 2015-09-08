package advancedsystemsmanager.api.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ILeftClickItem
{
    boolean leftClick(EntityPlayer player, ItemStack stack, World world, int x, int y, int z, int face);
}
