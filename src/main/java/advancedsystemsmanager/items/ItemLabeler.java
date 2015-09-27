package advancedsystemsmanager.items;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.items.IItemInterfaceProvider;
import advancedsystemsmanager.api.items.ILeftClickItem;
import advancedsystemsmanager.api.tileentities.IInternalInventory;
import advancedsystemsmanager.api.tileentities.IInternalTank;
import advancedsystemsmanager.api.tileentities.ITileElement;
import advancedsystemsmanager.client.gui.GuiLabeler;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ItemRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class ItemLabeler extends ItemBase implements IItemInterfaceProvider, ILeftClickItem
{
    public static final String LABEL = "Label";
    private static List<Class> registeredClasses = new ArrayList<Class>();

    public ItemLabeler()
    {
        super(Names.LABELER);
    }

    public static List<String> getSavedStrings(ItemStack stack)
    {
        List<String> result = new ArrayList<String>();
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) return result;
        NBTTagList tagList = tagCompound.getTagList("saved", 8);
        for (int i = 0; i < tagList.tagCount(); i++)
        {
            result.add(tagList.getStringTagAt(i));
        }
        return result;
    }

    public static void setLabel(ItemStack stack, String string)
    {
        stack.getTagCompound().setString(LABEL, string);
    }

    public static void saveStrings(ItemStack stack, List<String> strings)
    {
        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tagCompound = stack.getTagCompound();
        NBTTagList tagList = new NBTTagList();
        for (String string : strings)
            tagList.appendTag(new NBTTagString(string));
        tagCompound.setTag("saved", tagList);
    }

    public static boolean isValidTile(World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInventory || te instanceof IFluidHandler || te instanceof ITileElement || te instanceof IInternalInventory || te instanceof IInternalTank || isValidClass(te);
    }

    public static void registerClass(Class clazz)
    {
        registeredClasses.add(clazz);
    }

    private static boolean isValidClass(TileEntity te)
    {
        for (Class registered : registeredClasses)
        {
            if (registered.isInstance(te)) return true;
        }
        return false;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if (world.isRemote)
        {
            player.openGui(AdvancedSystemsManager.INSTANCE, 0, world, player.chunkCoordX, player.chunkCoordY, player.chunkCoordZ);
        }
        return super.onItemRightClick(stack, world, player);
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extra)
    {
        super.addInformation(stack, player, list, extra);
        String label = getLabel(stack);
        if (label.isEmpty()) list.add(StatCollector.translateToLocal(Names.CLEAR_LABEL));
        else list.add(StatCollector.translateToLocalFormatted(Names.LABELLED, label));
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings(value = "unchecked")
    public void getSubItems(Item item, CreativeTabs tab, List list)
    {
        list.add(ItemRegistry.defaultLabeler);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        return false;
    }

    public static String getLabel(ItemStack stack)
    {
        return stack.hasTagCompound() ? stack.getTagCompound().getString(LABEL) : "";
    }

    @Override
    public Container getContainer(ItemStack stack, EntityPlayer player)
    {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiScreen getGui(ItemStack stack, EntityPlayer player)
    {
        return new GuiLabeler(stack, player);
    }

    @Override
    public void readData(ItemStack stack, ASMPacket buf, EntityPlayer player)
    {
    }

    @Override
    public boolean leftClick(EntityPlayer player, ItemStack stack, World world, int x, int y, int z, int face)
    {
        if (ItemLabeler.isValidTile(world, x, y, z))
        {
            String label = ItemLabeler.getLabel(stack);
            if (label.isEmpty())
            {
                if (NameRegistry.removeName(world, x, y, z))
                {
                    player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(Names.LABEL_CLEARED)));
                }
            } else
            {
                NameRegistry.saveName(world, x, y, z, label);
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocalFormatted(Names.LABEL_SAVED, label)));
            }
            return true;
        }
        return false;
    }
}
