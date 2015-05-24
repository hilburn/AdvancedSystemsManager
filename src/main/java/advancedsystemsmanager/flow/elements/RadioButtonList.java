package advancedsystemsmanager.flow.elements;


import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.ASMPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class RadioButtonList extends UpdateElement
{

    public static final int RADIO_SIZE = 8;
    public static final int RADIO_SRC_X = 30;
    public static final int RADIO_SRC_Y = 52;
    public static final int RADIO_TEXT_X = 12;
    public static final int RADIO_TEXT_Y = 2;

    public List<RadioButton> radioButtonList;
    public int selectedOption;

    public RadioButtonList(IPacketProvider packetProvider)
    {
        super(packetProvider);
        radioButtonList = new ArrayList<RadioButton>();
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY)
    {
        for (int i = 0; i < radioButtonList.size(); i++)
        {
            RadioButton radioButton = radioButtonList.get(i);

            if (radioButton.isVisible())
            {
                int srcRadioX = getSelectedOption() == i ? 1 : 0;
                int srcRadioY = CollisionHelper.inBounds(radioButton.getX(), radioButton.getY(), RADIO_SIZE, RADIO_SIZE, mX, mY) ? 1 : 0;

                gui.drawTexture(radioButton.getX(), radioButton.getY(), RADIO_SRC_X + srcRadioX * RADIO_SIZE, RADIO_SRC_Y + srcRadioY * RADIO_SIZE, RADIO_SIZE, RADIO_SIZE);
                gui.drawString(radioButton.getText(), radioButton.getX() + RADIO_TEXT_X, radioButton.getY() + RADIO_TEXT_Y, 0.7F, 0x404040);
            }
        }
    }

    public int getSelectedOption()
    {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption)
    {
        this.selectedOption = selectedOption;
    }

    public void onClick(int mX, int mY, int button)
    {
        for (int i = 0; i < radioButtonList.size(); i++)
        {
            RadioButton radioButton = radioButtonList.get(i);

            if (radioButton.isVisible() && CollisionHelper.inBounds(radioButton.getX(), radioButton.getY(), RADIO_SIZE, RADIO_SIZE, mX, mY) && getSelectedOption() != i)
            {
                updateSelectedOption(i);
                break;
            }
        }
    }

    public void updateSelectedOption(int selectedOption)
    {
        this.selectedOption = selectedOption;
        onUpdate();
    }

    public void add(RadioButton radioButton)
    {
        radioButtonList.add(radioButton);
    }

    public int size()
    {
        return radioButtonList.size();
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        packet.writeByte(getSelectedOption());
        return true;
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        selectedOption = packet.readByte();
        return false;
    }
}
