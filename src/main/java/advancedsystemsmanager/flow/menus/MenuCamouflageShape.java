package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.CheckBox;
import advancedsystemsmanager.flow.elements.CheckBoxList;
import advancedsystemsmanager.flow.elements.TextBoxNumber;
import advancedsystemsmanager.flow.elements.TextBoxNumberList;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;


public class MenuCamouflageShape extends MenuCamouflageAdvanced
{
    public static final int CHECK_BOX_X = 5;
    public static final int CHECK_BOX_Y = 3;
    public static final int CHECK_BOX_SPACING_X = 50;
    public static final int CHECK_BOX_SPACING_Y = 10;
    public static final int TEXT_X = 6;
    public static final int TEXT_X_TO = 55;
    public static final int TEXT_Y = 28;
    public static final int TEXT_BOX_X = 30;
    public static final int TEXT_BOX_Y = 25;
    public static final int TEXT_BOX_SPACING_X = 40;
    public static final int TEXT_BOX_SPACING_Y = 15;
    public static final String NBT_USE = "Use";
    public static final String NBT_COLLISION = "UseCollision";
    public static final String NBT_FULL = "FullCollision";
    public static final String NBT_MIN_X = "MinX";
    public static final String NBT_MAX_X = "MaxX";
    public static final String NBT_MIN_Y = "MinY";
    public static final String NBT_MAX_Y = "MaxY";
    public static final String NBT_MIN_Z = "MinZ";
    public static final String NBT_MAX_Z = "MaxZ";
    public CheckBoxList checkBoxes;
    public boolean inUse;
    public boolean useCollision;
    public boolean fullCollision;
    public TextBoxNumberList textBoxes;
    public String[] coordinates = {Names.X, Names.Y, Names.Z};

    public MenuCamouflageShape(FlowComponent parent)
    {
        super(parent);

        checkBoxes = new CheckBoxList();

        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.CAMOUFLAGE_BOUNDS_USE, CHECK_BOX_X, CHECK_BOX_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                inUse = val;
            }

