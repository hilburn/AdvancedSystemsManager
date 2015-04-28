package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemManager extends ItemBlock
{
    public ItemManager(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;
        if (stack.hasTagCompound())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                ((TileEntityManager)te).readContentFromNBT(stack.getTagCompound(), false);
            }
        }
        return true;
    }
}
