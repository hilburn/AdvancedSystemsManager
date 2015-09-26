package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.ITileFactory;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.api.tiletypes.ITileElement;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityElementBase;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class TileFactory implements ITileFactory
{
    private Block block;
    private String[] iconNames;
    private Class<? extends TileEntityElementBase> tileClass;
    private int subType;
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;
    private int metadata;

    public TileFactory(Class<? extends TileEntityElementBase> tileClass, int subType, String... iconNames)
    {
        this.tileClass = tileClass;
        this.subType = subType;
        this.iconNames = new String[iconNames.length];
        this.iconNames[0] = iconNames[0];
        for (int i = 1; i < iconNames.length; i++)
        {
            this.iconNames[i] = iconNames[0] + iconNames[i];
        }
        GameRegistry.registerTileEntity(tileClass, iconNames[0]);
    }

    @Override
    public Block getBlock()
    {
        return block;
    }

    @Override
    public void setBlock(Block block)
    {
        this.block = block;
    }

    @Override
    public int getMetadata()
    {
        return metadata;
    }

    @Override
    public void setMetadata(int metadata)
    {
        this.metadata = metadata;
    }

    @Override
    public boolean canBeAddedToCluster(List<ITileElement> clusterElements)
    {
        return false;
    }

    @Override
    public String getUnlocalizedName()
    {
        return iconNames[0];
    }

    @Override
    public ItemStack getItemStack()
    {
        return new ItemStack(getBlock(), 1, getMetadata());
    }

    @Override
    public boolean hasTileEntity()
    {
        return tileClass != null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (hasTileEntity())
        {
            try
            {
                TileEntityElementBase tile = tileClass.newInstance();
                tile.setSubtype(subType);
                return tile;
            } catch (Exception ignored)
            {
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends TileEntity> T getTileEntity(IBlockAccess world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (isInstance(tileEntity))
        {
            return (T) tileEntity;
        } else if (tileEntity instanceof TileEntityCluster)
        {
            for (ITileElement tile : ((TileEntityCluster) tileEntity).getTiles())
            {
                if (isInstance(tile))
                {
                    return (T) tile;
                }
            }
        }
        return null;
    }

    @Override
    public boolean isInstance(TileEntity tileEntity)
    {
        return tileClass.isInstance(tileEntity);
    }

    public boolean isInstance(ITileElement tileEntity)
    {
        return tileClass.isInstance(tileEntity);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register)
    {
        icons = new IIcon[iconNames.length];
        for (int i = 0; i < icons.length; i++)
            icons[i] = register.registerIcon(getTextureName(iconNames[i]));
    }

    @SideOnly(Side.CLIENT)
    private static String getTextureName(String iconName)
    {
        return Reference.RESOURCE_LOCATION + ":" + iconName.replace(Names.PREFIX, "");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side)
    {
        return icons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        list.add(new ItemStack(item, 1, metadata));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon[] getIcons()
    {
        return icons;
    }

    public static class Cluster extends TileFactory
    {
        public Cluster(Class<? extends TileEntityElementBase> tileClass, int subType, String... iconNames)
        {
            super(tileClass, subType, iconNames);
        }

        @Override
        public boolean canBeAddedToCluster(List<ITileElement> clusterElements)
        {
            for (ITileElement tile : clusterElements)
            {
                if (isInstance(tile)) return false;
            }
            return true;
        }
    }

    public static class Interface extends TileFactory
    {
        public Interface(Class<? extends TileEntityElementBase> tileClass, int subType, String... iconNames)
        {
            super(tileClass, subType, iconNames);
        }

        @Override
        public boolean canBeAddedToCluster(List<ITileElement> clusterElements)
        {
            for (ITileElement tile : clusterElements)
            {
                if (isInstance(tile) || tile instanceof ITileInterfaceProvider) return false;
            }
            return true;
        }
    }

    public static class Directional extends Cluster
    {
        public Directional(Class<? extends TileEntityElementBase> tileClass, int subType, String... iconNames)
        {
            super(tileClass, subType, iconNames);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getIcon(int side)
        {
            return side == 3 ? super.icons[0] : super.icons[1];
        }
    }
}
