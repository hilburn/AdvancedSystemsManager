package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.api.tileentities.*;
import advancedsystemsmanager.items.blocks.ItemTileElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BlockTileElement extends Block
{
    private ITileFactory[] factories = new TileFactory[16];

    public BlockTileElement(String name)
    {
        super(Material.iron);
        setCreativeTab(AdvancedSystemsManager.creativeTab);
        setStepSound(soundTypeMetal);
        setBlockName(name);
        setHardness(1.2f);
    }

    public void setFactories(Iterator<ITileFactory> iterator)
    {
        for (int i = 0; iterator.hasNext() & i < factories.length;)
        {
            ITileFactory element = iterator.next();
            if (element.getBlock() == null)
            {
                factories[i] = element;
                element.setBlock(this);
                element.setMetadata(i);
                i++;
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        for (ITileFactory element : factories)
        {
            if (element != null) element.registerIcons(register);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        ITileFactory factory = getTileFactory(world.getBlockMetadata(x, y, z));
        if (factory != null)
        {
            return ((ITileElement)factory.getTileEntity(world, x, y, z)).getIcon(side);
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ITileFactory factory = getTileFactory(meta);
        return factory != null ? factory.getIcon(side, meta / 16) : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for (ITileFactory element : factories)
        {
            if (element != null) element.getSubBlocks(item, tab, list);
        }
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        ITileFactory factory = getTileFactory(meta);
        return factory != null && factory.hasTileEntity();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        ITileFactory factory = getTileFactory(meta);
        return factory != null ? factory.createTileEntity(world, meta) : null;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IBUDListener)
        {
            ((IBUDListener) tileEntity).onNeighborBlockChange();
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IActivateListener && ((IActivateListener) tileEntity).onBlockActivated(player, side, xSide, ySide, zSide);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IPlaceListener)
        {
            ((IPlaceListener) tileEntity).onBlockPlacedBy(entity, item);
        }
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener && ((IRedstoneListener) tileEntity).canConnectRedstone(side);
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener ? ((IRedstoneListener) tileEntity).getComparatorInputOverride(side) : 0;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener && ((IRedstoneListener) tileEntity).shouldCheckWeakPower(side);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener ? ((IRedstoneListener) tileEntity).isProvidingWeakPower(side) : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener ? ((IRedstoneListener) tileEntity).isProvidingStrongPower(side) : 0;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        ItemStack result = new ItemStack(this, 1, damageDropped(world.getBlockMetadata(x, y, z)));
        result.setTagCompound(getTagCompound(world, x, y, z));
        return result;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>(Collections.singletonList(getPickBlock(null, world, x, y, z, null)));
    }

    public static NBTTagCompound getTagCompound(World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ITileElement)
        {
            NBTTagCompound tag = new NBTTagCompound();
            ((ITileElement)te).writeToItemNBT(tag);
            return tag;
        }
        return null;
    }

    public ITileFactory getTileFactory(ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof ItemTileElement))
        {
            return null;
        }
        return getTileFactory(stack.getItemDamage());
    }

    public ITileFactory getTileFactory(int blockMetadata)
    {
        return factories[blockMetadata & 0xF];
    }
}
