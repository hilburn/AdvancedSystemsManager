package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
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

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                ASMPacket dw = getWriterForServerComponentPacket();
                dw.writeByte(selectedDirectionId);
                dw.writeByte(DataTypeHeader.START_OR_TANK_DATA.getId());
                dw.writeBoolean(selectedOption == 1);
                PacketHandler.sendDataToServer(dw);
            }
        };

        radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y, Names.EMPTY_TANK));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + RADIO_BUTTON_SPACING, Names.FILLED_TANK));
    }

    @Override
    public Button getSecondButton()
    {
        return new Button(27)
        {
            @Override
            public String getLabel()
            {
                return useAdvancedSetting(selectedDirectionId) ? Names.ADVANCED_MODE : Names.SIMPLE_MODE;
            }

            @Override
            public String getMouseOverText()
            {
                return useAdvancedSetting(selectedDirectionId) ? Names.ADVANCED_MODE_LONG : Names.SIMPLE_MODE_LONG;
            }

            @Override
            public void onClicked()
            {
                writeData(DataTypeHeader.USE_ADVANCED_SETTING, useAdvancedSetting(selectedDirectionId) ? 0 : 1);
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
    public void refreshAdvancedComponentData(ContainerManager container, Menu newData, int i)
    {
        MenuTargetTank newDataTarget = (MenuTargetTank)newData;

        if (onlyFull[i] != newDataTarget.onlyFull[i])
        {
            onlyFull[i] = newDataTarget.onlyFull[i];

            ASMPacket dw = getWriterForClientComponentPacket(container);
            dw.writeByte(i);
            dw.writeByte(DataTypeHeader.START_OR_TANK_DATA.getId());
            dw.writeBoolean(onlyFull[i]);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
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
