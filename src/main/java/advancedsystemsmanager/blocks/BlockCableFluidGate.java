package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityBaseGate;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityFluidGate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCableFluidGate extends BlockCableGate
{

    public BlockCableFluidGate()
    {
        super(Names.CABLE_FLUID_GATE, 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityBaseGate gate = TileEntityCluster.getTileEntity(TileEntityBaseGate.class, world, x, y, z);

        if (gate != null)
        {
            int meta = gate.getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length;

            if (side == meta)
            {
                return icons[0];
            }
        }

        return blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityFluidGate();
    }
}
