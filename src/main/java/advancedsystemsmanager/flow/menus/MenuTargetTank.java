package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuTargetTank extends MenuTarget
{
    public static final int RADIO_BUTTON_X = 36;
    public static final int RADIO_BUTTON_Y = 45;
    public static final int RADIO_BUTTON_SPACING = 12;
    public static final String NBT_FULL = "ONLY_FULL";
    public boolean[] onlyFull = new boolean[directions.length];
    public RadioButtonList radioButtons;

    public MenuTargetTank(FlowComponent parent)
    {
        super(parent);

        radioButtons = new RadioButtonList(getParent());

        radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y, Names.EMPTY_TANK));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + RADIO_BUTTON_SPACING, Names.FILLED_TANK));
    }

    @Override
    public Button getSecondButton()
    {
        return new Button(getParent(), 27)
        {
            @Override
            public String getLabel()
            {
                return useAdvancedSetting(selectedDirectionId) ? Names.ADVANCED_MODE : Names.SIMPLE_MODE;
            }

            @Override
            public boolean writeData(ASMPacket packet)
            {
                packet.writeByte(selectedDirectionId << 1 | (useAdvancedSetting(selectedDirectionId) ? 0 : 1));
                advancedDirections[selectedDirectionId] = !advancedDirections[selectedDirectionId];
                return true;
            }

            @Override
            public boolean readData(ASMPacket packet)
            {
                int data = packet.readByte();
                advancedDirections[data >> 1] = (data & 1) == 1;
                return false;
            }
        };
    }

    @Override
    public void copyAdvancedSetting(Menu menu, int i)
    {
        MenuTargetTank menuTarget = (MenuTargetTank)menu;
        onlyFull[i] = menuTarget.onlyFull[i];
    }

    @Override
    public void loadAdvancedComponent(NBTTagCompound directionTag, int i)
    {
        onlyFull[i] = directionTag.getBoolean(NBT_FULL);
    }

    @Override
    public void saveAdvancedComponent(NBTTagCompound directionTag, int i)
    {
        directionTag.setBoolean(NBT_FULL, onlyFull[i]);
    }

    @Override
    public void refreshAdvancedComponent()
    {
        if (selectedDirectionId != -1)
        {
            radioButtons.setSelectedOption(onlyFull[selectedDirectionId] ? 1 : 0);
        }
    }

    @Override
    public void onAdvancedClick(int mX, int mY, int button)
    {
        radioButtons.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawAdvancedComponent(GuiManager gui, int mX, int mY)
    {
        radioButtons.draw(gui, mX, mY);
    }

    public boolean requireEmpty(int side)
    {
        return !onlyFull[side];
    }


}
