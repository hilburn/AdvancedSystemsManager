package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.flow.menus.MenuLiquid;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public class LiquidSetting extends Setting<Fluid>
{
    public static final String NBT_FLUID_ID = "FluidId";
    public static final String NBT_FLUID_AMOUNT = "Amount";
    public int amount;

    public LiquidSetting(int id)
    {
        super(id);
    }

    @Override
    public void clear()
    {
        super.clear();

        content = null;
        setDefaultAmount();
    }

    @Override
    public List<String> getMouseOver()
    {
        List<String> ret = new ArrayList<String>();

        if (content == null)
        {
            ret.add(Names.NO_LIQUID_SELECTED);
        } else
        {
            ret.add(MenuLiquid.getDisplayName(content));
        }

        ret.add("");
        ret.add(Names.CHANGE_LIQUID);
        if (content != null)
        {
            ret.add(Names.EDIT_SETTING);
        }

        return ret;
    }

    @Override
    public int getDefaultAmount()
    {
        return 1000;
    }

    @Override
    public int getAmount()
    {
        return amount;
    }

    @Override
    public void setAmount(int val)
    {
        amount = val;
    }

    @Override
    public boolean isValid()
    {
        return content != null;
    }

    @Override
    public void writeData(ASMPacket dw)
    {
        dw.writeShort(content.getID());
    }

    @Override
    public void readData(ASMPacket dr)
    {
        content = FluidRegistry.getFluid(dr.readShort());
    }

    @Override
    public void copyFrom(Setting setting)
    {
        content = ((LiquidSetting)setting).content;
    }

    @Override
    public void load(NBTTagCompound settingTag)
    {
        //TODO load properly
        content = FluidRegistry.getFluid(settingTag.getShort(NBT_FLUID_ID));
        amount = settingTag.getInteger(NBT_FLUID_AMOUNT);
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        //TODO save properly
        settingTag.setShort(NBT_FLUID_ID, (short)content.getID());
        settingTag.setInteger(NBT_FLUID_AMOUNT, amount);
    }

    @Override
    public boolean isContentEqual(Setting otherSetting)
    {
        return content == ((LiquidSetting)otherSetting).content;
    }

    @Override
    public void setContent(Fluid obj)
    {
        content = obj;
        setDefaultAmount();
    }

    @Override
    public boolean isContentEqual(Fluid check)
    {
        return content == check;
    }

    public int getLiquidId()
    {
        return content.getID();
    }

    public void setLiquidFromId(int id)
    {
        content = FluidRegistry.getFluid(id);
    }

    public Fluid getFluid()
    {
        return content;
    }
}
