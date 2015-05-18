package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;


public abstract class Setting<Type>
{
    public int id;
    public Type content;
    public boolean isLimitedByAmount;

    public Setting(int id)
    {
        this.id = id;
        clear();
    }

    public void clear()
    {
        isLimitedByAmount = false;
    }

    @SideOnly(Side.CLIENT)
    public abstract List<String> getMouseOver();

    public int getId()
    {
        return id;
    }

    public boolean isLimitedByAmount()
    {
        return isLimitedByAmount;
    }

    public void setLimitedByAmount(boolean limitedByAmount)
    {
        isLimitedByAmount = limitedByAmount;
    }

    public void setDefaultAmount()
    {
        setAmount(getDefaultAmount());
    }

    public abstract int getDefaultAmount();

    public abstract int getAmount();

    public abstract void setAmount(int val);

    public abstract boolean isValid();

    public abstract void writeData(DataWriter dw);

    public abstract void readData(DataReader dr);

    public abstract void copyFrom(Setting setting);

    public abstract void load(NBTTagCompound settingTag);

    public abstract void save(NBTTagCompound settingTag);

    public abstract boolean isContentEqual(Setting otherSetting);

    public abstract void setContent(Type obj);

    public abstract boolean isContentEqual(Type check);

    public boolean isAmountSpecific()
    {
        return true;
    }

    public void delete()
    {
        clear();
    }
}