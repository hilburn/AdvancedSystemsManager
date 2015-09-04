package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.helpers.SavableData;
import advancedsystemsmanager.reference.Reference;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldSavedData;

public class TileEntityQuantumCable extends TileEntity
{
    public static final String NBT_QUANTUM_KEY = "quantumKey";
    public static final String NBT_QUANTUM_RANGE = "quantumRange";
    private static final QuantumSave NEXT_QUANTUM_KEY;
    private static TIntObjectHashMap<Pair> quantumRegistry = new TIntObjectHashMap<Pair>();
    private int quantumKey;
    private int quantumRange;

    static
    {
        AdvancedSystemsManager.worldSave.save(NEXT_QUANTUM_KEY = new QuantumSave());
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
        setQuantumKey(tagCompound.getInteger(NBT_QUANTUM_KEY));
        quantumRange = tagCompound.getInteger(NBT_QUANTUM_RANGE);
    }

    private boolean isInRange(TileEntityQuantumCable paired)
    {
        return (isInterDimensional() && paired.hasWorldObj() && paired.getWorldObj().provider.dimensionId != getWorldObj().provider.dimensionId) ||
                (getRange(quantumRange) * getRange(quantumRange) <= getDistanceFrom(paired.xCoord + 0.5d, paired.yCoord + 0.5d, paired.zCoord + 0.5d));
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
        Pair pair = quantumRegistry.get(cable.getQuantumKey());
        if (pair != null)
        {
            TileEntityQuantumCable paired = pair.getPairedCable(cable);
            if (paired != null && !paired.isInvalid() && paired.hasWorldObj() && paired.getWorldObj().blockExists(paired.xCoord, paired.yCoord, paired.zCoord) && paired.isInRange(cable))
            {
                return paired;
            }
        }
        return null;
    }

    public static boolean addCable(TileEntityQuantumCable cable)
    {
        if (quantumRegistry.containsKey(cable.getQuantumKey()))
        {
            return quantumRegistry.get(cable.getQuantumKey()).addCable(cable);
        } else
        {
            Pair pair = new Pair(cable);
            quantumRegistry.put(pair.hashCode(), pair);
            return true;
        }
    }

    public void setQuantumKey(int key)
    {
        quantumKey = key;
        addCable(this);
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

    public int getQuantumKey()
    {
        return quantumKey;
    }

    private static class Pair
    {
        private final TileEntityQuantumCable[] cables = new TileEntityQuantumCable[2];
        private int key;

        private Pair(TileEntityQuantumCable cable)
        {
            this(cable.getQuantumKey());
            addCable(cable);
        }

        public Pair(int key)
        {
            this.key = key;
        }

        public boolean addCable(TileEntityQuantumCable cable)
        {
            cables[cable.equals(cables[0]) ? 0 : 1] = cable;
            return true;
        }

        public TileEntityQuantumCable getPairedCable(TileEntityQuantumCable cable)
        {
            return cables[0].equals(cable) ? cables[1] : cables[1].equals(cable) ? cables[0] : null;
        }

        @Override
        public int hashCode()
        {
            return key;
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
