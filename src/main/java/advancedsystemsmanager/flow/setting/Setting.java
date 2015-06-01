package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.network.ASMPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;


public abstract class Setting<Type>
{
    public static final String NBT_AMOUNT = "Amount";
    public static final String NBT_IS_LIMITED = "Limited";

    public int id;
    public Type content;
    public boolean isLimitedByAmount;
    int amount;

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

    public void setFuzzyType(int id)
    {

    }

    public abstract void copyFrom(Setting setting);

    public void load(NBTTagCompound settingTag)
    {
        if (isAmountSpecific())
        {
            setLimitedByAmount(settingTag.getBoolean(NBT_IS_LIMITED));
            if (isLimitedByAmount())
            {
                amount = settingTag.getInteger(NBT_AMOUNT);
            }
        }
    }

    public void save(NBTTagCompound settingTag)
    {
        if (isAmountSpecific())
        {
            settingTag.setBoolean(NBT_IS_LIMITED, isLimitedByAmount());
            if (isLimitedByAmount())
            {
                settingTag.setInteger(NBT_AMOUNT, amount);
            }
        }
    }

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

    public abstract void writeContentData(ASMPacket packet);

    public abstract void readContentData(ASMPacket packet);
}