package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

public class ItemManager extends ItemBlock
{
    public ItemManager(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List information, boolean advanced)
    {
        int amount = 0;
        if (stack.hasTagCompound())
        {
            NBTTagList components = stack.getTagCompound().getTagList(TileEntityManager.NBT_COMPONENTS, 10);
            amount = components.tagCount();
        }

        if (amount > 0) information.add(StatCollector.translateToLocalFormatted(Names.COMMANDS, amount));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + (stack.getItemDamage() == 0 ? "" : "_unlimited");
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;
        if (stack.hasTagCompound())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                ((TileEntityManager)te).readContentFromNBT(stack.getTagCompound(), false);
            }
        }
        return true;
    }
}
