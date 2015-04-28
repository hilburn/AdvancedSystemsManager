package advancedsystemsmanager.blocks;


import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityOutput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableOutput extends BlockTileBase
{
    public BlockCableOutput()
    {
        super(Names.CABLE_OUTPUT, 2);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2)
    {
        return new TileEntityOutput();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }


    @Override
    public IIcon getIcon(int side, int meta)
    {
        return icons[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null && te.getStrengthFromSide(side) > 0)
        {
            return te.hasStrongSignalAtSide(side) ? icons[1] : icons[0];
        }
        return blockIcon;
    }


    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null)
        {
            return te.getStrengthFromOppositeSide(side);
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityOutput te = getTileEntity(world, x, y, z);
        if (te != null && te.hasStrongSignalAtOppositeSide(side))
        {
            return te.getStrengthFromOppositeSide(side);
        }

        return 0;
    }

    private TileEntityOutput getTileEntity(IBlockAccess world, int x, int y, int z)
    {
        return TileEntityCluster.getTileEntity(TileEntityOutput.class, world, x, y, z);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }
}
