package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ICable;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.*;

public class BlockCableQuantum extends BlockTileBase implements ICable
{
    public BlockCableQuantum()
    {
        super(Names.CABLE_QUANTUM);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        BlockRegistry.cable.updateInventories(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);

        BlockRegistry.cable.updateInventories(world, x, y, z);
    }

    @Override
    public boolean isCable(int meta)
    {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        TileEntity te = world.getTileEntity(x, y, z);
        if (!world.isRemote && te != null && te instanceof TileEntityQuantumCable && stack.hasTagCompound())
        {
            ((TileEntityQuantumCable) te).readContentFromNBT(stack.getTagCompound());
            BlockRegistry.cable.updateInventories(world, x, y, z);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>(Collections.singletonList(getPickBlock(null, world, x, y, z, null)));
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
        if (te instanceof TileEntityQuantumCable)
        {
            NBTTagCompound result = new NBTTagCompound();
            ((TileEntityQuantumCable)te).writeContentToNBT(result);
            return result;
        }
        return null;
    }

    @Override
    public void getConnectedCables(World world, SystemCoord coordinate, List<SystemCoord> visited, Queue<SystemCoord> cables)
    {
        BlockRegistry.cable.getConnectedCables(world, coordinate, visited, cables);
        TileEntity te = world.getTileEntity(coordinate.getX(), coordinate.getY(), coordinate.getZ());
        if (te != null && te instanceof TileEntityQuantumCable)
        {
            TileEntityQuantumCable paired = TileEntityQuantumCable.getPairedCable((TileEntityQuantumCable) te);
            if (paired != null)
            {
                SystemCoord pairedCoord = new SystemCoord(paired.xCoord, paired.yCoord, paired.zCoord, paired.getWorldObj(), coordinate.getDepth() + 1);
                if (!visited.contains(pairedCoord))
                {
                    cables.add(pairedCoord);
                    visited.add(pairedCoord);
                }
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityQuantumCable();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void getSubBlocks(Item item, CreativeTabs tab, List blocks)
    {
        for (int i : new int[]{1, 8, 9})
        {
            ItemStack stack = new ItemStack(this);
            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setInteger(TileEntityQuantumCable.NBT_QUANTUM_RANGE, i);
            stack.setTagCompound(tagCompound);
            blocks.add(stack);
        }
    }
}
