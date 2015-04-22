package advancedfactorymanager.components;

import advancedfactorymanager.helpers.StevesEnum;
import advancedfactorymanager.interfaces.GuiManager;
import advancedfactorymanager.network.DataBitHelper;
import advancedfactorymanager.network.DataReader;
import advancedfactorymanager.network.DataWriter;
import advancedfactorymanager.network.PacketHandler;
import afm.asm.StevesHooks;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumSet;
import java.util.List;

public class ComponentMenuDelayed extends ComponentMenuTriggered
{
    public static final int TEXT_BOX_X = 15;
    public static final int TEXT_BOX_Y = 35;
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 10;
    //    public static final int TEXT_Y2 = 15;
//    public static final int TEXT_SECOND_X = 60;
//    public static final int TEXT_SECOND_Y = 38;
    public TextBoxNumber intervalTicks;
    public TextBoxNumber intervalSeconds;
    public RadioButtonList buttonList;
    public static final EnumSet<ConnectionOption> delayed = EnumSet.of(StevesEnum.DELAYED_OUTPUT);
    public static final String NBT_RESTART = "Restart";

    public ComponentMenuDelayed(FlowComponent parent)
    {
        super(parent);
        this.textBoxes.addTextBox(this.intervalSeconds = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 3, true)
        {
            public void onNumberChanged()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(getDelay(), DataBitHelper.MENU_INTERVAL);
                dw.writeBoolean(buttonList.getSelectedOption() == 0);
                PacketHandler.sendDataToServer(dw);
            }
        });
        this.textBoxes.addTextBox(this.intervalTicks = new TextBoxNumber(TEXT_BOX_X + intervalSeconds.getWidth() + TEXT_MARGIN_X, TEXT_BOX_Y, 2, true)
        {
            public void onNumberChanged()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(getDelay(), DataBitHelper.MENU_INTERVAL);
                dw.writeBoolean(buttonList.getSelectedOption() == 0);
                PacketHandler.sendDataToServer(dw);
            }

            @Override
            public int getMaxNumber()
            {
                return 19;
            }
        });
        this.buttonList = new RadioButtonList()
        {
            @Override
            public void updateSelectedOption(int i)
            {
                setSelectedOption(i);
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(getDelay(), DataBitHelper.MENU_INTERVAL);
                dw.writeBoolean(buttonList.getSelectedOption() == 0);
                PacketHandler.sendDataToServer(dw);
            }
        };
        buttonList.add(new RadioButton(TEXT_MARGIN_X, TEXT_BOX_Y + 20, StevesEnum.DELAY_RESTART));
        buttonList.add(new RadioButton(TEXT_MARGIN_X * 5 + intervalSeconds.getWidth(), TEXT_BOX_Y + 20, StevesEnum.DELAY_IGNORE));
        setDelay(5);
        buttonList.setSelectedOption(0);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(StevesEnum.DELAY_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X, 0.7F, 4210752);
        buttonList.draw(gui, mX, mY);
        //gui.drawString(Localization.SECOND.toString(), TEXT_SECOND_X, TEXT_SECOND_Y, 0.7F, 4210752);
        super.draw(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        super.onClick(mX, mY, button);
        buttonList.onClick(mX, mY, button);
    }

    @Override
    public void writeData(DataWriter dw)
    {
        super.writeData(dw);
        dw.writeBoolean(buttonList.getSelectedOption() == 0);
    }

    @Override
    public void readData(DataReader dr)
    {
        super.readData(dr);
        buttonList.setSelectedOption(dr.readBoolean() ? 0 : 1);
    }

    @Override
    public int getDelay()
    {
        return intervalTicks.getNumber() + intervalSeconds.getNumber() * 20;
    }

    @Override
    public void setDelay(int val)
    {
        intervalTicks.setNumber(val % 20);
        intervalSeconds.setNumber(val / 20);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        super.readFromNBT(nbtTagCompound, version, pickup);
        if (this.isVisible() && this.counter >= 0) StevesHooks.registerTicker(getParent(), this);
        buttonList.setSelectedOption(nbtTagCompound.getBoolean(NBT_RESTART) ? 0 : 1);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (getDelay() < 5 && isVisible())
        {
            errors.add(StevesEnum.DELAY_ERROR.toString());
        }
    }

    @Override
    protected EnumSet<ConnectionOption> getConnectionSets()
    {
        return delayed;
    }

    @Override
    public void setCountdown()
    {
        int selected = buttonList.getSelectedOption();
        if (getDelay() >= 5 && (selected == 0 || (selected == 1 && counter == -1)))
        {
            super.setCountdown();
        }
    }

    @Override
    public String getName()
    {
        return StevesEnum.DELAY_TRIGGER.toString();
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == StevesEnum.DELAYED;
    }

    @Override
    public boolean remove()
    {
        return counter < 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        super.writeToNBT(nbtTagCompound, pickup);
        nbtTagCompound.setBoolean(NBT_RESTART, buttonList.getSelectedOption() == 0);
    }

    @Override
    protected void resetCounter()
    {
        counter = -1;
    }
}
