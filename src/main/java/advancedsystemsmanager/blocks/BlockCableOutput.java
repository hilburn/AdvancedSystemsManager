package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityEmitter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCableOutput extends BlockClusterElementBase<TileEntityEmitter>
{
    public BlockCableOutput()
    {
        super(Names.CABLE_OUTPUT, 2);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2)
    {
        return new TileEntityEmitter();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityEmitter te = getTileEntity(world, x, y, z);
        if (te != null && te.getStrengthFromSide(side) > 0)
        {
            return te.hasStrongSignalAtSide(side) ? icons[1] : icons[0];
        }
        return blockIcon;
    }

    @Override
    public IIcon getIcon(int side, int meta)
    {
        return icons[0];
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityEmitter te = getTileEntity(world, x, y, z);
        if (te != null)
        {
            return te.getStrengthFromOppositeSide(side);
        }
        return 0;
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityEmitter te = getTileEntity(world, x, y, z);
        if (te != null && te.hasStrongSignalAtOppositeSide(side))
        {
            return te.getStrengthFromOppositeSide(side);
        }

        return 0;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityEmitter;
    }
}
