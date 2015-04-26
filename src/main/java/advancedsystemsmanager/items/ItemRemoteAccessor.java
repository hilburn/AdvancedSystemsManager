package advancedsystemsmanager.items;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class ItemRemoteAccessor extends ItemBase
{
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String WORLD = "world";

    public ItemRemoteAccessor()
    {
        super(Names.REMOTE_ACCESS);
        setHasSubtypes(true);
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                NBTTagCompound tagCompound = new NBTTagCompound();
                tagCompound.setByte(WORLD, (byte)world.provider.dimensionId);
                tagCompound.setInteger(X, te.xCoord);
                tagCompound.setInteger(Y, te.yCoord);
                tagCompound.setInteger(Z, te.zCoord);
                stack.setTagCompound(tagCompound);
                return true;
            }
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings(value = "unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName(stack) + (stack.getItemDamage() != 0 ? "_advanced" : "");
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        if (stack.hasTagCompound())
        {
            if (stack.getItemDamage() == 0 && player.getEntityWorld().provider.dimensionId != stack.getTagCompound().getByte("World"))
            {
                list.add("§cWrong Dimension");
            }
            else
            {
                int x = stack.getTagCompound().getInteger(X);
                int y = stack.getTagCompound().getInteger(Y);
                int z = stack.getTagCompound().getInteger(Z);
                list.add("Linked to Manager at:");
                list.add("x: " + x + " y: " + y + " z: " + z);
            }
        }
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (!world.isRemote && stack.hasTagCompound())
        {
            World managerWorld = stack.getItemDamage() == 0 ? world : DimensionManager.getWorld(stack.getTagCompound().getByte(WORLD));
            int x = stack.getTagCompound().getInteger(X);
            int y = stack.getTagCompound().getInteger(Y);
            int z = stack.getTagCompound().getInteger(Z);
            if (managerWorld.getChunkProvider().chunkExists(x/16, z/16))
            {
                if (managerWorld.getTileEntity(x, y, z) instanceof TileEntityManager)
                    FMLNetworkHandler.openGui(player, AdvancedSystemsManager.INSTANCE, 1, world, x, y, z);
            }
        }
        return super.onItemRightClick(stack, world, player);
    }
}
