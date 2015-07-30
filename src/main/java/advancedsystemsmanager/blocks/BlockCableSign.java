package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntitySignUpdater;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCableSign extends BlockClusterElementBase<TileEntitySignUpdater>
{
    public BlockCableSign()
    {
        super(Names.CABLE_SIGN, 1);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntitySignUpdater();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        return getIconFromSideAndMeta(side, world.getBlockMetadata(x, y, z));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return getIconFromSideAndMeta(side, 3);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);
        TileEntitySignUpdater sign = getTileEntity(world, x, y, z);
        if (sign != null)
        {
            sign.setMetaData(meta);
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon getIconFromSideAndMeta(int side, int meta)
    {
        return side == meta ? icons[0] : blockIcon;
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntitySignUpdater;
    }
}
