package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityFluidGate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCableFluidGate extends BlockClusterElementBase<TileEntityFluidGate>
{

    public BlockCableFluidGate()
    {
        super(Names.CABLE_FLUID_GATE, 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return side == 3 ? icons[0] : blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityFluidGate gate = getTileEntity(world, x, y, z);

        if (gate != null)
        {
            int meta = gate.getMetadata() % ForgeDirection.VALID_DIRECTIONS.length;

            if (side == meta)
            {
                return icons[0];
            }
        }

        return blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        TileEntityFluidGate gate = getTileEntity(world, x, y, z);
        if (gate != null)
        {
            gate.onNeighbourBlockChange();
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntityFluidGate gate = getTileEntity(world, x, y, z);
        if (gate != null)
        {
            gate.setMetaData(meta);
        }
    }


    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityFluidGate();
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityFluidGate;
    }
}
