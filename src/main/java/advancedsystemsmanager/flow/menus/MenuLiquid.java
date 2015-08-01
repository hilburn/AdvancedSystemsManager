package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.setting.LiquidSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Null;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class MenuLiquid extends MenuStuff<Fluid>
{
    public TextBoxNumber amountTextBoxBuckets;
    public TextBoxNumber amountTextBoxMilli;


    public MenuLiquid(FlowComponent parent)
    {
        this(parent, true);
    }

    public MenuLiquid(FlowComponent parent, boolean whitelist)
    {
        super(parent);

        numberTextBoxes.addTextBox(amountTextBoxBuckets = new TextBoxNumber(Null.NULL_PACKET, 10, 50, 3, true)
        {
            @Override
            public void setNumber(int number)
            {
                super.setNumber(number);
                selectedSetting.setAmount(selectedSetting.getAmount() % 1000 + number * 1000);
            }            @Override
            public boolean isVisible()
            {
                return selectedSetting.isLimitedByAmount();
            }


        });

        numberTextBoxes.addTextBox(amountTextBoxMilli = new TextBoxNumber(Null.NULL_PACKET, 60, 50, 3, true)
        {
            @Override
            public boolean isVisible()
            {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void setNumber(int number)
            {
                super.setNumber(number);
                selectedSetting.setAmount((selectedSetting.getAmount() / 1000) * 1000 + number);
            }
        });
        setFirstRadioButtonSelected(whitelist);
    }

    @Override
    public Setting<Fluid> getSetting(int id)
    {
        return new LiquidSetting(id);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<Fluid> updateSearch(String search, boolean showAll)
    {
        List<Fluid> ret = new ArrayList<Fluid>(FluidRegistry.getRegisteredFluids().values());
        Iterator<Fluid> itr = ret.iterator();
        Pattern pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
        if (!showAll)
        {
            while (itr.hasNext())
            {
                Fluid fluid = itr.next();
                if (!pattern.matcher(getDisplayName(fluid)).find())
                {
                    itr.remove();
                }
            }
        }
        return ret;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawResultObject(GuiManager gui, Fluid obj, int x, int y)
    {
        gui.drawFluid(obj, x, y);
    }

    @Override
    public List<String> getResultObjectMouseOver(Fluid obj)
    {
        List<String> ret = new ArrayList<String>();
        ret.add(getDisplayName(obj));
        return ret;
    }

    @Override
    public void updateTextBoxes()
    {
        int amount = selectedSetting.getAmount();
        amountTextBoxBuckets.setNumber(amount / 1000);
        amountTextBoxMilli.setNumber(amount % 1000);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawSettingObject(GuiManager gui, Setting setting, int x, int y)
    {
        drawResultObject(gui, ((LiquidSetting)setting).getFluid(), x, y);
    }

    @Override
    public List<String> getSettingObjectMouseOver(Setting setting)
    {
        return getResultObjectMouseOver(((LiquidSetting)setting).getFluid());
    }

    @Override
    protected boolean readSpecificData(ASMPacket packet, int action, Setting<Fluid> setting)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawInfoMenuContent(GuiManager gui, int mX, int mY)
    {
        if (selectedSetting.isLimitedByAmount())
        {
            gui.drawCenteredString(Names.BUCKETS, amountTextBoxBuckets.getX(), amountTextBoxBuckets.getY() - 7, 0.7F, amountTextBoxBuckets.getWidth(), 0x404040);
            gui.drawCenteredString(Names.MILLI_BUCKETS, amountTextBoxMilli.getX(), amountTextBoxMilli.getY() - 7, 0.55F, amountTextBoxMilli.getWidth(), 0x404040);
        }
    }

    public static String getDisplayName(Fluid fluid)
    {
        String name = fluid.getLocalizedName(null);
        if (name.contains("."))
        {
            name = FluidRegistry.getFluidName(fluid);
        }

        return name;
    }

    @Override
    public String getName()
    {
        return Names.LIQUIDS_MENU;
    }
}
