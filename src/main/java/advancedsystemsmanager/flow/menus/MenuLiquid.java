package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.setting.LiquidSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuLiquid extends MenuStuff<Fluid>
{
    public TextBoxNumber amountTextBoxBuckets;
    public TextBoxNumber amountTextBoxMilli;


    public MenuLiquid(FlowComponent parent)
    {
        this(parent, true);
        //this(parent, !Settings.isAutoBlacklist());
    }

    public MenuLiquid(FlowComponent parent, boolean whitelist)
    {
        super(parent);

        numberTextBoxes.addTextBox(amountTextBoxBuckets = new TextBoxNumber(10, 50, 3, true)
        {
            @Override
            public boolean isVisible()
            {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void onNumberChanged()
            {
                sendAmountData();
            }
        });

        numberTextBoxes.addTextBox(amountTextBoxMilli = new TextBoxNumber(60, 50, 3, true)
        {
            @Override
            public boolean isVisible()
            {
                return selectedSetting.isLimitedByAmount();
            }

            @Override
            public void onNumberChanged()
            {
                sendAmountData();
            }
        });
        setFirstRadioButtonSelected(whitelist);
    }

    @Override
    public Setting<Fluid> getSetting(int id)
    {
        return new LiquidSetting(id);
    }

    public void sendAmountData()
    {
        selectedSetting.setAmount(amountTextBoxBuckets.getNumber() * 1000 + amountTextBoxMilli.getNumber());
    }

    @Override
    public String getName()
    {
        return Names.LIQUIDS_MENU;
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

    public LiquidSetting getSelectedSetting()
    {
        return (LiquidSetting)selectedSetting;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawResultObject(GuiManager gui, Object obj, int x, int y)
    {
        gui.drawFluid((Fluid)obj, x, y);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawSettingObject(GuiManager gui, Setting setting, int x, int y)
    {
        drawResultObject(gui, ((LiquidSetting)setting).getFluid(), x, y);
    }

    @Override
    public List<String> getResultObjectMouseOver(Object obj)
    {
        List<String> ret = new ArrayList<String>();
        ret.add(getDisplayName((Fluid)obj));
        return ret;
    }

    @Override
    public List<String> getSettingObjectMouseOver(Setting setting)
    {
        return getResultObjectMouseOver(((LiquidSetting)setting).getFluid());
    }

    @Override
    public void updateTextBoxes()
    {
        int amount = selectedSetting.getAmount();
        amountTextBoxBuckets.setNumber(amount / 1000);
        amountTextBoxMilli.setNumber(amount % 1000);
    }

    @Override
    public DataBitHelper getAmountBitLength()
    {
        return DataBitHelper.MENU_LIQUID_AMOUNT;
    }

    @Override
    public void writeSpecificHeaderData(DataWriter dw, DataTypeHeader header, Setting setting)
    {
        LiquidSetting liquidSetting = (LiquidSetting)setting;
        switch (header)
        {
            case SET_ITEM:
                dw.writeData(liquidSetting.getLiquidId(), DataBitHelper.MENU_FLUID_ID);
        }
    }


    @SideOnly(Side.CLIENT)
    @Override
    public List updateSearch(String search, boolean showAll)
    {
        List ret = new ArrayList(FluidRegistry.getRegisteredFluids().values());

        Iterator<Fluid> itemIterator = ret.iterator();

        if (!showAll)
        {
            while (itemIterator.hasNext())
            {

                Fluid fluid = itemIterator.next();

                if (!getDisplayName(fluid).toLowerCase().contains(search))
                {
                    itemIterator.remove();
                }
            }
        }

        return ret;
    }

    public static String getDisplayName(Fluid fluid)
    {
        //different mods store the name in different ways apparently
        String name = fluid.getLocalizedName(null);
        if (name.contains("."))
        {
            name = FluidRegistry.getFluidName(fluid.getID());
        }

        return name;
    }
}
