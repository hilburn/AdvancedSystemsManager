package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;


public class MenuCamouflageInside extends MenuCamouflageAdvanced
{
    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 5;
    public static final int RADIO_BUTTON_SPACING = 12;
    public static final String NBT_SETTING = "Setting";
    public RadioButtonList radioButtons;

    public MenuCamouflageInside(FlowComponent parent)
    {
        super(parent);

        radioButtons = new RadioButtonList(getParent());

        for (int i = 0; i < InsideSetType.values().length; i++)
        {
            radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + i * RADIO_BUTTON_SPACING, InsideSetType.values()[i].name));
        }
    }

    @Override
    public String getName()
    {
        return Names.INSIDE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        radioButtons.onClick(mX, mY, button);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        radioButtons.setSelectedOption(((MenuCamouflageInside)menu).radioButtons.getSelectedOption());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_SETTING));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_SETTING, (byte)radioButtons.getSelectedOption());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);

        radioButtons.draw(gui, mX, mY);
    }

    @Override
    public String getWarningText()
    {
        return Names.INSIDE_WARNING;
    }

    public InsideSetType getCurrentType()
    {
        return InsideSetType.values()[radioButtons.getSelectedOption()];
    }

    public enum InsideSetType
    {
        ONLY_OUTSIDE(Names.CAMOUFLAGE_ONLY_OUTSIDE),
        ONLY_INSIDE(Names.CAMOUFLAGE_ONLY_INSIDE),
        OPPOSITE(Names.CAMOUFLAGE_OPPOSITE_INSIDE),
        SAME(Names.CAMOUFLAGE_SAME_INSIDE),
        NOTHING(Names.CAMOUFLAGE_NO_UPDATE);


        public String name;

        InsideSetType(String name)
        {
            this.name = name;
        }
    }
}
