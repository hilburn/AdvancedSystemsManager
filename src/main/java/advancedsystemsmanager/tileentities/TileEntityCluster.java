package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.api.tileentities.*;
import advancedsystemsmanager.helpers.PlayerHelper;
import advancedsystemsmanager.items.blocks.ItemTileElement;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.ClusterRegistry;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

@Optional.InterfaceList({
        @Optional.Interface(iface = "cofh.api.energy.IEnergyProvider", modid = Mods.COFH_ENERGY),
        @Optional.Interface(iface = "cofh.api.energy.IEnergyReceiver", modid = Mods.COFH_ENERGY)
})
public class TileEntityCluster extends TileEntityElementRotation implements ITileInterfaceProvider, ICluster, IEnergyProvider, IEnergyReceiver, IActivateListener, IBUDListener, IRedstoneListener
{

    private static final String NBT_SUB_BLOCKS = "SubBlocks";
    private boolean requestedInfo;
    private Map<ITileFactory, ITileElement> pairs = new HashMap<ITileFactory, ITileElement>();
    private List<ITileElement> elements = new ArrayList<ITileElement>();
    private ITileInterfaceProvider interfaceObject;
    private TileEntityCamouflage camouflageObject;

    public static boolean hasSubBlocks(ItemStack stack)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_SUB_BLOCKS) && !stack.getTagCompound().getCompoundTag(NBT_SUB_BLOCKS).tagMap.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public static List<ItemStack> getSubblocks(ItemStack stack)
    {
        List<ItemStack> result = new ArrayList<ItemStack>();
        if (hasSubBlocks(stack))
        {
            NBTTagCompound tag = stack.getTagCompound().getCompoundTag(NBT_SUB_BLOCKS);
            for (Object obj : tag.tagMap.entrySet())
            {
                Map.Entry<String, NBTBase> entry = (Map.Entry<String, NBTBase>)obj;
                if (entry.getValue() instanceof NBTTagCompound)
                {
                    ITileFactory factory = ClusterRegistry.get(entry.getKey());
                    if (factory != null)
                    {
                        result.add(factory.getItemStack(((NBTTagCompound) entry.getValue()).getByte(NBT_SUBTYPE)));
                    }
                }
            }
        }
        return result;
    }

    public Set<Map.Entry<ITileFactory, ITileElement>> getPairs()
    {
        return pairs.entrySet();
    }

    public List<ITileElement> getTiles()
    {
        return elements;
    }

    public void loadElements(ItemStack itemStack)
    {
        NBTTagCompound compound = itemStack.getTagCompound();

        if (compound != null)
        {
            loadElements(compound);
        }
    }

    public static void saveSubBlocks(ItemStack cluster, List<ITileFactory> factories, List<ItemStack> stacks)
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        for (int i = 0; i < factories.size(); i++)
        {
            factories.get(i).saveToClusterTag(stacks.get(i), tagCompound);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadElements(NBTTagCompound tag)
    {
        for (Object obj : tag.tagMap.entrySet())
        {
            Map.Entry<String, NBTBase> entry = (Map.Entry<String, NBTBase>)obj;
            if (entry.getValue() instanceof NBTTagCompound)
            {
                addElement(entry.getKey(), (NBTTagCompound)entry.getValue());
            }
        }
    }

    private ITileElement addElement(String key, NBTTagCompound value)
    {
        ITileFactory factory = ClusterRegistry.get(key);
        if (factory != null)
        {
            return addElement(factory, value);
        }
        return null;
    }

    private ITileElement addElement(ITileFactory factory, NBTTagCompound value)
    {
        if (!factory.canBeAddedToCluster(pairs.keySet()))
        {
            return null;
        }
        TileEntity tile = factory.createTileEntity(getWorldObj(), 0);
        if (tile instanceof ITileElement)
        {
            ITileElement element = (ITileElement) tile;
            tile.readFromNBT(value);
            pairs.put(factory, element);
            elements.add(element);
            if (tile instanceof ITileInterfaceProvider)
            {
                interfaceObject = (ITileInterfaceProvider)tile;
            } else if (tile instanceof TileEntityCamouflage)
            {
                camouflageObject = (TileEntityCamouflage)tile;
            }
            tile.xCoord = xCoord;
            tile.yCoord = yCoord;
            tile.zCoord = zCoord;
            tile.setWorldObj(worldObj);
            element.setPartOfCluster(true);
            return element;
        }
        return null;
    }

    @Override
    public void onBlockPlacedBy(EntityLivingBase entity, ItemStack itemStack)
    {
        super.onBlockPlacedBy(entity, itemStack);

        for (ITileElement element : elements)
        {
            if (element instanceof IPlaceListener)
            {
                ((IPlaceListener) element).onBlockPlacedBy(entity, itemStack);
            }
        }
    }

    public void setWorld(ITileElement tile)
    {
        setWorldObject((TileEntity) tile);
    }

    public void setWorldObject(TileEntity te)
    {
        if (!te.hasWorldObj())
        {
            te.setWorldObj(this.worldObj);
        }
    }

    @Override
    public void onNeighborBlockChange()
    {
        for (ITileElement element : elements)
        {
            if (element instanceof IBUDListener)
            {
                ((IBUDListener) element).onNeighborBlockChange();
            }
        }
    }

    @Override
    public boolean canConnectRedstone(int side)
    {
        for (ITileElement element : elements)
        {
            if (element instanceof IRedstoneListener && ((IRedstoneListener) element).canConnectRedstone(side))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getComparatorInputOverride(int side)
    {
        int output = 0;
        for (ITileElement element : elements)
        {
            if (element instanceof IRedstoneListener)
            {
                output = Math.max(output, ((IRedstoneListener) element).getComparatorInputOverride(side));
            }
        }
        return output;
    }

    @Override
    public void validate()
    {
        super.validate();
        for (ITileElement element : elements)
        {
            ((TileEntity)element).validate();
        }
    }

    @Override
    public void invalidate()
    {
        for (ITileElement element : elements)
        {
            ((TileEntity)element).invalidate();
        }
        super.invalidate();
    }

    @Override
    public void onChunkUnload()
    {
        for (ITileElement element : elements)
        {
            ((TileEntity)element).onChunkUnload();
        }
        super.onChunkUnload();
    }

    @Override
    public boolean shouldCheckWeakPower(int side)
    {
        for (ITileElement element : elements)
        {
            if (element instanceof IRedstoneListener && ((IRedstoneListener) element).shouldCheckWeakPower(side))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public int isProvidingWeakPower(int side)
    {
        int output = 0;
        for (ITileElement element : elements)
        {
            if (element instanceof IRedstoneListener)
            {
                output = Math.max(output, ((IRedstoneListener) element).isProvidingWeakPower(side));
            }
        }
        return output;
    }

    @Override
    public int isProvidingStrongPower(int side)
    {
        int output = 0;
        for (ITileElement element : elements)
        {
            if (element instanceof IRedstoneListener)
            {
                output = Math.max(output, ((IRedstoneListener) element).isProvidingStrongPower(side));
            }
        }
        return output;
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getCurrentEquippedItem();
        if (stack != null && stack.getItem() instanceof ItemTileElement)
        {
            ITileFactory factory = ((ItemTileElement)stack.getItem()).getTileFactory(stack);
            if (factory != null && addElement(player, factory, stack))
            {
                PlayerHelper.consumeItem(player);
                return true;
            }
        }
        if (interfaceObject != null)
        {
            return interfaceObject.onBlockActivated(player, side, hitX, hitY, hitZ);
        } else
        {
            for (ITileElement element : elements)
            {
                if (element instanceof IActivateListener && ((IActivateListener) element).onBlockActivated(player, side, hitX, hitY, hitZ))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addElement(EntityPlayer player, ITileFactory factory, ItemStack stack)
    {
        ITileElement element = addElement(factory, stack.getTagCompound());
        if (element == null) return false;
        if (element instanceof IPlaceListener)
        {
            ((IPlaceListener) element).onBlockPlacedBy(player, stack);
        }
        if (element instanceof TileEntity)
        {
            ((TileEntity) element).validate();
        }
        element.onAddedToCluster(this);
        return true;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        int toReceive = 0;
        for (ITileElement element : elements)
        {
            if (element instanceof IEnergyReceiver)
                toReceive += ((IEnergyReceiver)element).receiveEnergy(from, maxReceive - toReceive, simulate);
        }
        return toReceive;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        int toExtract = maxExtract;
        for (ITileElement element : elements)
        {
            if (element instanceof IEnergyProvider)
                toExtract += ((IEnergyProvider)element).extractEnergy(from, maxExtract - toExtract, simulate);
        }
        return maxExtract - toExtract;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getEnergyStored(ForgeDirection from)
    {
        int stored = -1;
        for (ITileElement element : elements)
        {
            if (element instanceof IEnergyReceiver)
                stored += ((IEnergyReceiver)element).getEnergyStored(from);
            else if (element instanceof IEnergyProvider)
                stored += ((IEnergyProvider)element).getEnergyStored(from);
        }
        return stored;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public int getMaxEnergyStored(ForgeDirection from)
    {
        int max = -1;
        for (ITileElement element : elements)
        {
            if (element instanceof IEnergyReceiver)
                max += ((IEnergyReceiver)element).getMaxEnergyStored(from);
            else if (element instanceof IEnergyProvider)
                max += ((IEnergyProvider)element).getMaxEnergyStored(from);
        }
        return max;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_ENERGY)
    public boolean canConnectEnergy(ForgeDirection from)
    {
        for (ITileElement element : elements)
        {
            if (element instanceof IEnergyConnection && ((IEnergyConnection) element).canConnectEnergy(from))
                return true;
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
    public void readItemNBT(NBTTagCompound tag)
    {
        super.readItemNBT(tag);
        loadElements(tag.getCompoundTag(NBT_SUB_BLOCKS));
    }

    @Override
    public void writeItemNBT(NBTTagCompound tag)
    {
        super.writeItemNBT(tag);
        NBTTagCompound subBlocks = new NBTTagCompound();
        for (Map.Entry<ITileFactory, ITileElement> entry : pairs.entrySet())
        {
            NBTTagCompound elementData = new NBTTagCompound();
            ((TileEntity)entry.getValue()).writeToNBT(elementData);
            subBlocks.setTag(entry.getKey().getKey(), elementData);
        }
        tag.setTag(NBT_SUB_BLOCKS, subBlocks);
    }

    @Override
    public void updateEntity()
    {
        for (ITileElement element : elements)
        {
            TileEntity te = (TileEntity)element;
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
        PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, TILE_DATA);
    }

    @Override
    public void writeClientSyncData(ASMPacket packet)
    {
        super.writeClientSyncData(packet);
        if (camouflageObject != null)
        {
            camouflageObject.writeData(packet, CLIENT_SYNC);
        }
    }

    @Override
    public void writeTileData(ASMPacket packet)
    {
        super.writeTileData(packet);
        packet.writeByte(pairs.size());
        for (Map.Entry<ITileFactory, ITileElement> entry : pairs.entrySet())
        {
            packet.writeStringToBuffer(entry.getKey().getKey());
            packet.writeByte(entry.getValue().getSubtype());
        }
        if (camouflageObject != null)
        {
            camouflageObject.writeData(packet, CLIENT_SYNC);
        }
    }

    @Override
    public void readClientSyncData(ASMPacket packet)
    {
        super.readClientSyncData(packet);
        if (camouflageObject != null)
        {
            camouflageObject.readData(packet, CLIENT_SYNC);
        }
    }

    @Override
    public void readTileData(ASMPacket packet)
    {
        super.readTileData(packet);
        if (worldObj.isRemote)
        {
            clearElements();
            int length = packet.readByte();
            for (int i = 0; i < length; i++)
            {
                ITileElement element = addElement(packet.readStringFromBuffer(), null);
                if (element != null)
                {
                    element.setSubtype(packet.readByte());
                }
            }
            if (camouflageObject != null)
            {
                camouflageObject.readData(packet, CLIENT_SYNC);
            }
        } else
        {
            ASMPacket response = PacketHandler.constructBlockPacket(this, this, TILE_DATA);
            response.setPlayers(packet.getPlayers());
            response.sendResponse();
        }
    }

    private void clearElements()
    {
        elements.clear();
        pairs.clear();
        interfaceObject = null;
        camouflageObject = null;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        return camouflageObject != null? camouflageObject.getDescriptionPacket() : super.getDescriptionPacket();
    }

    public List<ItemStack> getStacks()
    {
        List<ItemStack> stacks = new ArrayList<ItemStack>();
        for (Map.Entry<ITileFactory, ITileElement> entry : pairs.entrySet())
            stacks.add(entry.getKey().getItemStack(entry.getValue().getSubtype()));
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
}