            @Override
            public boolean getValue()
            {
                return inUse;
            }
        });

        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.CAMOUFLAGE_COLLISION_USE, CHECK_BOX_X, CHECK_BOX_Y + CHECK_BOX_SPACING_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                useCollision = val;
            }

            @Override
            public boolean getValue()
            {
                return useCollision;
            }

            @Override
            public boolean isVisible()
            {
                return inUse;
            }
        });

        checkBoxes.addCheckBox(new CheckBox(getParent(), Names.CAMOUFLAGE_COLLISION_FULL, CHECK_BOX_X + CHECK_BOX_SPACING_X, CHECK_BOX_Y + CHECK_BOX_SPACING_Y)
        {
            @Override
            public void setValue(boolean val)
            {
                fullCollision = val;
            }

            @Override
            public boolean getValue()
            {
                return fullCollision;
            }

            @Override
            public boolean isVisible()
            {
                return inUse && useCollision;
            }
        });


        textBoxes = new TextBoxNumberList();
        for (int i = 0; i < 6; i++)
        {
            int x = i % 2;
            int y = i / 2;

            textBoxes.addTextBox(new TextBoxRange(TEXT_BOX_X + TEXT_BOX_SPACING_X * x, TEXT_BOX_Y + TEXT_BOX_SPACING_Y * y, x == 0 ? 0 : 32));
        }

        loadDefault();

    }

    public void loadDefault()
    {
        inUse = false;
        useCollision = true;
        fullCollision = false;
        for (int i = 0; i < 6; i++)
        {
            ((TextBoxRange)textBoxes.getTextBox(i)).reset();
        }
    }

    public boolean shouldUpdate()
    {
        return inUse;
    }

    public boolean isUseCollision()
    {
        return useCollision;
    }

    public boolean isFullCollision()
    {
        return fullCollision;
    }

    public int getBounds(int id)
    {
        return textBoxes.getTextBox(id).getNumber();
    }

    @Override
    public String getName()
    {
        return Names.BOUNDS_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxes.onClick(mX, mY);
        if (inUse)
        {
            textBoxes.onClick(mX, mY, button);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean onKeyStroke(GuiManager gui, char c, int k)
    {
        if (inUse)
        {
            return textBoxes.onKeyStroke(gui, c, k);
        } else
        {
            return super.onKeyStroke(gui, c, k);
        }
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuCamouflageShape menuShape = (MenuCamouflageShape)menu;
        if (menuShape.inUse)
        {
            inUse = true;
            useCollision = menuShape.useCollision;
            fullCollision = menuShape.fullCollision;
            for (int i = 0; i < 6; i++)
            {
                textBoxes.getTextBox(i).setNumber(menuShape.textBoxes.getTextBox(i).getNumber());
            }
        } else
        {
            loadDefault();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        inUse = nbtTagCompound.getBoolean(NBT_USE);
        if (inUse)
        {
            useCollision = nbtTagCompound.getBoolean(NBT_COLLISION);
            fullCollision = nbtTagCompound.getBoolean(NBT_FULL);
            textBoxes.getTextBox(0).setNumber(nbtTagCompound.getByte(NBT_MIN_X));
            textBoxes.getTextBox(1).setNumber(nbtTagCompound.getByte(NBT_MAX_X));
            textBoxes.getTextBox(2).setNumber(nbtTagCompound.getByte(NBT_MIN_Y));
            textBoxes.getTextBox(3).setNumber(nbtTagCompound.getByte(NBT_MAX_Y));
            textBoxes.getTextBox(4).setNumber(nbtTagCompound.getByte(NBT_MIN_Z));
            textBoxes.getTextBox(5).setNumber(nbtTagCompound.getByte(NBT_MAX_Z));
        } else
        {
            loadDefault();
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        nbtTagCompound.setBoolean(NBT_USE, inUse);
        if (inUse)
        {
            nbtTagCompound.setBoolean(NBT_COLLISION, useCollision);
            nbtTagCompound.setBoolean(NBT_FULL, fullCollision);
            nbtTagCompound.setByte(NBT_MIN_X, (byte)textBoxes.getTextBox(0).getNumber());
            nbtTagCompound.setByte(NBT_MAX_X, (byte)textBoxes.getTextBox(1).getNumber());
            nbtTagCompound.setByte(NBT_MIN_Y, (byte)textBoxes.getTextBox(2).getNumber());
            nbtTagCompound.setByte(NBT_MAX_Y, (byte)textBoxes.getTextBox(3).getNumber());
            nbtTagCompound.setByte(NBT_MIN_Z, (byte)textBoxes.getTextBox(4).getNumber());
            nbtTagCompound.setByte(NBT_MAX_Z, (byte)textBoxes.getTextBox(5).getNumber());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);

        checkBoxes.draw(gui, mX, mY);
        if (inUse)
        {
            textBoxes.draw(gui, mX, mY);
            for (int i = 0; i < coordinates.length; i++)
            {
                gui.drawString(coordinates[i], TEXT_X, TEXT_Y + TEXT_BOX_SPACING_Y * i, 0x404040);
                gui.drawString(Names.TO, TEXT_X_TO, TEXT_Y + TEXT_BOX_SPACING_Y * i, 0x404040);
            }
        }
    }

    @Override
    public String getWarningText()
    {
        return Names.BOUNDS_WARNING;
    }

    public class TextBoxRange extends TextBoxNumber
    {
        public int defaultNumber;

        public TextBoxRange(int x, int y, int defaultNumber)
        {
            super(getParent(), x, y, 2, false);
            this.defaultNumber = defaultNumber;
        }

        @Override
        public int getMaxNumber()
        {
            return 32;
        }

        public void reset()
        {
            setNumber(defaultNumber);
        }
    }


}
