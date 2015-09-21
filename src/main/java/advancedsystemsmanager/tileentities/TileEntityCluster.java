package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.items.IClusterItem;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.ICluster;
import advancedsystemsmanager.api.tileentities.IClusterElement;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.helpers.PlayerHelper;
import advancedsystemsmanager.items.blocks.ItemCluster;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = Mods.COFH_ENERGY),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = Mods.COFH_ENERGY)
})
public class TileEntityCluster extends TileEntity implements ITileInterfaceProvider, ICluster, IPacketBlock, IEnergyProvider, IEnergyReceiver
{

    private static final String NBT_SUB_BLOCKS = "SubBlocks";
    private static final String NBT_SUB_BLOCK_ID = "SubId";
    private static final String NBT_SUB_BLOCK_META = "SubMeta";
    private boolean requestedInfo;
    private List<ClusterPair> pairs = new ArrayList<ClusterPair>();
    private List<IClusterElement> elements;
    private Multimap<ClusterMethodRegistration, ClusterPair> methods = HashMultimap.create();
    private ITileInterfaceProvider interfaceObject;  //only the relay is currently having a interface
    private TileEntityCamouflage camouflageObject;

    public TileEntityCluster()
    {
        elements = new ArrayList<IClusterElement>();
    }

    public List<IClusterTile> getTiles()
    {
        List<IClusterTile> result = new ArrayList<IClusterTile>();
        for (ClusterPair pair : pairs) result.add(pair.tile);
        return result;
    }

    public List<ClusterPair> getElementPairs()
    {
        return pairs;
    }

    public void loadElements(ItemStack itemStack)
    {
        NBTTagCompound compound = itemStack.getTagCompound();

        if (compound != null && compound.hasKey(ItemCluster.NBT_CABLE))
        {
            NBTTagCompound cable = compound.getCompoundTag(ItemCluster.NBT_CABLE);
            byte[] types = cable.getByteArray(ItemCluster.NBT_TYPES);
            byte[] metas = cable.hasKey(ItemCluster.NBT_DAMAGE) ? cable.getByteArray(ItemCluster.NBT_DAMAGE) : new byte[types.length];
            for (int i = 0; i < types.length; i++)
            {
                addElement(types[i], metas[i]);
            }
        }
    }

    private ClusterPair addElement(byte id, byte meta)
    {
        IClusterElement element = ClusterRegistry.get(id);
        if (element == null) return null;
        return addElement(element, meta);
    }

