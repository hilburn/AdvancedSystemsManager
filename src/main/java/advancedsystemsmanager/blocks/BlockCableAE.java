package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityAENode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableAE extends BlockTileBase
{
    public BlockCableAE()
    {
        super(Names.CABLE_AE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityAENode();
    }
}
