package advancedfactorymanager.components;

import advancedfactorymanager.helpers.StevesEnum;
import advancedfactorymanager.interfaces.ContainerManager;
import advancedfactorymanager.interfaces.GuiManager;
import advancedfactorymanager.network.DataReader;
import advancedfactorymanager.network.DataWriter;
import advancedfactorymanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class ComponentMenuRFCondition extends ComponentMenu
{
    private CheckBoxList checkBoxes = new CheckBoxList();
    private WideNumberBoxList textBoxes;
    private WideNumberBox textBox;
    private boolean triggerBelow;

    public ComponentMenuRFCondition(FlowComponent parent)
    {
        super(parent);
        this.checkBoxes.addCheckBox(new CheckBox(StevesEnum.BELOW, 5, 50)
        {
            public void setValue(boolean val)
            {
                ComponentMenuRFCondition.this.triggerBelow = val;
            }

            public boolean getValue()
            {
                return ComponentMenuRFCondition.this.triggerBelow;
            }

            public void onUpdate()
            {
                ComponentMenuRFCondition.this.sendServerData(1);
            }
        });
        this.textBoxes = new WideNumberBoxList();
        this.textBoxes.addTextBox(this.textBox = new WideNumberBox(5, 30, 31)
        {
            @Override
            public void onNumberChanged()
            {
                ComponentMenuRFCondition.this.sendServerData(0);
            }
        });
        this.textBox.setNumber(0);
    }

    public String getName()
    {
        return StevesEnum.RF_CONDITION_MENU.toString();
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(StevesEnum.RF_CONDITION_INFO.toString(), 5, 5, 110, 0.7F, 4210752);
        this.checkBoxes.draw(gui, mX, mY);
        this.textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
    }

    public void onClick(int mX, int mY, int button)
    {
        this.checkBoxes.onClick(mX, mY);
        this.textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return this.textBoxes.onKeyStroke(gui, c, k);
    }

    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
    }

    public void writeData(DataWriter dw)
    {
        dw.writeData(this.textBox.getNumber(), 32);
        dw.writeBoolean(this.triggerBelow);
    }

    public void readData(DataReader dr)
    {
        this.textBox.setNumber(dr.readData(32));
        this.triggerBelow = dr.readBoolean();
    }

    public void copyFrom(ComponentMenu menu)
    {
        ComponentMenuRFCondition menuStrength = (ComponentMenuRFCondition)menu;
        this.textBox.setNumber(menuStrength.textBox.getNumber());
        this.triggerBelow = menuStrength.triggerBelow;
    }

    public void refreshData(ContainerManager container, ComponentMenu newData)
    {
        ComponentMenuRFCondition newDataStrength = (ComponentMenuRFCondition)newData;
        if (this.textBox.getNumber() != newDataStrength.textBox.getNumber())
        {
            this.textBox.setNumber(newDataStrength.textBox.getNumber());
            this.sendClientData(container, 0);
        }

        if (this.triggerBelow != newDataStrength.triggerBelow)
        {
            this.triggerBelow = newDataStrength.triggerBelow;
            this.sendClientData(container, 1);
        }

    }

    private void sendServerData(int id)
    {
        DataWriter dw = this.getWriterForServerComponentPacket();
        this.writeData(dw, id);
        PacketHandler.sendDataToServer(dw);
    }

    private void sendClientData(ContainerManager container, int id)
    {
        DataWriter dw = this.getWriterForClientComponentPacket(container);
        this.writeData(dw, id);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    private void writeData(DataWriter dw, int id)
    {
        boolean isTextBox = id == 0;
        dw.writeBoolean(isTextBox);
        if (isTextBox)
        {
            dw.writeData(textBox.getNumber(), 32);
        } else
        {
            dw.writeBoolean(this.triggerBelow);
        }

    }

    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        this.textBox.setNumber(nbtTagCompound.getInteger("textBox"));
        this.triggerBelow = nbtTagCompound.getBoolean("Inverted");
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger("textBox", this.textBox.getNumber());
        nbtTagCompound.setBoolean("Inverted", this.triggerBelow);
    }

    public boolean isVisible()
    {
        return true;
    }

    public void readNetworkComponent(DataReader dr)
    {
        if (dr.readBoolean())
        {
            this.textBox.setNumber(dr.readData(32));
        } else
        {
            this.triggerBelow = dr.readBoolean();
        }
    }

    public boolean isLessThan()
    {
        return this.triggerBelow;
    }

    public int getAmount()
    {
        return this.textBox.getNumber();
    }

    public void addErrors(List<String> errors)
    {
        if (textBox.getNumber() == 0 && triggerBelow) errors.add(StevesEnum.RF_CONDITION_ERROR.toString());
    }
}
