package advancedsystemsmanager.api.tileentities;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public interface IClusterElement<T extends TileEntity & IClusterTile>
{
    ItemStack getItemStack(int metadata);

    Block getBlock();

    T getClusterTile(World world, int metadata);

    boolean isInstance(IClusterTile tile);

    byte getId();
}
