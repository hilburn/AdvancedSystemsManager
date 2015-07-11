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
    public boolean isLimitedByAmount;
    protected int amount;
    private int count;

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

    public void setDefaultAmount()
    {
        setAmount(getDefaultAmount());
    }

    public abstract int getDefaultAmount();

    public abstract Type getContent();

    public void resetCount()
    {
        this.count = this.amount;
    }

    public int getAmountLeft()
    {
        return count;
    }

    public void reduceAmount(int amount)
    {
        this.count -= amount;
    }

    public int getAmount()
    {
        return amount;
    }

    public void setAmount(int val)
    {
        this.amount = val;
    }

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

    public boolean isAmountSpecific()
    {
        return true;
    }

    public boolean isLimitedByAmount()
    {
        return isLimitedByAmount;
    }

    public void setLimitedByAmount(boolean limitedByAmount)
    {
        isLimitedByAmount = limitedByAmount;
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

    public abstract void setFluid(Type obj);

    public abstract boolean isContentEqual(Type check);

    public void delete()
    {
        clear();
    }

    public void writeContentData(ASMPacket packet)
    {
        if (isAmountSpecific())
        {
            packet.writeBoolean(isLimitedByAmount());
            if (isLimitedByAmount()) packet.writeVarIntToBuffer(amount);
        }
    }

    public void readContentData(ASMPacket packet)
    {
        if (isAmountSpecific())
        {
            setLimitedByAmount(packet.readBoolean());
            if (isLimitedByAmount())
            {
                this.amount = packet.readVarIntFromBuffer();
            }
        }
    }
}