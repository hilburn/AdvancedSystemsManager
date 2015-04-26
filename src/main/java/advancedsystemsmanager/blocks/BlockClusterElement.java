package advancedsystemsmanager.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockClusterElement extends BlockBase implements ITileEntityProvider
{
    protected BlockClusterElement(String name)
    {
        super(name);
        this.isBlockContainer = true;
    }

    protected BlockClusterElement(String name, int extraIcons)
    {
        super(name, extraIcons);
        this.isBlockContainer = true;
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        super.breakBlock(world, x, y, z, block, meta);
        world.removeTileEntity(x, y, z);
    }

    public boolean onBlockEventReceived(World world, int x, int y, int z, int p_149696_5_, int p_149696_6_)
    {
        super.onBlockEventReceived(world, x, y, z, p_149696_5_, p_149696_6_);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(p_149696_5_, p_149696_6_);
    }
}
