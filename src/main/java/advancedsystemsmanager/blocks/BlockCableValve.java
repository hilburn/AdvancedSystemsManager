package advancedsystemsmanager.blocks;


import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import advancedsystemsmanager.tileentities.TileEntityValve;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableValve extends BlockCableDirectionAdvanced
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
    protected Class<? extends TileEntityClusterElement> getTeClass()
    {
        return TileEntityValve.class;
    }

}
