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
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityTCNode();
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityTCNode;
    }
}
