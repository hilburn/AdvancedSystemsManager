package advancedfactorymanager.components;

import advancedfactorymanager.helpers.Localization;
import advancedfactorymanager.interfaces.ContainerManager;
import advancedfactorymanager.interfaces.GuiManager;
import advancedfactorymanager.network.DataBitHelper;
import advancedfactorymanager.network.DataReader;
import advancedfactorymanager.network.DataWriter;
import advancedfactorymanager.network.PacketHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;


public class ComponentMenuCamouflageShape extends ComponentMenuCamouflageAdvanced
{
    public ComponentMenuCamouflageShape(FlowComponent parent)
    {
        super(parent);

        checkBoxes = new CheckBoxList();

        checkBoxes.addCheckBox(new CheckBox(Localization.CAMOUFLAGE_BOUNDS_USE, CHECK_BOX_X, CHECK_BOX_Y)
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

            @Override
            public void onUpdate()
            {
                sendCheckBoxPacket();
            }
        });

        checkBoxes.addCheckBox(new CheckBox(Localization.CAMOUFLAGE_COLLISION_USE, CHECK_BOX_X, CHECK_BOX_Y + CHECK_BOX_SPACING_Y)
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
            public void onUpdate()
            {
                sendCheckBoxPacket();
            }

            @Override
            public boolean isVisible()
            {
                return inUse;
            }
        });

        checkBoxes.addCheckBox(new CheckBox(Localization.CAMOUFLAGE_COLLISION_FULL, CHECK_BOX_X + CHECK_BOX_SPACING_X, CHECK_BOX_Y + CHECK_BOX_SPACING_Y)
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
            public void onUpdate()
            {
                sendCheckBoxPacket();
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

            textBoxes.addTextBox(new TextBoxRange(i, TEXT_BOX_X + TEXT_BOX_SPACING_X * x, TEXT_BOX_Y + TEXT_BOX_SPACING_Y * y, x == 0 ? 0 : 32));
        }

        loadDefault();

    }

    public void sendCheckBoxPacket()
    {
        DataWriter dw = getWriterForServerComponentPacket();
        dw.writeData(0, DataBitHelper.CAMOUFLAGE_BOUND_TYPE);
        dw.writeBoolean(inUse);
        if (inUse)
        {
            dw.writeBoolean(useCollision);
            dw.writeBoolean(fullCollision);
        }
        PacketHandler.sendDataToServer(dw);
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

    public class TextBoxRange extends TextBoxNumber
    {
        public int id;
        public int defaultNumber;

        public TextBoxRange(int id, int x, int y, int defaultNumber)
        {
            super(x, y, 2, false);
            this.defaultNumber = defaultNumber;
            this.id = id;
        }

        @Override
        public void onNumberChanged()
        {
            DataWriter dw = getWriterForServerComponentPacket();
            dw.writeData(id + 1, DataBitHelper.CAMOUFLAGE_BOUND_TYPE);
            dw.writeData(getNumber(), DataBitHelper.CAMOUFLAGE_BOUNDS);
            PacketHandler.sendDataToServer(dw);
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

    public CheckBoxList checkBoxes;
    public boolean inUse;
    public boolean useCollision;
    public boolean fullCollision;
    public TextBoxNumberList textBoxes;

    @Override
    protected String getWarningText()
    {
        return Localization.BOUNDS_WARNING.toString();
    }

    @Override
    public String getName()
    {
        return Localization.BOUNDS_MENU.toString();
    }

    public Localization[] coordinates = {Localization.X, Localization.Y, Localization.Z};

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
                gui.drawString(coordinates[i].toString(), TEXT_X, TEXT_Y + TEXT_BOX_SPACING_Y * i, 0x404040);
                gui.drawString(Localization.TO.toString(), TEXT_X_TO, TEXT_Y + TEXT_BOX_SPACING_Y * i, 0x404040);
            }
        }
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        checkBoxes.onClick(mX, mY);
        if (inUse)
        {
            textBoxes.onClick(mX, mY, button);
        }
    }


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
        dw.writeBoolean(inUse);
        if (inUse)
        {
            dw.writeBoolean(useCollision);
            dw.writeBoolean(fullCollision);
            for (int i = 0; i < 6; i++)
            {
                dw.writeData(textBoxes.getTextBox(i).getNumber(), DataBitHelper.CAMOUFLAGE_BOUNDS);
            }
        }
    }

    @Override
    public void readData(DataReader dr)
    {
        inUse = dr.readBoolean();
        if (inUse)
        {
            useCollision = dr.readBoolean();
            fullCollision = dr.readBoolean();
            for (int i = 0; i < 6; i++)
            {
                textBoxes.getTextBox(i).setNumber(dr.readData(DataBitHelper.CAMOUFLAGE_BOUNDS));
            }
        } else
        {
            loadDefault();
        }
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

    @Override
    public void copyFrom(ComponentMenu menu)
    {
        ComponentMenuCamouflageShape menuShape = (ComponentMenuCamouflageShape)menu;
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
    public void refreshData(ContainerManager container, ComponentMenu newData)
    {
        ComponentMenuCamouflageShape newDataShape = (ComponentMenuCamouflageShape)newData;
        if (inUse != newDataShape.inUse || useCollision != newDataShape.useCollision || fullCollision != newDataShape.fullCollision)
        {
            inUse = newDataShape.inUse;

            DataWriter dw = getWriterForClientComponentPacket(container);
            dw.writeData(0, DataBitHelper.CAMOUFLAGE_BOUND_TYPE);
            dw.writeBoolean(inUse);
            if (!inUse)
            {
                loadDefault();
            } else
            {
                useCollision = newDataShape.useCollision;
                fullCollision = newDataShape.fullCollision;
                dw.writeBoolean(useCollision);
                dw.writeBoolean(fullCollision);
            }
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        for (int i = 0; i < 6; i++)
        {
            if (textBoxes.getTextBox(i).getNumber() != newDataShape.textBoxes.getTextBox(i).getNumber())
            {
                textBoxes.getTextBox(i).setNumber(newDataShape.textBoxes.getTextBox(i).getNumber());

                DataWriter dw = getWriterForClientComponentPacket(container);
                dw.writeData(i + 1, DataBitHelper.CAMOUFLAGE_BOUND_TYPE);
                dw.writeData(textBoxes.getTextBox(i).getNumber(), DataBitHelper.CAMOUFLAGE_BOUNDS);
                PacketHandler.sendDataToListeningClients(container, dw);
            }
        }
    }

    public static final String NBT_USE = "Use";
    public static final String NBT_COLLISION = "UseCollision";
    public static final String NBT_FULL = "FullCollision";
    public static final String NBT_MIN_X = "MinX";
    public static final String NBT_MAX_X = "MaxX";
    public static final String NBT_MIN_Y = "MinY";
    public static final String NBT_MAX_Y = "MaxY";
    public static final String NBT_MIN_Z = "MinZ";
    public static final String NBT_MAX_Z = "MaxZ";

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
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

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        int id = dr.readData(DataBitHelper.CAMOUFLAGE_BOUND_TYPE);
        if (id == 0)
        {
            inUse = dr.readBoolean();
            if (inUse)
            {
                useCollision = dr.readBoolean();
                fullCollision = dr.readBoolean();
            } else
            {
                loadDefault();
            }
        } else
        {
            textBoxes.getTextBox(id - 1).setNumber(dr.readData(DataBitHelper.CAMOUFLAGE_BOUNDS));
        }
    }


}
