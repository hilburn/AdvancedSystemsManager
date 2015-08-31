package advancedsystemsmanager.tileentities;

import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityQuantumCable extends TileEntity
{
    public static final String NBT_QUANTUM_KEY = "quantumKey";
    private static int NEXT_QUANTUM_KEY = 0;
    private static TIntObjectHashMap<Pair> quantumRegistry = new TIntObjectHashMap<Pair>();
    private int quantumKey;

    public static int getNextQuantumKey()
    {
        return NEXT_QUANTUM_KEY++;
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
    }

    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        setQuantumKey(tagCompound.getInteger(NBT_QUANTUM_KEY));
    }

    public static TileEntityQuantumCable getPairedCable(TileEntityQuantumCable cable)
    {
        Pair pair = quantumRegistry.get(cable.getQuantumKey());
        if (pair != null)
        {
            TileEntityQuantumCable paired = pair.getPairedCable(cable);
            if (paired != null && !paired.isInvalid() && paired.getWorldObj().blockExists(paired.xCoord, paired.yCoord, paired.zCoord))
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

    public int getQuantumKey()
    {
        return quantumKey;
    }

    private static class Pair
    {
        private final TileEntityQuantumCable[] cables = new TileEntityQuantumCable[2];
        private int cableID;
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
            if (cableID < 2)
            {
                cables[cableID++] = cable;
                return true;
            }
            return false;
        }

        public TileEntityQuantumCable getPairedCable(TileEntityQuantumCable cable)
        {
            return cables[0] == cable ? cables[1] : cables[1] == cable ? cables[0] : null;
        }

        @Override
        public int hashCode()
        {
            return key;
        }
    }
}
