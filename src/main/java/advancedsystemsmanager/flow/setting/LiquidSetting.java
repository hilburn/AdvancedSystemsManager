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
    public Fluid fluid;

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
    public int getDefaultAmount()
    {
        return 1000;
    }

    @Override
    public Fluid getContent()
    {
        return fluid;
    }

    @Override
    public boolean isValid()
    {
        return fluid != null;
    }

    @Override
    public void copyFrom(Setting setting)
    {
        fluid = ((LiquidSetting)setting).fluid;
    }

    @Override
    public void load(NBTTagCompound settingTag)
    {
        super.load(settingTag);
        fluid = FluidRegistry.getFluid(settingTag.getShort(NBT_FLUID_ID));
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        super.save(settingTag);
        settingTag.setShort(NBT_FLUID_ID, (short)fluid.getID());
    }

    public int getLiquidId()
    {
        return fluid.getID();
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    @Override
    public void setContent(Fluid obj)
    {
        fluid = obj;
        setDefaultAmount();
    }

    @Override
    public boolean isContentEqual(Fluid check)
    {
        return fluid == check;
    }

    @Override
    public void writeContentData(ASMPacket packet)
    {
        super.writeContentData(packet);
        packet.writeShort(fluid.getID());
    }

    @Override
    public void readContentData(ASMPacket packet)
    {
        super.readContentData(packet);
        setLiquidFromId(packet.readShort());
    }

    public void setLiquidFromId(int id)
    {
        fluid = FluidRegistry.getFluid(id);
    }
}
