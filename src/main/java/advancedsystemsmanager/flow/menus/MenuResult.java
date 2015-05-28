package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.flow.elements.RadioButtonList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class MenuResult extends Menu
{

    public static final int RADIO_X = 5;
    public static final int RADIO_Y = 5;
    public static final int RADIO_MARGIN = 13;
    public static final String NBT_SELECTED = "SelectedOption";
    public ConnectionSet[] sets;
    public RadioButtonList radioButtons;

    public MenuResult(FlowComponent parent)
    {
        super(parent);

        sets = parent.getType().getSets();

        radioButtons = new RadioButtonList(getParent())
        {
            @Override
            public void setSelectedOption(int selectedOption)
            {
                super.setSelectedOption(selectedOption);

                getParent().setConnectionSet(sets[radioButtons.getSelectedOption()]);

                if (getParent().getType() == CommandRegistry.VARIABLE)
                {
                    getParent().getManager().updateVariables();
                } else if (getParent().getType() == CommandRegistry.NODE)
                {
                    getParent().setParent(getParent().getParent());
                }
            }
        };

        for (int i = 0; i < sets.length; i++)
        {
            radioButtons.add(new RadioButton(RADIO_X, RADIO_Y + i * RADIO_MARGIN, sets[i].getName()));
        }

        for (int i = 0; i < sets.length; i++)
        {
            ConnectionSet set = sets[i];

            if (parent.getConnectionSet().equals(set))
            {
                radioButtons.setSelectedOption(i);
                break;
            }
        }
    }

    public void writeData(ASMPacket dw, int val)
    {
        dw.writeByte(val);
    }

    @Override
    public String getName()
    {
        return Names.CONNECTIONS_MENU;
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
    public void copyFrom(Menu menu)
    {
        radioButtons.setSelectedOption(((MenuResult)menu).radioButtons.getSelectedOption());
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

    @Override
    public boolean isVisible()
    {
        return sets.length > 1;
    }

}
