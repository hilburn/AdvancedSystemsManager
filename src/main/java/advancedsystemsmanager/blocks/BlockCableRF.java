package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityRFNode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCableRF extends BlockClusterElementBase<TileEntityRFNode>
{
    public BlockCableRF()
    {
        super(Names.CABLE_RF, 4);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityRFNode();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess iBlockAccess, int x, int y, int z, int side)
    {
        TileEntity te = iBlockAccess.getTileEntity(x, y, z);
        if (te instanceof TileEntityRFNode) return getIcon((TileEntityRFNode)te, side);
        return icons[3];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return icons[3];
    }

    @SideOnly(Side.CLIENT)
    private IIcon getIcon(TileEntityRFNode te, int side)
    {
        boolean in = te.isInput(side);
        boolean out = te.isOutput(side);
        if (in)
        {
            if (out) return icons[3];
            return icons[2];
        } else
        {
            if (out) return icons[1];
            return icons[0];
        }
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityRFNode;
    }
}
