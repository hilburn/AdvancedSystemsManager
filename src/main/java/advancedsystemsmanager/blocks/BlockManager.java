package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityReceiver;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockManager extends BlockTileBase
{
    @SideOnly(Side.CLIENT)
    private IIcon sideIcon;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon botIcon;

    public BlockManager()
    {
        super(Names.MANAGER, 2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityManager();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        sideIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":manager_side");
        topIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":manager_top");
        botIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":manager_bot");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        if (side == 0)
        {
            return botIcon;
        } else if (side == 1)
        {
            return topIcon;
        } else
        {
            return sideIcon;
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);
        updateRedstone(world, x, y, z);
        updateInventories(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);
        updateRedstone(world, x, y, z);
        updateInventories(world, x, y, z);
    }

    private void updateInventories(World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof TileEntityManager)
        {
            ((TileEntityManager)tileEntity).updateInventories();
        }
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta & 1;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide)
    {
        if (!world.isRemote)
        {
            FMLNetworkHandler.openGui(player, AdvancedSystemsManager.INSTANCE, 1, world, x, y, z);
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack)
    {
        super.onBlockPlacedBy(world, x, y, z, entity, itemStack);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>(Arrays.asList(getPickBlock(null, world, x, y, z, null)));
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        ItemStack result = new ItemStack(this, 1, damageDropped(world.getBlockMetadata(x, y, z)));
        result.setTagCompound(getTagCompound(world, x, y, z));
        return result;
    }

    public static NBTTagCompound getTagCompound(World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileEntityManager)
        {
            NBTTagCompound result = new NBTTagCompound();
            ((TileEntityManager)te).writeContentToNBT(result, false);
            return result;
        }
        return null;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        super.breakBlock(world, x, y, z, block, meta);

        updateInventories(world, x, y, z);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    private void updateRedstone(World world, int x, int y, int z)
    {
        TileEntity input = world.getTileEntity(x, y, z);
        if (input instanceof TileEntityManager)
        {
            ((TileEntityManager)input).triggerRedstone();
        }
    }
}
