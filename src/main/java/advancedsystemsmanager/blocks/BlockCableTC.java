package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityTCNode;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableTC extends BlockClusterElementBase<TileEntityTCNode>
{
    public BlockCableTC()
    {
        super(Names.CABLE_TC);
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityTCNode;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityTCNode();
    }
}
