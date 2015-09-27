package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.api.tileentities.ITileElement;
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
import net.minecraft.entity.player.EntityPlayer;
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
    private String[][] iconNames;
    private Class<? extends TileEntityElementBase> tileClass;
    @SideOnly(Side.CLIENT)
    protected IIcon[][] icons;
    private int metadata;

    public TileFactory(Class<? extends TileEntityElementBase> tileClass, String[] subtypes, String... iconNames)
    {
        this.tileClass = tileClass;
        this.iconNames = new String[subtypes.length][iconNames.length + 1];
        for (int i = 0; i < subtypes.length; i++)
        {
            this.iconNames[i][0] = subtypes[i];
            for (int j = 0; j < iconNames.length; j++)
            {
                this.iconNames[i][j+1] = this.iconNames[i][0] + iconNames[j];
            }
        }
        GameRegistry.registerTileEntity(tileClass, subtypes[0]);
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
    public String getUnlocalizedName(int subtype)
    {
        return iconNames[subtype][0];
    }

    @Override
    public String getKey()
    {
        return iconNames[0][0];
    }

    public ItemStack getItemStack()
    {
        return getItemStack(0);
    }

    @Override
    public ItemStack getItemStack(int subtype)
    {
        return new ItemStack(getBlock(), 1, getMetadata() + subtype * 16);
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
                return tileClass.newInstance();
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
        icons = new IIcon[iconNames.length][iconNames[0].length];
        for (int i = 0; i < icons.length; i++)
            for (int j = 0; j < icons[0].length; j++)
                icons[i][j] = register.registerIcon(getTextureName(iconNames[i][j]));
    }

    @SideOnly(Side.CLIENT)
    private static String getTextureName(String iconName)
    {
        return Reference.RESOURCE_LOCATION + ":" + iconName.replace(Names.PREFIX, "");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int subtype)
    {
        return icons[subtype][0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list)
    {
        for (int i = 0; i < iconNames.length; i++)
            list.add(getItemStack(i));
    }

    @Override
    public boolean canPlaceBlock(World world, int x, int y, int z, ItemStack stack)
    {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon[] getIcons(int subtype)
    {
        return icons[subtype];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> information, boolean advanced)
    {

    }

    public static class Cluster extends TileFactory
    {
        public Cluster(Class<? extends TileEntityElementBase> tileClass, String[] subType, String... iconNames)
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
        public Interface(Class<? extends TileEntityElementBase> tileClass, String[] subType, String... iconNames)
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
        public Directional(Class<? extends TileEntityElementBase> tileClass, String[] subType, String... iconNames)
        {
            super(tileClass, subType, iconNames);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getIcon(int side, int subtype)
        {
            return super.icons[subtype][side == 3 ? 0 : 1];
        }
    }
}
