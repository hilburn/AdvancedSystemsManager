package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import java.util.List;

public class MenuTargetInventory extends MenuTarget
{
    public static final String NBT_START = "StartRange";
    public static final String NBT_END = "EndRange";
    public TextBoxNumberList textBoxes;
    public TextBoxNumber startTextBox;
    public TextBoxNumber endTextBox;
    public int[] startRange = new int[directions.length];
    public int[] endRange = new int[directions.length];

    public MenuTargetInventory(FlowComponent parent)
    {
        super(parent);

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(startTextBox = new TextBoxNumber(39, 49, 2, false)
        {
            @Override
            public void onNumberChanged()
            {
                if (selectedDirectionId != -1 && getParent().getManager().getWorldObj().isRemote)
                {
                    writeData(DataTypeHeader.START_OR_TANK_DATA, getNumber());
                }
            }
        });
        textBoxes.addTextBox(endTextBox = new TextBoxNumber(60, 49, 2, false)
        {
            @Override
            public void onNumberChanged()
            {
                if (selectedDirectionId != -1 && getParent().getManager().getWorldObj().isRemote)
                {
                    writeData(DataTypeHeader.END, getNumber());
                }
            }
        });
    }

    @Override
    public Button getSecondButton()
    {
        return new Button(27)
        {
            @Override
            public String getLabel()
            {
                return useAdvancedSetting(selectedDirectionId) ? Names.ALL_SLOTS : Names.ID_RANGE;
            }

            @Override
            public String getMouseOverText()
            {
                return useAdvancedSetting(selectedDirectionId) ? Names.ALL_SLOTS_LONG : Names.ID_RANGE_LONG;
            }

            @Override
            public void onClicked()
            {
                writeData(DataTypeHeader.USE_ADVANCED_SETTING, useAdvancedSetting(selectedDirectionId) ? 0 : 1);
            }
        };
    }

    @Override
    public void writeAdvancedSetting(DataWriter dw, int i)
    {
        dw.writeData(startRange[i], DataBitHelper.MENU_TARGET_RANGE);
        dw.writeData(endRange[i], DataBitHelper.MENU_TARGET_RANGE);
    }

    @Override
    public void readAdvancedSetting(DataReader dr, int i)
    {
        startRange[i] = dr.readData(DataBitHelper.MENU_TARGET_RANGE);
        endRange[i] = dr.readData(DataBitHelper.MENU_TARGET_RANGE);
    }

    @Override
    public void resetAdvancedSetting(int i)
    {
        startRange[i] = endRange[i] = 0;
    }

    @Override
    public void copyAdvancedSetting(Menu menu, int i)
    {
        MenuTargetInventory menuTarget = (MenuTargetInventory)menu;
        startRange[i] = menuTarget.startRange[i];
        endRange[i] = menuTarget.endRange[i];
    }

    @Override
    public void refreshAdvancedComponentData(ContainerManager container, Menu newData, int i)
    {
        MenuTargetInventory newDataTarget = (MenuTargetInventory)newData;

        if (startRange[i] != newDataTarget.startRange[i])
        {
            startRange[i] = newDataTarget.startRange[i];

            writeUpdatedData(container, i, DataTypeHeader.START_OR_TANK_DATA, startRange[i]);
        }

        if (endRange[i] != newDataTarget.endRange[i])
        {
            endRange[i] = newDataTarget.endRange[i];

            writeUpdatedData(container, i, DataTypeHeader.END, endRange[i]);
        }
    }

    @Override
    public void loadAdvancedComponent(NBTTagCompound directionTag, int i)
    {
        startRange[i] = directionTag.getByte(NBT_START);
        endRange[i] = directionTag.getByte(NBT_END);
    }

    @Override
    public void saveAdvancedComponent(NBTTagCompound directionTag, int i)
    {
        directionTag.setByte(NBT_START, (byte)getStart(i));
        directionTag.setByte(NBT_END, (byte)getEnd(i));
    }

    public int getStart(int i)
    {
        return startRange[i];
    }

    public int getEnd(int i)
    {
        return endRange[i];
    }

    @Override
    public void addErrors(List<String> errors)
    {
        for (int i = 0; i < directions.length; i++)
        {
            if (isActive(i) && getStart(i) > getEnd(i))
            {
                errors.add(StatCollector.translateToLocal(LocalizationHelper.getDirectionString(i)) + " " + StatCollector.translateToLocal(Names.INVALID_RANGE));
            }
        }

        super.addErrors(errors);
    }

    @Override
    public void refreshAdvancedComponent()
    {
        if (selectedDirectionId != -1)
        {
            startTextBox.setNumber(startRange[selectedDirectionId]);
            endTextBox.setNumber(endRange[selectedDirectionId]);
        }
    }

    @Override
    public void onAdvancedClick(int mX, int mY, int button)
    {
        textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawAdvancedComponent(GuiManager gui, int mX, int mY)
    {
        textBoxes.draw(gui, mX, mY);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        if (selectedDirectionId != -1 && useAdvancedSetting(selectedDirectionId))
        {
            return textBoxes.onKeyStroke(gui, c, k);
        }


        return false;
    }
}
