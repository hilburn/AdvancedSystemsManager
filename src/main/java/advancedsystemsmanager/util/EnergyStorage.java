package advancedsystemsmanager.util;

import net.minecraft.nbt.NBTTagCompound;

public class EnergyStorage
{
    public static final String MAX_ENERGY = "max";
    public static final String ENERGY = "energy";
    int maxEnergy;
    int energy;

    public EnergyStorage(int max)
    {
        this(max, 0);
    }

    public EnergyStorage(int max, int energy)
    {
        this.maxEnergy = max;
        this.energy = energy;
    }

    public static EnergyStorage readFromNBT(NBTTagCompound tagCompound)
    {
        return new EnergyStorage(tagCompound.getInteger(MAX_ENERGY), tagCompound.getInteger(ENERGY));
    }

    public int extractEnergy(int amount, boolean simulate)
    {
        amount = Math.min(amount, energy);
        if (!simulate)
            energy -= amount;
        return amount;
    }

    public int receiveEnergy(int amount, boolean simulate)
    {
        amount = Math.min(amount, maxEnergy - energy);
        if (!simulate)
            energy += amount;
        return amount;
    }

    public int getEnergyStored()
    {
        return energy;
    }

    public int getMaxEnergyStored()
    {
        return maxEnergy;
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setInteger(MAX_ENERGY, maxEnergy);
        tagCompound.setInteger(ENERGY, energy);
        return tagCompound;
    }
}
