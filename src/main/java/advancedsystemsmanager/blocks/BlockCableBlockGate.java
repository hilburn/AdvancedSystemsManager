package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityBaseGate;
import advancedsystemsmanager.tileentities.TileEntityBlockGate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCableBlockGate extends BlockCableGate<TileEntityBlockGate>
{

    public BlockCableBlockGate()
    {
        super(Names.CABLE_BLOCK_GATE, 3);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityBaseGate gate = getTileEntity(world, x, y, z);

        if (gate != null)
        {
            int meta = gate.getMetadata() % ForgeDirection.VALID_DIRECTIONS.length;
            int direction = gate.getPlaceDirection();

            if (side == meta && side == direction)
            {
                return icons[0];
            } else if (side == meta)
            {
                return icons[1];
            } else if (side == direction)
            {
                return icons[2];
            }
        }

        return blockIcon;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBlockGate();
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityBlockGate;
    }
}
