package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.gui.ContainerManager;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataBitHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuInterval extends Menu
{
    public MenuInterval(FlowComponent parent)
    {
        super(parent);

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(interval = new TextBoxNumber(TEXT_BOX_X, TEXT_BOX_Y, 3, true)
        {
            @Override
            public void onNumberChanged()
            {
                DataWriter dw = getWriterForServerComponentPacket();
                dw.writeData(getNumber(), DataBitHelper.MENU_INTERVAL);
                PacketHandler.sendDataToServer(dw);
            }
        });

        interval.setNumber(1);
    }

    public static final int TEXT_BOX_X = 15;
    public static final int TEXT_BOX_Y = 35;
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 10;
    public static final int TEXT_Y2 = 15;

    public static final int TEXT_SECONDS_X = 60;
    public static final int TEXT_SECOND_Y = 38;

    @Override
    public String getName()
    {
        return Localization.INTERVAL_MENU.toString();
    }

    public TextBoxNumberList textBoxes;
    public TextBoxNumber interval;

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Localization.INTERVAL_INFO.toString(), TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 0.7F, 0x404040);
        gui.drawString(Localization.SECOND.toString(), TEXT_SECONDS_X, TEXT_SECOND_Y, 0.7F, 0x404040);
        textBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {

    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        textBoxes.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeData(DataWriter dw)
    {
        int val = getInterval();
        if (val == 0)
        {
            val = 1;
        }

        dw.writeData(val, DataBitHelper.MENU_INTERVAL);
    }

    @Override
    public void readData(DataReader dr)
    {
        setInterval(dr.readData(DataBitHelper.MENU_INTERVAL));
    }

    @Override
    public void copyFrom(Menu menu)
    {
        setInterval(((MenuInterval)menu).getInterval());
    }

    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuInterval newDataInterval = (MenuInterval)newData;

        if (newDataInterval.getInterval() != getInterval())
        {
            setInterval(newDataInterval.getInterval());

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeData(getInterval(), DataBitHelper.MENU_INTERVAL);
            PacketHandler.sendDataToListeningClients(container, dw);
        }
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        setInterval(dr.readData(DataBitHelper.MENU_INTERVAL));
    }

    public int getInterval()
    {
        return interval.getNumber();
    }

    public void setInterval(int val)
    {
        interval.setNumber(val);
    }

    public static final String NBT_INTERVAL = "Interval";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        setInterval(nbtTagCompound.getShort(NBT_INTERVAL));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setShort(NBT_INTERVAL, (short)getInterval());
    }

}
