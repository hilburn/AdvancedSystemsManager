package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumSet;
import java.util.List;

public class MenuDelayed extends MenuTriggered
{
    public static final int TEXT_BOX_X = 15;
    public static final int TEXT_BOX_Y = 35;
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 10;
    public static final EnumSet<ConnectionOption> delayed = EnumSet.of(ConnectionOption.DELAY_OUTPUT);
    public static final String NBT_RESTART = "Restart";
    //    public static final int TEXT_Y2 = 15;
//    public static final int TEXT_SECOND_X = 60;
//    public static final int TEXT_SECOND_Y = 38;
    public TextBoxNumber intervalTicks;
    public TextBoxNumber intervalSeconds;
    public RadioButtonList buttonList;

    public MenuDelayed(FlowComponent parent)
    {
        super(parent);
        this.textBoxes.addTextBox(this.intervalSeconds = new TextBoxNumber(getParent(), TEXT_BOX_X, TEXT_BOX_Y, 3, true));
        this.textBoxes.addTextBox(this.intervalTicks = new TextBoxNumber(getParent(), TEXT_BOX_X + intervalSeconds.getWidth() + TEXT_MARGIN_X, TEXT_BOX_Y, 2, true)
        {
            @Override
            public int getMaxNumber()
            {
                return 19;
            }
        });
        this.buttonList = new RadioButtonList(getParent());
        buttonList.add(new RadioButton(TEXT_MARGIN_X, TEXT_BOX_Y + 20, Names.DELAY_RESTART));
        buttonList.add(new RadioButton(TEXT_MARGIN_X * 5 + intervalSeconds.getWidth(), TEXT_BOX_Y + 20, Names.DELAY_IGNORE));
        setDelay(5);
        buttonList.setSelectedOption(0);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Names.DELAY_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X, 0.7F, 4210752);
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
    public void writeData(ASMPacket dw)
    {
        super.writeData(dw);
        dw.writeBoolean(buttonList.getSelectedOption() == 0);
    }

    @Override
    public void readData(ASMPacket dr)
    {
        super.readData(dr);
        buttonList.setSelectedOption(dr.readBoolean() ? 0 : 1);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        super.writeToNBT(nbtTagCompound, pickup);
        nbtTagCompound.setBoolean(NBT_RESTART, buttonList.getSelectedOption() == 0);
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
    public void setCountdown()
    {
        int selected = buttonList.getSelectedOption();
        if (getDelay() >= 5 && (selected == 0 || (selected == 1 && counter == -1)))
        {
            super.setCountdown();
        }
    }

    @Override
    public void resetCounter()
    {
        counter = -1;
    }

    @Override
    public EnumSet<ConnectionOption> getConnectionSets()
    {
        return delayed;
    }

    @Override
    public boolean remove()
    {
        return counter < 0;
    }

    @Override
    public String getName()
    {
        return Names.DELAY_TRIGGER;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        if (this.isVisible() && this.counter >= 0) getParent().getManager().addQuickTrigger(getParent());
        buttonList.setSelectedOption(nbtTagCompound.getBoolean(NBT_RESTART) ? 0 : 1);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (getDelay() < 5 && isVisible())
        {
            errors.add(Names.DELAY_ERROR);
        }
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() == ConnectionSet.DELAYED;
    }
}
