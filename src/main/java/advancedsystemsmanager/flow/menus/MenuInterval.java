package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuInterval extends Menu
{
    public static final int TEXT_BOX_X = 15;
    public static final int TEXT_BOX_Y = 35;
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 10;
    public static final int TEXT_Y2 = 15;
    public static final int TEXT_SECONDS_X = 60;
    public static final int TEXT_SECOND_Y = 38;
    public static final String NBT_INTERVAL = "Interval";
    public TextBoxNumberList textBoxes;
    public TextBoxNumber interval;

    public MenuInterval(FlowComponent parent)
    {
        super(parent);

        textBoxes = new TextBoxNumberList();
        textBoxes.addTextBox(interval = new TextBoxNumber(getParent(), TEXT_BOX_X, TEXT_BOX_Y, 3, true));

        interval.setNumber(1);
    }

    @Override
    public String getName()
    {
        return Names.INTERVAL_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        gui.drawSplitString(Names.INTERVAL_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 0.7F, 0x404040);
        gui.drawString(Names.SECOND, TEXT_SECONDS_X, TEXT_SECOND_Y, 0.7F, 0x404040);
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

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return textBoxes.onKeyStroke(gui, c, k);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        setInterval(((MenuInterval)menu).getInterval());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        setInterval(nbtTagCompound.getShort(NBT_INTERVAL));
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setShort(NBT_INTERVAL, (short)getInterval());
    }

    @Override
    public boolean isVisible()
    {
        return getParent().getConnectionSet() != ConnectionSet.DELAYED;
    }

    public int getInterval()
    {
        return interval.getNumber();
    }

    public void setInterval(int val)
    {
        interval.setNumber(val);
    }
}
