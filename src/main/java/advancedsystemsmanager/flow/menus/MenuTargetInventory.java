package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
        textBoxes.addTextBox(startTextBox = new TextBoxNumber(getParent(), 39, 49, 2, false)
        {
            @Override
            public boolean writeData(ASMPacket packet)
            {
                packet.writeByte(selectedDirectionId);
                packet.writeByte(number);
                return true;
            }

            @Override
            public boolean readData(ASMPacket packet)
            {
                startRange[packet.readByte()] = packet.readUnsignedByte();
                return false;
            }
        });
        textBoxes.addTextBox(endTextBox = new TextBoxNumber(getParent(), 60, 49, 2, false)
        {
            @Override
            public boolean writeData(ASMPacket packet)
            {
                packet.writeByte(selectedDirectionId);
                packet.writeByte(number);
                return true;
            }

            @Override
            public boolean readData(ASMPacket packet)
            {
                endRange[packet.readByte()] = packet.readUnsignedByte();
                return false;
            }
        });
    }

    @Override
    public Button getSecondButton()
    {
        return new Button(getParent(), 27)
        {
            @Override
            public String getLabel()
            {
                return useAdvancedSetting(selectedDirectionId) ? Names.ALL_SLOTS : Names.ID_RANGE;
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

    @SideOnly(Side.CLIENT)
    @Override
    public void drawAdvancedComponent(GuiBase gui, int mX, int mY)
    {
        textBoxes.draw(gui, mX, mY);
    }

    @Override
    public void copyAdvancedSetting(Menu menu, int i)
    {
        MenuTargetInventory menuTarget = (MenuTargetInventory)menu;
        startRange[i] = menuTarget.startRange[i];
        endRange[i] = menuTarget.endRange[i];
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
    public boolean onKeyStroke(GuiBase gui, char c, int k)
    {
        return selectedDirectionId != -1 && useAdvancedSetting(selectedDirectionId) && textBoxes.onKeyStroke(gui, c, k);
    }
}
