package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public abstract class BlockCableDirectionAdvanced<T extends TileEntity & IClusterTile> extends BlockClusterElementBase<T>
{
    public BlockCableDirectionAdvanced(String name)
    {
        super(name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        icons = new IIcon[4];
        icons[0] = register.registerIcon(Reference.RESOURCE_LOCATION + ":" + getSideTextureName(false).replace(Names.PREFIX, ""));
        icons[1] = register.registerIcon(Reference.RESOURCE_LOCATION + ":" + getFrontTextureName(false).replace(Names.PREFIX, ""));
        icons[2] = register.registerIcon(Reference.RESOURCE_LOCATION + ":" + getSideTextureName(true).replace(Names.PREFIX, ""));
        icons[3] = register.registerIcon(Reference.RESOURCE_LOCATION + ":" + getFrontTextureName(true).replace(Names.PREFIX, ""));
    }

    protected abstract String getSideTextureName(boolean isAdvanced);

    protected abstract String getFrontTextureName(boolean isAdvanced);

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        int meta = world.getBlockMetadata(x, y, z);

        return getIconFromSideAndMeta(side, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        //pretend the meta is 3
        return getIconFromSideAndMeta(side, addAdvancedMeta(3, meta));
    }

    private int addAdvancedMeta(int meta, int advancedMeta)
    {
        return meta | (advancedMeta & 8);
    }

    @Override
    public int damageDropped(int meta)
    {
        return getAdvancedMeta(meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        int meta = addAdvancedMeta(BlockPistonBase.determineOrientation(world, x, y, z, entity), item.getItemDamage());

        IClusterTile element = getTileEntity(world, x, y, z);
        if (element != null)
        {
            element.setMetaData(meta);
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void getSubBlocks(Item item, CreativeTabs tabs, List list)
    {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 8));
    }

    private int getAdvancedMeta(int meta)
    {
        return addAdvancedMeta(0, meta);
    }

    @SideOnly(Side.CLIENT)
    private IIcon getIconFromSideAndMeta(int side, int meta)
    {
        return icons[(side == getSideMeta(meta) % ForgeDirection.VALID_DIRECTIONS.length ? 1 : 0) + (isAdvanced(meta) ? 2 : 0)];
        //return side == (getSideMeta(meta) % ForgeDirection.VALID_DIRECTIONS.length) ? isAdvanced(meta) ? advancedActiveIcon : activeIcon : isAdvanced(meta) ? advancedInactiveIcon : inactiveIcon;
    }

    public int getSideMeta(int meta)
    {
        return meta & 7;
    }

    public boolean isAdvanced(int meta)
    {
        return (meta & 8) != 0;
    }

}
