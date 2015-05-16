package advancedsystemsmanager.blocks;


import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import advancedsystemsmanager.tileentities.TileEntityValve;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableIntake extends BlockCableDirectionAdvanced
{

    public BlockCableIntake()
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
        return isAdvanced ? "cable_intake_3" : "cable_intake_1";
    }

    @Override
    protected String getSideTextureName(boolean isAdvanced)
    {
        return isAdvanced ? "cable_intake_2" : "cable_intake";
    }

    @Override
    protected Class<? extends TileEntityClusterElement> getTeClass()
    {
        return TileEntityValve.class;
    }

}
