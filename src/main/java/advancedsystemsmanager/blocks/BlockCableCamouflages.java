package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

public class BlockCableCamouflages extends BlockCamouflageBase
{

    public BlockCableCamouflages()
    {
        super(Names.CABLE_CAMO);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCamouflage();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        icons = new IIcon[TileEntityCamouflage.CamouflageType.values().length];
        for (int i = 0; i < icons.length; i++)
        {
            icons[i] = register.registerIcon(Reference.RESOURCE_LOCATION + ":" + TileEntityCamouflage.CamouflageType.values()[i].getIcon().replace("system_", ""));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return getDefaultIcon(side, meta, meta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getDefaultIcon(int side, int blockMeta, int camoMeta)
    {
        return icons[camoMeta % icons.length];
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        TileEntityCamouflage camouflage = getTileEntity(world, x, y, z);
        if (camouflage != null)
        {
            camouflage.setMetaData(item.getItemDamage());
        }
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void getSubBlocks(Item block, CreativeTabs tabs, List list)
    {
        for (int i = 0; i < TileEntityCamouflage.CamouflageType.values().length; i++)
        {
            list.add(new ItemStack(block, 1, i));
        }
    }
}
