package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityValve;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableValve extends BlockCableDirectionAdvanced<TileEntityValve>
{

    public BlockCableValve()
    {
        super(Names.CABLE_VALVE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityValve();
    }

    @Override
    protected String getFrontTextureName(boolean isAdvanced)
    {
        return Names.CABLE_VALVE + (isAdvanced ? "_3" : "_1");
    }

    @Override
    protected String getSideTextureName(boolean isAdvanced)
    {
        return Names.CABLE_VALVE + (isAdvanced ? "_2" : "");
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityValve;
    }
}