    private ClusterPair addElement(IClusterElement element, byte meta)
    {
        TileEntity tile = element.getClusterTile(getWorldObj(), meta);
        if ((interfaceObject != null && tile instanceof ITileInterfaceProvider) || (camouflageObject != null && tile instanceof TileEntityCamouflage))
            return null;
        ClusterPair pair = new ClusterPair(element, (IClusterTile)tile);
        elements.add(element);
        pairs.add(pair);
        if (tile instanceof ITileInterfaceProvider)
        {
            interfaceObject = (ITileInterfaceProvider)tile;
        } else if (tile instanceof TileEntityCamouflage)
        {
            camouflageObject = (TileEntityCamouflage)tile;
        }
        for (ClusterMethodRegistration clusterMethodRegistration : pair.tile.getRegistrations())
        {
            methods.put(clusterMethodRegistration, pair);
        }
        tile.xCoord = xCoord;
        tile.yCoord = yCoord;
        tile.zCoord = zCoord;
        tile.setWorldObj(worldObj);
        pair.tile.setPartOfCluster(true);
        pair.tile.setMetaData(meta);
        return pair;
    }

    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack itemStack)
    {
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.ON_BLOCK_PLACED_BY))
        {
            setWorld(pair.tile);
            pair.element.getBlock().onBlockPlacedBy(worldObj, xCoord, yCoord, zCoord, entity, pair.getItemStack());
        }
    }

    private Collection<ClusterPair> getRegistrations(ClusterMethodRegistration method)
    {
        return methods.get(method);
    }

    public void setWorld(IClusterTile tile)
    {
        setWorldObject((TileEntity)tile);
    }

    public void setWorldObject(TileEntity te)
    {
        if (!te.hasWorldObj())
        {
            te.setWorldObj(this.worldObj);
        }
    }

    public void onNeighborBlockChange(Block block)
    {
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.ON_NEIGHBOR_BLOCK_CHANGED))
        {
            setWorld(pair.tile);
            pair.element.getBlock().onNeighborBlockChange(worldObj, xCoord, yCoord, zCoord, block);
        }
    }

    public boolean canConnectRedstone(int side)
    {
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.CAN_CONNECT_REDSTONE))
        {
            setWorld(pair.tile);
            if (pair.element.getBlock().canConnectRedstone(worldObj, xCoord, yCoord, zCoord, side))
            {
                return true;
            }
        }

        return false;
    }

    public void onBlockAdded()
    {
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ADDED))
        {
            setWorld(pair.tile);
            pair.element.getBlock().onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        }
    }

    public boolean shouldCheckWeakPower(int side)
    {
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.SHOULD_CHECK_WEAK_POWER))
        {
            setWorld(pair.tile);
            if (pair.element.getBlock().shouldCheckWeakPower(worldObj, xCoord, yCoord, zCoord, side))
            {
                return true;
            }
        }

        return false;
    }

    public int isProvidingWeakPower(int side)
    {
        int max = 0;

        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_WEAK_POWER))
        {
            setWorld(pair.tile);
            max = Math.max(max, pair.element.getBlock().isProvidingWeakPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public int isProvidingStrongPower(int side)
    {
        int max = 0;

        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.IS_PROVIDING_STRONG_POWER))
        {
            setWorld(pair.tile);
            max = Math.max(max, pair.element.getBlock().isProvidingStrongPower(worldObj, xCoord, yCoord, zCoord, side));
        }

        return max;
    }

    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof IClusterItem)
        {
            IClusterElement element = ((IClusterItem)stack.getItem()).getClusterElement(stack);
            if (element != null && !elements.contains(element) && addElement(player, element, stack.getItemDamage()))
            {
                PlayerHelper.consumeItem(player);
                return true;
            }
        }
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.ON_BLOCK_ACTIVATED))
        {
            setWorldObject((TileEntity)pair.tile);
            if (pair.element.getBlock().onBlockActivated(worldObj, xCoord, yCoord, zCoord, player, side, hitX, hitY, hitZ))
            {
                return true;
            }
        }

        return false;
    }

    public boolean addElement(EntityPlayer player, IClusterElement element, int damage)
    {
        //TileEntityClusterElement element = addElement(registry);
        ClusterPair pair = addElement(element, (byte)damage);
        if (element == null) return false;
        if (pair.tile.getRegistrations().contains(ClusterMethodRegistration.ON_BLOCK_PLACED_BY))
        {
            element.getBlock().onBlockPlacedBy(worldObj, xCoord, yCoord, zCoord, player, element.getItemStack(damage));
        }
        if (pair.tile.getRegistrations().contains(ClusterMethodRegistration.ON_BLOCK_ADDED))
        {
            element.getBlock().onBlockAdded(worldObj, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        int toReceive = 0;
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.RECEIVE_ENERGY))
        {
            toReceive += ((IEnergyReceiver)pair.tile).receiveEnergy(from, maxReceive - toReceive, simulate);
        }
        return toReceive;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int toExtract = maxExtract;
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.EXTRACT_ENERGY))
        {
            toExtract += ((IEnergyProvider)pair.tile).extractEnergy(from, maxExtract - toExtract, simulate);
        }
        return maxExtract - toExtract;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getEnergyStored(ForgeDirection from)
    {
        int stored = -1;
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.STORED_ENERGY))
        {
            stored += ((IEnergyProvider)pair.tile).getEnergyStored(from);
        }
        return stored;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getMaxEnergyStored(ForgeDirection from)
    {
        int max = -1;
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.STORED_ENERGY))
        {
            max += ((IEnergyProvider)pair.tile).getMaxEnergyStored(from);
        }
        return max;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public boolean canConnectEnergy(ForgeDirection from)
    {
        for (ClusterPair pair : getRegistrations(ClusterMethodRegistration.CONNECT_ENERGY))
        {
            if (((IEnergyConnection)pair.tile).canConnectEnergy(from)) return true;
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
    public boolean readData(ASMPacket packet, EntityPlayer player)
    {
        return interfaceObject != null && interfaceObject.readData(packet, player);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        NBTTagList subList = tagCompound.getTagList(NBT_SUB_BLOCKS, 10);

        for (int i = 0; i < subList.tagCount(); i++)
        {
            NBTTagCompound sub = subList.getCompoundTagAt(i);
            ClusterPair pair = addElement(sub.getByte(NBT_SUB_BLOCK_ID), sub.getByte(NBT_SUB_BLOCK_META));
            pair.tile.readContentFromNBT(sub);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        NBTTagList subList = new NBTTagList();
        for (ClusterPair pair : pairs)
        {
            NBTTagCompound sub = new NBTTagCompound();
            sub.setByte(NBT_SUB_BLOCK_ID, pair.getId());
            sub.setByte(NBT_SUB_BLOCK_META, pair.getMeta());
            pair.tile.writeContentToNBT(sub);
            subList.appendTag(sub);
        }


        tagCompound.setTag(NBT_SUB_BLOCKS, subList);
    }

    @Override
    public void updateEntity()
    {
        for (ClusterPair pair : pairs)
        {
            TileEntity te = (TileEntity)pair.tile;
            setWorldObject(te);
            te.updateEntity();
        }

        if (!requestedInfo && worldObj.isRemote)
        {
            requestedInfo = true;
            requestData();
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
        if (!worldObj.isRemote)
        {
            if (id == 0)
            {
                if (camouflageObject != null)
                {
                    camouflageObject.writeData(dw, id);
                }
            } else
            {
                dw.writeByte(pairs.size());
                for (ClusterPair pair : pairs)
                {
                    dw.writeByte(pair.getId());
                    dw.writeByte(pair.getMeta());
                }
                if (camouflageObject != null)
                {
                    camouflageObject.writeData(dw, 0);
                }
            }
        }
    }

    @Override
    public void readData(ASMPacket packet, int id)
    {
        if (id == 0)
        {
            if (camouflageObject != null)
            {
                camouflageObject.readData(packet, id);
            }
        } else
        {
            if (worldObj.isRemote)
            {
                clearElements();
                int length = packet.readByte();
                for (int i = 0; i < length; i++)
                {
                    addElement(packet.readByte(), packet.readByte());
                }
                if (camouflageObject != null)
                {
                    camouflageObject.readData(packet, 0);
                }
            } else
            {
                ASMPacket response = PacketHandler.constructBlockPacket(this, this, 1);
                response.setPlayers(packet.getPlayers());
                response.sendResponse();
            }
        }
    }

    private void clearElements()
    {
        elements.clear();
        pairs.clear();
        interfaceObject = null;
        camouflageObject = null;
    }

    public byte[] getTypes()
    {
        byte[] bytes = new byte[pairs.size()];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = pairs.get(i).getId();
        }
        return bytes;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return camouflageObject != null? camouflageObject.getDescriptionPacket() : super.getDescriptionPacket();
    }

    public byte[] getDamages()
    {
        byte[] bytes = new byte[pairs.size()];
        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = pairs.get(i).getMeta();
        }
        return bytes;
    }

    public List<ItemStack> getStacks()
    {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (ClusterPair pair : pairs)
            stacks.add(pair.getItemStack());
        return stacks;
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        return interfaceObject != null && interfaceObject.writeData(packet);
    }

    public boolean isHidden(int sideHit)
    {
        return camouflageObject != null && camouflageObject.hasSideBlock(sideHit);
    }

    public class ClusterPair
    {
        public IClusterElement element;
        public IClusterTile tile;

        private ClusterPair(IClusterElement element, IClusterTile tile)
        {
            this.element = element;
            this.tile = tile;
        }

        private ItemStack getItemStack()
        {
            return tile.getItemStackFromBlock();
        }

        private byte getId()
        {
            return element.getId();
        }

        private byte getMeta()
        {
            return (byte)tile.getMetadata();
        }
    }
}
