package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ICable;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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
    public boolean isCable(int meta)
    {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityQuantumCable && stack.hasTagCompound())
        {
            ((TileEntityQuantumCable) te).setQuantumKey(stack.getTagCompound().getInteger(TileEntityQuantumCable.NBT_QUANTUM_KEY));
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
                cables.add(pairedCoord);
                visited.add(pairedCoord);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityQuantumCable();
    }

}
