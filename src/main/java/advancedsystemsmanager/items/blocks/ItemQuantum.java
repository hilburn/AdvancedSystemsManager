package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.client.gui.TextColour;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

public class ItemQuantum extends ItemBlock
{
    public ItemQuantum(Block block)
    {
        super(block);
        setMaxStackSize(2);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack item, EntityPlayer player, List list, boolean extraInfo)
    {
        if (!item.hasTagCompound())
        {
            item.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = item.getTagCompound();

        if (tag.hasKey(TileEntityQuantumCable.NBT_QUANTUM_KEY))
        {
            list.add(LocalizationHelper.translateFormatted(Names.QUANTUM_PAIRING, tag.getInteger(TileEntityQuantumCable.NBT_QUANTUM_KEY)));
        } else
        {
            list.add(TextColour.RED.toString() + LocalizationHelper.translate(Names.QUANTUM_UNPAIRED));
        }
        int range = tag.getInteger(TileEntityQuantumCable.NBT_QUANTUM_RANGE);
        if (range < 9)
        {
            list.add(LocalizationHelper.translateFormatted(Names.QUANTUM_RANGE, TileEntityQuantumCable.getRange(range)));
        } else
        {
            list.add(LocalizationHelper.translate(Names.QUANTUM_INTERDIMENSIONAL));
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player)
    {
        super.onCreated(stack, world, player);
        if (!world.isRemote && stack.hasTagCompound() && stack.getTagCompound().hasKey(TileEntityQuantumCable.NBT_QUANTUM_KEY))
        {
            TileEntityQuantumCable.getNextQuantumKey();
        }
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        return !(!stack.hasTagCompound() || !stack.getTagCompound().hasKey(TileEntityQuantumCable.NBT_QUANTUM_KEY)) && super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
}
