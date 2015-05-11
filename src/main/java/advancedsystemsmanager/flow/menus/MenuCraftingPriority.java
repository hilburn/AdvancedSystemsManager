package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuCraftingPriority extends Menu
{
    public static final int RADIO_X = 5;
    public static final int RADIO_Y = 5;
    public static final int RADIO_MARGIN = 13;
    public static final String NBT_SELECTED = "SelectedOption";
    public RadioButtonList radioButtons;

    public MenuCraftingPriority(FlowComponent parent)
    {
        super(parent);

        radioButtons = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int selectedOption)
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeBoolean(selectedOption == 0);
                PacketHandler.sendDataToServer(dw);
            }
        };


        radioButtons.add(new RadioButton(RADIO_X, RADIO_Y, Names.PRIORITY_MOVE_FIRST));
        radioButtons.add(new RadioButton(RADIO_X, RADIO_Y + RADIO_MARGIN, Names.PRIORITY_CRAFT_FIRST));
    }

    @Override
    public String getName()
    {
        return "Priority";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        radioButtons.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {

    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        radioButtons.onClick(mX, mY, button);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {

    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {

    }

    @Override
    public void writeData(DataWriter dw)
    {
        dw.writeBoolean(radioButtons.getSelectedOption() == 0);
    }

    @Override
    public void readData(DataReader dr)
    {
        radioButtons.setSelectedOption(dr.readBoolean() ? 0 : 1);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        radioButtons.setSelectedOption(((MenuCraftingPriority)menu).radioButtons.getSelectedOption());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuCraftingPriority newDataPriority = ((MenuCraftingPriority)newData);

        if (radioButtons.getSelectedOption() != newDataPriority.radioButtons.getSelectedOption())
        {
            radioButtons.setSelectedOption(newDataPriority.radioButtons.getSelectedOption());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeBoolean(radioButtons.getSelectedOption() == 0);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_SELECTED));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setByte(NBT_SELECTED, (byte)radioButtons.getSelectedOption());
    }

    public boolean shouldPrioritizeCrafting()
    {
        return radioButtons.getSelectedOption() == 1;
    }

    public void setPrioritizeCrafting(boolean val)
    {
        radioButtons.setSelectedOption(val ? 1 : 0);
    }
}
