package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableBUD extends BlockTileBase
{
    public BlockCableBUD()
    {
        super(Names.CABLE_BUD);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBUD();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        TileEntityBUD bud = TileEntityCluster.getTileEntity(TileEntityBUD.class, world, x, y, z);
        if (bud != null)
        {
            bud.onTrigger();
        }
    }


}
