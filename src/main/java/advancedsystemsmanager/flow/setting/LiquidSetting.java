package advancedsystemsmanager.flow.setting;

import advancedsystemsmanager.flow.menus.MenuLiquid;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.reference.Names;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.List;

public class LiquidSetting extends Setting<Fluid>
{
    public Fluid fluid;
    public int amount;

    public LiquidSetting(int id)
    {
        super(id);
    }

    @Override
    public void clear()
    {
        super.clear();

        fluid = null;
        setDefaultAmount();
    }

    @Override
    public List<String> getMouseOver()
    {
        List<String> ret = new ArrayList<String>();

        if (fluid == null)
        {
            ret.add(Names.NO_LIQUID_SELECTED);
        } else
        {
            ret.add(MenuLiquid.getDisplayName(fluid));
        }

        ret.add("");
        ret.add(Names.CHANGE_LIQUID);
        if (fluid != null)
        {
            ret.add(Names.EDIT_SETTING);
        }

        return ret;
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
        return fluid != null;
    }


    @Override
    public void writeData(DataWriter dw)
    {
        dw.writeData(fluid.getID(), DataBitHelper.MENU_FLUID_ID);
    }

    @Override
    public void readData(DataReader dr)
    {
        fluid = FluidRegistry.getFluid(dr.readData(DataBitHelper.MENU_FLUID_ID));
    }

    @Override
    public void copyFrom(Setting setting)
    {
        fluid = ((LiquidSetting)setting).fluid;
    }

    @Override
    public int getDefaultAmount()
    {
        return 1000;
    }

    public static final String NBT_FLUID_ID = "FluidId";
    public static final String NBT_FLUID_AMOUNT = "Amount";

    @Override
    public void load(NBTTagCompound settingTag)
    {
        //TODO load properly
        fluid = FluidRegistry.getFluid(settingTag.getShort(NBT_FLUID_ID));
        amount = settingTag.getInteger(NBT_FLUID_AMOUNT);
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        //TODO save properly
        settingTag.setShort(NBT_FLUID_ID, (short)fluid.getID());
        settingTag.setInteger(NBT_FLUID_AMOUNT, amount);
    }

    @Override
    public boolean isContentEqual(Setting otherSetting)
    {
        return fluid.getID() == ((LiquidSetting)otherSetting).fluid.getID();
    }

    @Override
    public void setContent(Object obj)
    {
        fluid = (Fluid)obj;
        setDefaultAmount();
    }

    @Override
    public boolean isContentEqual(Fluid check)
    {
        return fluid == check;
    }

    public int getLiquidId()
    {
        return fluid.getID();
    }

    public void setLiquidFromId(int id)
    {
        fluid = FluidRegistry.getFluid(id);
    }

    public Fluid getFluid()
    {
        return fluid;
    }
}
