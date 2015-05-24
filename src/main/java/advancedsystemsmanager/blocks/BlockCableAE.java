package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityAENode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableAE extends BlockClusterElementBase<TileEntityAENode>
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

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityAENode;
    }
}
