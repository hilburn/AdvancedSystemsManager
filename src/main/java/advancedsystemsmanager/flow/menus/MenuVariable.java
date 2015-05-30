package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.*;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

public class MenuVariable extends Menu implements IPacketSync
{
    public static final int RADIO_BUTTON_X = 5;
    public static final int RADIO_BUTTON_Y = 28;
    public static final int RADIO_BUTTON_SPACING = 12;
    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 52;
    public static final String NBT_VARIABLE = "Variable";
    public static final String NBT_MODE = "Mode";
    public static final String NBT_EXECUTED = "Executed";
    public RadioButtonList radioButtons;
    ScrollController<Variable> variables;
    public int selectedVariable;
    private byte id;
    public boolean executed;

    public MenuVariable(final FlowComponent parent)
    {
        super(parent);

        parent.registerSyncable(this);
        selectedVariable = -1;

        radioButtons = new RadioButtonList(getParent());

        for (VariableMode mode : VariableMode.values())
        {
            radioButtons.add(new RadioButton(RADIO_BUTTON_X, RADIO_BUTTON_Y + mode.ordinal() * RADIO_BUTTON_SPACING, mode.toString())
            {
                @Override
                public boolean isVisible()
                {
                    return !isDeclaration();
                }
            });
        }

        variables = new ScrollVariable(parent)
        {

            @Override
            public void onClick(Variable variable, int mX, int mY, int button)
            {
                setSelectedVariable(variable.colour);
                sendUpdatePacket();
            }

            @Override
            public void draw(GuiManager gui, Variable variable, int x, int y, boolean hover)
            {
                int srcInventoryX = selectedVariable == variable.colour ? 1 : 0;
                int srcInventoryY = hover ? 1 : 0;

                gui.drawTexture(x, y, MenuContainer.INVENTORY_SRC_X + srcInventoryX * MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SRC_Y + srcInventoryY * MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SIZE, MenuContainer.INVENTORY_SIZE);
                variable.draw(gui, x, y);
            }
        };

    }

    public boolean isDeclaration()
    {
        return getParent().getConnectionSet() == ConnectionSet.EMPTY;
    }

    public VariableMode getVariableMode()
    {
        return VariableMode.values()[radioButtons.getSelectedOption()];
    }

    @Override
    public String getName()
    {
        return Names.VARIABLE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        radioButtons.draw(gui, mX, mY);
        variables.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        variables.drawMouseOver(gui, mX, mY);
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        radioButtons.onClick(mX, mY, button);
        variables.onClick(mX, mY, button);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        return variables.onKeyStroke(gui, c, k);
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {
    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {
        variables.onRelease(mX, mY);
    }

    @Override
    public void doScroll(int scroll)
    {
        variables.doScroll(scroll);
    }

    @Override
    public void update(float partial)
    {
        variables.update(partial);
    }

    @Override
    public void copyFrom(Menu menu)
    {
        setSelectedVariable(((MenuVariable)menu).selectedVariable);
        radioButtons.setSelectedOption(((MenuVariable)menu).radioButtons.getSelectedOption());
        executed = ((MenuVariable)menu).executed;
    }

    public Variable getVariable()
    {
        return getParent().getManager().getVariable(getSelectedVariable());
    }

    public int getSelectedVariable()
    {
        return selectedVariable;
    }

    public void setSelectedVariable(int val)
    {
        boolean declaration = isDeclaration();
        if (declaration)
        {
            getParent().getManager().removeVariableDeclaration(selectedVariable, getParent());
            selectedVariable = val;
            getParent().getManager().updateDeclaration(getParent(), selectedVariable);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        setSelectedVariable(nbtTagCompound.getInteger(NBT_VARIABLE));
        radioButtons.setSelectedOption(nbtTagCompound.getByte(NBT_MODE));
        executed = nbtTagCompound.getBoolean(NBT_EXECUTED);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setInteger(NBT_VARIABLE, selectedVariable);
        nbtTagCompound.setByte(NBT_MODE, (byte)radioButtons.getSelectedOption());
        nbtTagCompound.setBoolean(NBT_EXECUTED, executed);
    }

    @Override
    public void addErrors(List<String> errors)
    {
        Variable variable = getVariable();
        if (variable == null || !variable.isValid())
        {
            errors.add(Names.NOT_DECLARED_ERROR);
        } else if (isDeclaration() && variable.getDeclaration().getId() != getParent().getId())
        {
            errors.add(Names.ALREADY_DECLARED_ERROR);
        }
    }

    @Override
    public void setId(int id)
    {
        this.id = (byte)id;
    }

    private void sendUpdatePacket()
    {
        ASMPacket packet = parent.getSyncPacket();
        packet.writeByte(id);
        packet.writeMedium(selectedVariable);
        packet.sendServerPacket();
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        setSelectedVariable(packet.readUnsignedMedium());
        return false;
    }


    public enum VariableMode
    {
        ADD(Names.ADD),
        REMOVE(Names.REMOVE),
        SET(Names.SET);

        public String name;

        VariableMode(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }
}
