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
    public void copyFrom(Setting setting)
    {
        content = ((LiquidSetting)setting).content;
    }

    @Override
    public void load(NBTTagCompound settingTag)
    {
        super.load(settingTag);
        content = FluidRegistry.getFluid(settingTag.getShort(NBT_FLUID_ID));
    }

    @Override
    public void save(NBTTagCompound settingTag)
    {
        super.save(settingTag);
        settingTag.setShort(NBT_FLUID_ID, (short)content.getID());
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

    @Override
    public void readContentData(ASMPacket packet)
    {
        setLiquidFromId(packet.readShort());
    }

    @Override
    public void writeContentData(ASMPacket packet)
    {
        packet.writeShort(content.getID());
    }
}
