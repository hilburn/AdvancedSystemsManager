package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityCreative;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableCreative extends BlockClusterElementBase<TileEntityCreative>
{
    public BlockCableCreative()
    {
        super(Names.CABLE_CREATIVE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCreative();
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityCreative;
    }
}