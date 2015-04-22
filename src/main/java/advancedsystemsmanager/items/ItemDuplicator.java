package advancedsystemsmanager.items;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.registry.ModBlocks;
import advancedsystemsmanager.tileentities.TileEntityManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class ItemDuplicator extends Item
{
    public ItemDuplicator()
    {
        this.setCreativeTab(ModBlocks.creativeTab);
        this.setUnlocalizedName(Names.DRIVE);
        this.setTextureName(Reference.ID.toLowerCase() + ":" + Names.DRIVE);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_)
    {
        if (stack.hasTagCompound() && validateNBT(stack))
        {
            if (stack.getTagCompound().hasKey("Author"))
            {
                list.add("Manager setup authored by:");
                list.add(stack.getTagCompound().getString("Author"));
            } else
            {
                int x = stack.getTagCompound().getInteger("x");
                int y = stack.getTagCompound().getInteger("y");
                int z = stack.getTagCompound().getInteger("z");
                list.add("Data stored from Manager at:");
                list.add("x: " + x + " y: " + y + " z: " + z);
            }

        }
    }

    public static boolean validateNBT(ItemStack stack)
    {
        if (stack.hasTagCompound() && (stack.getTagCompound().getString("id").equals("TileEntityMachineManagerName") || stack.getTagCompound().getString("id").equals("TileEntityRFManager")))
            return true;
        stack.setTagCompound(null);
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityManager)
            {
                if (stack.hasTagCompound() && validateNBT(stack))
                {
                    te.readFromNBT(correctNBT((TileEntityManager)te, stack.getTagCompound()));
                    stack.setTagCompound(null);
                } else
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    te.writeToNBT(tagCompound);
                    tagCompound.setTag("ench", new NBTTagList());
                    stack.setTagCompound(tagCompound);
                }
                return true;
            }
        }
        validateNBT(stack);
        return false;
    }

    private static NBTTagCompound correctNBT(TileEntityManager manager, NBTTagCompound tagCompound)
    {
        tagCompound.setInteger("x", manager.xCoord);
        tagCompound.setInteger("y", manager.yCoord);
        tagCompound.setInteger("z", manager.zCoord);
        int currentFlow = manager.getFlowItems().size();
        if (currentFlow > 0)
        {
            byte version = tagCompound.getByte("ProtocolVersion");
            NBTTagList components = tagCompound.getTagList("Components", 10);
            NBTTagList newComponents = new NBTTagList();
            for (int variablesTag = 0; variablesTag < components.tagCount(); ++variablesTag)
            {
                NBTTagCompound flowComponent = components.getCompoundTagAt(variablesTag);
                NBTTagList connections = flowComponent.getTagList("Connection", 10);
                NBTTagList newConnections = new NBTTagList();
                for (int i = 0; i < connections.tagCount(); ++i)
                {
                    NBTTagCompound connection = connections.getCompoundTagAt(i);
                    if (connection.hasKey("ConnectionComponent"))
                    {
                        if (version < 9)
                        {
                            connection.setByte("ConnectionComponent", (byte)(connection.getByte("ConnectionComponent") + currentFlow));
                        } else
                        {
                            connection.setShort("ConnectionComponent", (short)(connection.getShort("ConnectionComponent") + currentFlow));
                        }
                    }
                    newConnections.appendTag(connection);
                }
                flowComponent.setTag("Connection", newConnections);
                newComponents.appendTag(flowComponent);
            }
            tagCompound.setTag("Components", newComponents);
        }
        return tagCompound;
    }
}
