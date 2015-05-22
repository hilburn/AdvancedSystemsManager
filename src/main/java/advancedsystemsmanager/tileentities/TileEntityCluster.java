package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.items.IClusterItem;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.ITileEntityInterface;
import advancedsystemsmanager.helpers.PlayerHelper;
import advancedsystemsmanager.items.blocks.ItemCluster;
import advancedsystemsmanager.network.*;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = Mods.COFH_ENERGY),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = Mods.COFH_ENERGY)
})
public class TileEntityCluster extends TileEntity implements ITileEntityInterface, IPacketBlock, IEnergyProvider, IEnergyReceiver
{

    private static final String NBT_SUB_BLOCKS = "SubBlocks";
    private static final String NBT_SUB_BLOCK_ID = "SubId";
    private static final String NBT_SUB_BLOCK_META = "SubMeta";
    private boolean requestedInfo;
    private List<TileEntityClusterElement> elements;
    private List<ClusterRegistry> registryList;
    private Map<ClusterMethodRegistration, List<Pair>> methodRegistration;
    private ITileEntityInterface interfaceObject;  //only the relay is currently having a interface
    private TileEntityCamouflage camouflageObject;

    public TileEntityCluster()
    {
        elements = new ArrayList<TileEntityClusterElement>();
        registryList = new ArrayList<ClusterRegistry>();
        methodRegistration = new HashMap<ClusterMethodRegistration, List<Pair>>();
        for (ClusterMethodRegistration clusterMethodRegistration : ClusterMethodRegistration.values())
        {
            methodRegistration.put(clusterMethodRegistration, new ArrayList<Pair>());
        }
    }

    public static <T extends TileEntityClusterElement> T getTileEntity(Class<T> clazz, IBlockAccess world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te != null)
        {
            if (clazz.isInstance(te))
            {
                return (T)te;
            } else if (te instanceof TileEntityCluster)
            {
                for (TileEntityClusterElement element : ((TileEntityCluster)te).getElements())
                {
                    if (clazz.isInstance(element))
                    {
                        return (T)element;
                    }
                }
            }
        }

