package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.helpers.BlockHelper;
import advancedsystemsmanager.helpers.SavableData;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.util.SystemCoord;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.DimensionManager;

public class TileEntityQuantumCable extends TileEntity implements IPacketBlock
{
    public static final String NBT_QUANTUM_KEY = "quantumKey";
    public static final String NBT_QUANTUM_RANGE = "quantumRange";
    private static final QuantumSave NEXT_QUANTUM_KEY;
    private static TIntObjectHashMap<TileEntityQuantumCable> quantumRegistry = new TIntObjectHashMap<TileEntityQuantumCable>();
    private int quantumKey;
    private int quantumRange;
    private TileEntityQuantumCable pair;
    private byte sendUpdate;

    static
    {
        AdvancedSystemsManager.worldSave.save(NEXT_QUANTUM_KEY = new QuantumSave());
    }

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote && sendUpdate > 0)
        {
            sendUpdate = 0;
            PacketHandler.sendBlockPacket(this, null, sendUpdate);
        }
    }

    public static int getNextQuantumKey()
    {
        return NEXT_QUANTUM_KEY.getNextID();
    }

    public static int peekNextQuantumKey()
    {
        return NEXT_QUANTUM_KEY.peekNextID();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        readContentFromNBT(tag);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        writeContentToNBT(tag);
    }

    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger(NBT_QUANTUM_KEY, quantumKey);
        tagCompound.setInteger(NBT_QUANTUM_RANGE, quantumRange);
    }

    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        quantumRange = tagCompound.getInteger(NBT_QUANTUM_RANGE);
        setQuantumKey(tagCompound.getInteger(NBT_QUANTUM_KEY));
        sendUpdate |= 1;
    }

    private boolean isInRange(TileEntityQuantumCable paired)
    {
        return (isInterDimensional() && paired.hasWorldObj() && paired.getWorldObj().provider.dimensionId != getWorldObj().provider.dimensionId) ||
                (getRange(quantumRange) * getRange(quantumRange) >= getDistanceFrom(paired.xCoord + 0.5d, paired.yCoord + 0.5d, paired.zCoord + 0.5d));
    }

    public int getQuantumRange()
    {
        return getRange(quantumRange);
    }

    public static int getRange(int quantumRange)
    {
        return quantumRange * quantumRange + 5;
    }

    public boolean isInterDimensional()
    {
        return quantumRange == 9;
    }

    public static TileEntityQuantumCable getPairedCable(TileEntityQuantumCable cable)
    {
        TileEntityQuantumCable paired = cable.pair;
        if (paired != null && !paired.isInvalid() && paired.hasWorldObj() && paired.getWorldObj().blockExists(paired.xCoord, paired.yCoord, paired.zCoord) && paired.isInRange(cable))
        {
            return paired;
        }
        return null;
    }

    public void pairWith(TileEntityQuantumCable pair)
    {
        if (pair != this && this.pair != pair)
        {
            this.pair = pair;
            pair.pair = this;
            if (!worldObj.isRemote)
            {
                BlockHelper.updateInventories(new SystemCoord(xCoord, yCoord, zCoord, worldObj));
                quantumRegistry.remove(getQuantumKey());
                sendUpdate |= 2;
                pair.sendUpdate |= 2;
            }
        }
    }

    public static void addCable(TileEntityQuantumCable cable)
    {
        if (quantumRegistry.containsKey(cable.getQuantumKey()))
        {
            quantumRegistry.get(cable.getQuantumKey()).pairWith(cable);
        } else
        {
            quantumRegistry.put(cable.getQuantumKey(), cable);
        }
    }

    public void setQuantumKey(int key)
    {
        quantumKey = key;
        addCable(this);
    }

    @Override
    public void validate()
    {
        super.validate();
    }

    @Override
    public void invalidate()
    {
        unloadPairing();
        super.invalidate();
    }

    @Override
    public void onChunkUnload()
    {
        unloadPairing();
        super.onChunkUnload();
    }

    public void unloadPairing()
    {
        if (!worldObj.isRemote)
        {
            if (pair != null && !pair.isInvalid())
            {
                pair.pair = null;
                addCable(pair);
                pair.sendUpdate |= 2;
                BlockHelper.updateInventories(new SystemCoord(pair.xCoord, pair.yCoord, pair.zCoord, pair.worldObj));
            }else
            {
                quantumRegistry.remove(getQuantumKey());
            }

        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof TileEntityQuantumCable)
        {
            TileEntityQuantumCable other = (TileEntityQuantumCable) obj;
            return other.xCoord == xCoord && other.yCoord == yCoord && other.zCoord == zCoord && other.quantumKey == quantumKey;
        }
        return false;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        PacketHandler.sendBlockPacket(this, null, 3);
        return null;
    }

    public int getQuantumKey()
    {
        return quantumKey;
    }

    private static final char[] SPIN = "UDSCTB".toCharArray();

    public String getSpinString()
    {
        return getSpinString(quantumKey);
    }

    public static String getSpinString(int key)
    {
        String result = "";
        while (key != 0)
        {
            result = SPIN[key % SPIN.length] + result;
            key /= SPIN.length;
        }
        return result;
    }

    public static void clearRegistry()
    {
        quantumRegistry.clear();
    }

    @Override
    public void writeData(ASMPacket packet, int id)
    {
//        if ((id & 1) == 1)
        {
            packet.writeByte(quantumRange);
            packet.writeVarIntToBuffer(quantumKey);
//        }
//        if ((id & 2) == 2)
//        {
            if (pair != null && pair.worldObj != null)
            {
                packet.writeBoolean(true);
                packet.writeShort(pair.worldObj.provider.dimensionId);
                packet.writeInt(pair.xCoord);
                packet.writeByte(pair.yCoord);
                packet.writeInt(pair.zCoord);
            } else
            {
                packet.writeBoolean(false);
            }
        }
    }

    @Override
    public void readData(ASMPacket packet, int id)
    {
//        if ((id & 1) == 1)
        {
            quantumRange = packet.readByte();
            quantumKey = packet.readVarIntFromBuffer();
//        }
//        if ((id & 2) == 2)
//        {
            if (packet.readBoolean())
            {
                World world = DimensionManager.getWorld(packet.readShort());
                TileEntity te = world.getTileEntity(packet.readInt(), packet.readUnsignedByte(), packet.readInt());
                if (te instanceof TileEntityQuantumCable)
                {
                    pair = (TileEntityQuantumCable)te;
                    ((TileEntityQuantumCable) te).pair = this;
                }
            } else
            {
                pair = null;
            }
        }
    }

    public static class QuantumSave extends SavableData
    {
        private static final String KEY = "quantumSave";
        private int ID = 0;

        public QuantumSave(String key)
        {
            super(key);
        }

        @Override
        protected SavableData getNew()
        {
            return new QuantumSave();
        }

        public QuantumSave()
        {
            this(Reference.NETWORK_ID + "." + KEY);
        }

        @Override
        public boolean copyFrom(WorldSavedData worldSavedData)
        {
            if (worldSavedData instanceof QuantumSave)
            {
                ID = ((QuantumSave) worldSavedData).ID;
                return true;
            }
            return false;
        }

        @Override
        public void readFromNBT(NBTTagCompound tag)
        {
            ID = tag.getInteger(KEY);
        }

        @Override
        public void writeToNBT(NBTTagCompound tag)
        {
            tag.setInteger(KEY, ID);
        }

        public int getNextID()
        {
            markDirty();
            return ++ID;
        }

        public int peekNextID()
        {
            return ID+1;
        }
    }
}
