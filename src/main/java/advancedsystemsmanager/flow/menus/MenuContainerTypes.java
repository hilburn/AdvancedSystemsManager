package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MenuContainerTypes extends Menu
{
    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 5;
    public static final int CHECK_BOX_SPACING_X = 55;
    public static final int CHECK_BOX_SPACING_Y = 12;
    public static final String NBT_CHECKED = "Checked";
    public List<ISystemType> types;
    public boolean[] checked;
    public CheckBoxList checkBoxes;

    public MenuContainerTypes(FlowComponent parent)
    {
        super(parent);

        types = new ArrayList<ISystemType>();
        for (ISystemType connectionBlockType : SystemTypeRegistry.getTypes())
        {
            if (!connectionBlockType.isGroup())
            {
                types.add(connectionBlockType);
            }
        }

        checked = new boolean[types.size()];
        for (int i = 0; i < checked.length; i++)
        {
            checked[i] = true;
        }
        checkBoxes = new CheckBoxList();
        for (int i = 0; i < types.size(); i++)
        {
            final int id = i;
            int x = i % 2;
            int y = i / 2;
            checkBoxes.addCheckBox(new CheckBox(getParent(), types.get(i).getName(), CHECK_BOX_X + CHECK_BOX_SPACING_X * x, CHECK_BOX_Y + CHECK_BOX_SPACING_Y * y)
            {
                @Override
                public void setValue(boolean val)
                {
                    MenuContainerTypes.this.checked[id] = val;
                }

                @Override
                public boolean getValue()
                {
                    return MenuContainerTypes.this.checked[id];
                }
            });
        }

    }

    @Override
    public String getName()
    {
        return Names.CONTAINER_TYPE_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        checkBoxes.draw(gui, mX, mY);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxes.onClick(mX, mY);
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
    public void copyFrom(Menu menu)
    {
        MenuContainerTypes menuTypes = (MenuContainerTypes)menu;

        System.arraycopy(menuTypes.checked, 0, checked, 0, checked.length);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        short data = nbtTagCompound.getShort(NBT_CHECKED);
        for (int i = 0; i < checked.length; i++)
        {
            checked[i] = ((data >> i) & 1) != 0;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        short data = 0;
        for (int i = 0; i < checked.length; i++)
        {
            if (checked[i])
            {
                data |= 1 << i;
            }
        }
        nbtTagCompound.setShort(NBT_CHECKED, data);
    }


    public Set<ISystemType> getValidTypes()
    {
        Set<ISystemType> types = new HashSet<ISystemType>();
        for (int i = 0; i < getTypes().size(); i++)
        {
            if (getChecked()[i])
            {
                types.add(getTypes().get(i));
            }
        }
        return types;
    }

    public boolean[] getChecked()
    {
        return checked;
    }

    public List<ISystemType> getTypes()
    {
        return types;
    }
}