        return null;
    }

    public List<TileEntityClusterElement> getElements()
    {
        return elements;
    }

    public void loadElements(ItemStack itemStack)
    {
        NBTTagCompound compound = itemStack.getTagCompound();

        if (compound != null && compound.hasKey(ItemCluster.NBT_CABLE))
        {
            NBTTagCompound cable = compound.getCompoundTag(ItemCluster.NBT_CABLE);
            byte[] types = cable.getByteArray(ItemCluster.NBT_TYPES);
            loadElements(types);
        }
    }

    private void loadElements(byte[] types)
    {
        registryList.clear();
        elements.clear();

        for (byte type : types)
        {
            addElement(type);
        }
    }

    public TileEntityClusterElement addElement(byte type)
    {
        ClusterRegistry block = ClusterRegistry.getRegistryList().get(type);
        return addElement(block);
    }

    public TileEntityClusterElement addElement(ClusterRegistry registry)
    {
        registryList.add(registry);
        TileEntityClusterElement element = (TileEntityClusterElement)((ITileEntityProvider)registry.getBlock()).createNewTileEntity(getWorldObj(), 0);
        elements.add(element);
        if (element instanceof ITileEntityInterface)
        {
            interfaceObject = (ITileEntityInterface)element;
        } else if (element instanceof TileEntityCamouflage)
        {
            camouflageObject = (TileEntityCamouflage)element;
        }
        Pair result = new Pair(registry, element);
        for (ClusterMethodRegistration clusterMethodRegistration : element.getRegistrations())
        {
            methodRegistration.get(clusterMethodRegistration).add(result);
        }
        element.xCoord = xCoord;
        element.yCoord = yCoord;
        element.zCoord = zCoord;
        element.setWorldObj(worldObj);
        element.setPartOfCluster(true);
        return element;
    }

    public void addElement(EntityPlayer player, ClusterRegistry registry)
    {
        TileEntityClusterElement element = addElement(registry);
        if (element.getRegistrations().contains(ClusterMethodRegistration.ON_BLOCK_PLACED_BY))
        {
            registry.getBlock().onBlockPlacedBy(worldObj, xCoord, yCoord, zCoord, player, registry.getItemStack());
        }
        if (element.getRegistrations().contains(ClusterMethodRegistration.ON_BLOCK_ADDED))
        {
            registry.getBlock().onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack itemStack)
    {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_PLACED_BY))
        {
            setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onBlockPlacedBy(worldObj, xCoord, yCoord, zCoord, entity, blockContainer.registry.getItemStack());
        }
    }

    private List<Pair> getRegistrations(ClusterMethodRegistration method)
    {
        return methodRegistration.get(method);
    }

    public void onNeighborBlockChange(Block block)
    {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED))
        {
            setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, block);
        }
    }

    public boolean canConnectRedstone(int side)
    {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.CAN_CONNECT_REDSTONE))
        {
            setWorldObject(blockContainer.te);
            if (blockContainer.registry.getBlock().canConnectRedstone(worldObj, xCoord, yCoord, zCoord, side))
            {
                return true;
            }
        }

        return false;
    }

    public void onBlockAdded()
    {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ADDED))
        {
            setWorldObject(blockContainer.te);
            blockContainer.registry.getBlock().onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public boolean shouldCheckWeakPower(int side)
    {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.SHOULD_CHECK_WEAK_POWER))
        {
            setWorldObject(blockContainer.te);
            if (blockContainer.registry.getBlock().shouldCheckWeakPower(worldObj, xCoord, yCoord, zCoord, side))
            {
                return true;
            }
        }

        return false;
    }


    public int isProvidingWeakPower(int side)
    {
        int max = 0;

        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_WEAK_POWER))
        {
            setWorldObject(blockContainer.te);
            max = Math.max(max, blockContainer.registry.getBlock().isProvidingWeakPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public int isProvidingStrongPower(int side)
    {
        int max = 0;

        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_STRONG_POWER))
        {
            setWorldObject(blockContainer.te);
            max = Math.max(max, blockContainer.registry.getBlock().isProvidingStrongPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof IClusterItem)
        {
            ClusterRegistry registry = ((IClusterItem)stack.getItem()).getClusterRegistry(stack);
            if (registry != null && !registryList.contains(registry))
            {
                addElement(registry);
                PlayerHelper.consumeItem(player);
                return true;
            }
        }
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ACTIVATED))
        {
            setWorldObject(blockContainer.te);
            if (blockContainer.registry.getBlock().onBlockActivated(worldObj, xCoord, yCoord, zCoord, player, side, hitX, hitY, hitZ))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        int toReceive = 0;
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.RECEIVE_ENERGY))
        {
            toReceive += ((TileEntityRFNode)blockContainer.te).receiveEnergy(from, maxReceive - toReceive, simulate);
        }
        return toReceive;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int toExtract = maxExtract;
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.EXTRACT_ENERGY))
        {
            toExtract += ((TileEntityRFNode)blockContainer.te).extractEnergy(from, maxExtract - toExtract, simulate);
        }
        return maxExtract - toExtract;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getEnergyStored(ForgeDirection from)
    {
        int stored = -1;
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.STORED_ENERGY))
        {
            stored += ((TileEntityRFNode)blockContainer.te).getEnergyStored(from);
        }
        return stored;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getMaxEnergyStored(ForgeDirection from)
    {
        int max = -1;
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.STORED_ENERGY))
        {
            max += ((TileEntityRFNode)blockContainer.te).getMaxEnergyStored(from);
        }
        return max;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public boolean canConnectEnergy(ForgeDirection from)
    {
        for (Pair blockContainer : getRegistrations(ClusterMethodRegistration.CONNECT_ENERGY))
        {
            if (((TileEntityRFNode)blockContainer.te).canConnectEnergy(from)) return true;
        }
        return false;
    }

    @Override
    public Container getContainer(EntityPlayer player)
    {
        return interfaceObject == null ? null : interfaceObject.getContainer(player);
    }

    @Override
    public GuiScreen getGui(EntityPlayer player)
    {
        return interfaceObject == null ? null : interfaceObject.getGui(player);
    }

    @Override
    public void readData(ByteBuf buf, EntityPlayer player)
    {
        if (interfaceObject != null)
        {
            interfaceObject.readData(buf, player);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        NBTTagList subList = tagCompound.getTagList(NBT_SUB_BLOCKS, 10);
        List<Byte> bytes = new ArrayList<Byte>();
        for (int i = 0; i < subList.tagCount(); i++)
        {
            NBTTagCompound sub = subList.getCompoundTagAt(i);
            bytes.add(sub.getByte(NBT_SUB_BLOCK_ID));
        }
        byte[] byteArr = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++)
        {
            byteArr[i] = bytes.get(i);
        }
        loadElements(byteArr);
        for (int i = 0; i < subList.tagCount(); i++)
        {
            NBTTagCompound sub = subList.getCompoundTagAt(i);
            TileEntityClusterElement element = elements.get(i);
            element.setMetaData(sub.getByte(NBT_SUB_BLOCK_META));
            element.readContentFromNBT(sub);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        NBTTagList subList = new NBTTagList();
        for (int i = 0; i < elements.size(); i++)
        {
            TileEntityClusterElement element = elements.get(i);
            ClusterRegistry registryElement = registryList.get(i);
            NBTTagCompound sub = new NBTTagCompound();
            sub.setByte(NBT_SUB_BLOCK_ID, (byte)registryElement.getId());
            sub.setByte(NBT_SUB_BLOCK_META, (byte)element.getBlockMetadata());
            element.writeContentToNBT(sub);

            subList.appendTag(sub);
        }


        tagCompound.setTag(NBT_SUB_BLOCKS, subList);
    }

    @Override
    public void updateEntity()
    {
        for (TileEntityClusterElement element : elements)
        {
            setWorldObject(element);
            element.updateEntity();
        }

        if (!requestedInfo && worldObj.isRemote)
        {
            requestedInfo = true;
            requestData();
        }
    }

    public void setWorldObject(TileEntityClusterElement te)
    {
        if (!te.hasWorldObj())
        {
            te.setWorldObj(this.worldObj);
        }
    }

    @SideOnly(Side.CLIENT)
    private void requestData()
    {
        PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 1);
    }

    @Override
    public void writeData(ASMPacket dw, int id)
    {
        if (id == 0)
        {
            if (camouflageObject != null)
            {
                camouflageObject.writeData(dw, id);
            }
        } else
        {

            dw.writeByte(elements.size());
            for (ClusterRegistry registry : registryList)
            {
                dw.writeByte(registry.getId());
            }
            for (TileEntityClusterElement element : elements)
            {
                dw.writeByte(element.getBlockMetadata());
            }
        }
    }

    @Override
    public void readData(ASMPacket dr, int id)
    {
        if (id == 0)
        {
            if (camouflageObject != null)
            {
                camouflageObject.readData(dr, id);
            }
        } else
        {
            int length = dr.readByte();
            byte[] types = new byte[length];
            for (int i = 0; i < length; i++)
            {
                types[i] = dr.readByte();
            }
            loadElements(types);
            for (int i = 0; i < length; i++)
            {
                elements.get(i).setMetaData(dr.readByte());
            }
        }
    }


    public byte[] getTypes()
    {
        byte[] bytes = new byte[registryList.size()];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte)registryList.get(i).getId();
        }

        return bytes;
    }

    @Override
    public void writeNetworkComponent(ByteBuf buf)
    {
        if (interfaceObject != null)
        {
            interfaceObject.writeNetworkComponent(buf);
        }
    }

    private class Pair
    {
        private ClusterRegistry registry;
        private TileEntityClusterElement te;

        private Pair(ClusterRegistry registry, TileEntityClusterElement te)
        {
            this.registry = registry;
            this.te = te;
        }
    }
}
