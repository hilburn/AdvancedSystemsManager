package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.helpers.CollisionHelper;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class MenuTarget extends Menu
{


    public MenuTarget(FlowComponent parent)
    {
        super(parent);

        selectedDirectionId = -1;

    }

    public static final int DIRECTION_SIZE_W = 31;
    public static final int DIRECTION_SIZE_H = 12;
    public static final int DIRECTION_SRC_X = 0;
    public static final int DIRECTION_SRC_Y = 70;
    public static final int DIRECTION_X_LEFT = 2;
    public static final int DIRECTION_X_RIGHT = 88;
    public static final int DIRECTION_Y = 5;
    public static final int DIRECTION_MARGIN = 10;
    public static final int DIRECTION_TEXT_X = 2;
    public static final int DIRECTION_TEXT_Y = 3;

    public static final int BUTTON_SIZE_W = 42;
    public static final int BUTTON_SIZE_H = 12;
    public static final int BUTTON_SRC_X = 0;
    public static final int BUTTON_SRC_Y = 106;
    public static final int BUTTON_X = 39;
    public static final int BUTTON_TEXT_Y = 5;


    public Button[] buttons = {new Button(5)
    {
        @Override
        public String getLabel()
        {
            return isActive(selectedDirectionId) ? Localization.DEACTIVATE.toString() : Localization.ACTIVATE.toString();
        }

        @Override
        public String getMouseOverText()
        {
            return isActive(selectedDirectionId) ? Localization.DEACTIVATE_LONG.toString() : Localization.ACTIVATE_LONG.toString();
        }

        @Override
        public void onClicked()
        {
            writeData(DataTypeHeader.ACTIVATE, isActive(selectedDirectionId) ? 0 : 1);
        }
    },
            getSecondButton()};

    public abstract Button getSecondButton();


    @Override
    public String getName()
    {
        return Localization.TARGET_MENU.toString();
    }


    public static ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;

    public int selectedDirectionId;
    public boolean[] activatedDirections = new boolean[directions.length];
    public boolean[] useRangeForDirections = new boolean[directions.length];


    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        for (int i = 0; i < directions.length; i++)
        {
            ForgeDirection direction = directions[i];

            int x = getDirectionX(i);
            int y = getDirectionY(i);

            int srcDirectionX = isActive(i) ? 1 : 0;
            int srcDirectionY = selectedDirectionId != -1 && selectedDirectionId != i ? 2 : CollisionHelper.inBounds(x, y, DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY) ? 1 : 0;


            gui.drawTexture(x, y, DIRECTION_SRC_X + srcDirectionX * DIRECTION_SIZE_W, DIRECTION_SRC_Y + srcDirectionY * DIRECTION_SIZE_H, DIRECTION_SIZE_W, DIRECTION_SIZE_H);

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            int color = selectedDirectionId != -1 && selectedDirectionId != i ? 0x70404040 : 0x404040;
            gui.drawString(Localization.getForgeDirectionLocalization(i).toString(), x + DIRECTION_TEXT_X, y + DIRECTION_TEXT_Y, color);
            GL11.glPopMatrix();
        }

        if (selectedDirectionId != -1)
        {
            for (Button button : buttons)
            {
                int srcButtonY = CollisionHelper.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY) ? 1 : 0;

                gui.drawTexture(BUTTON_X, button.y, BUTTON_SRC_X, BUTTON_SRC_Y + srcButtonY * BUTTON_SIZE_H, BUTTON_SIZE_W, BUTTON_SIZE_H);
                gui.drawCenteredString(button.getLabel(), BUTTON_X, button.y + BUTTON_TEXT_Y, 0.5F, BUTTON_SIZE_W, 0x404040);
            }

            if (useAdvancedSetting(selectedDirectionId))
            {
                drawAdvancedComponent(gui, mX, mY);
            }
        }
    }


    public boolean isActive(int i)
    {
        return activatedDirections[i];
    }

    public int getDirectionX(int i)
    {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }


    public boolean useAdvancedSetting(int i)
    {
        return useRangeForDirections[i];
    }


    public int getDirectionY(int i)
    {
        return DIRECTION_Y + (DIRECTION_SIZE_H + DIRECTION_MARGIN) * (i / 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        if (selectedDirectionId != -1)
        {
            for (Button button : buttons)
            {
                if (CollisionHelper.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY))
                {
                    gui.drawMouseOver(button.getMouseOverText(), mX, mY);
                }
            }
        }
    }

    @Override
    public void onClick(int mX, int mY, int button)
    {
        for (int i = 0; i < directions.length; i++)
        {
            if (CollisionHelper.inBounds(getDirectionX(i), getDirectionY(i), DIRECTION_SIZE_W, DIRECTION_SIZE_H, mX, mY))
            {
                if (selectedDirectionId == i)
                {
                    selectedDirectionId = -1;
                } else
                {
                    selectedDirectionId = i;
                    refreshAdvancedComponent();
                }

                break;
            }
        }

        if (selectedDirectionId != -1)
        {
            for (Button optionButton : buttons)
            {
                if (CollisionHelper.inBounds(BUTTON_X, optionButton.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY))
                {
                    optionButton.onClicked();
                    break;
                }
            }

            if (useAdvancedSetting(selectedDirectionId))
            {
                onAdvancedClick(mX, mY, button);
            }
        }
    }

    @Override
    public void onDrag(int mX, int mY, boolean isMenuOpen)
    {

    }

    @Override
    public void onRelease(int mX, int mY, boolean isMenuOpen)
    {

    }

    public abstract class Button
    {
        public int y;

        public Button(int y)
        {
            this.y = y;
        }

        public abstract String getLabel();

        public abstract String getMouseOverText();

        public abstract void onClicked();
    }


    @Override
    public void writeData(DataWriter dw)
    {
        for (int i = 0; i < directions.length; i++)
        {
            dw.writeBoolean(isActive(i));
            dw.writeBoolean(useAdvancedSetting(i));
            if (useAdvancedSetting(i))
            {
                writeAdvancedSetting(dw, i);
            }

        }
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawAdvancedComponent(GuiManager gui, int mX, int mY);

    public abstract void refreshAdvancedComponent();

    public abstract void writeAdvancedSetting(DataWriter dw, int i);

    public abstract void readAdvancedSetting(DataReader dr, int i);

    public abstract void copyAdvancedSetting(Menu menuTarget, int i);

    public abstract void onAdvancedClick(int mX, int mY, int button);

    public abstract void loadAdvancedComponent(NBTTagCompound directionTag, int i);

    public abstract void saveAdvancedComponent(NBTTagCompound directionTag, int i);

    public abstract void resetAdvancedSetting(int i);

    public abstract void refreshAdvancedComponentData(ContainerManager container, Menu newData, int i);

    public abstract void readAdvancedNetworkComponent(DataReader dr, DataTypeHeader header, int i);

    @Override
    public void readData(DataReader dr)
    {
        for (int i = 0; i < directions.length; i++)
        {

            activatedDirections[i] = dr.readBoolean();
            useRangeForDirections[i] = dr.readBoolean();
            if (useAdvancedSetting(i))
            {
                readAdvancedSetting(dr, i);
            } else
            {
                resetAdvancedSetting(i);
            }

        }
    }

    @Override
    public void copyFrom(Menu menu)
    {
        MenuTarget menuTarget = (MenuTarget)menu;

        for (int i = 0; i < directions.length; i++)
        {
            activatedDirections[i] = menuTarget.activatedDirections[i];
            useRangeForDirections[i] = menuTarget.useRangeForDirections[i];
            copyAdvancedSetting(menu, i);
        }
    }


    @Override
    public void refreshData(ContainerManager container, Menu newData)
    {
        MenuTarget newDataTarget = (MenuTarget)newData;

        for (int i = 0; i < directions.length; i++)
        {
            if (activatedDirections[i] != newDataTarget.activatedDirections[i])
            {
                activatedDirections[i] = newDataTarget.activatedDirections[i];

                writeUpdatedData(container, i, DataTypeHeader.ACTIVATE, activatedDirections[i] ? 1 : 0);
            }

            if (useRangeForDirections[i] != newDataTarget.useRangeForDirections[i])
            {
                useRangeForDirections[i] = newDataTarget.useRangeForDirections[i];

                writeUpdatedData(container, i, DataTypeHeader.USE_ADVANCED_SETTING, useRangeForDirections[i] ? 1 : 0);
            }

            refreshAdvancedComponentData(container, newData, i);
        }
    }


    public void writeUpdatedData(ContainerManager container, int id, DataTypeHeader header, int data)
    {
        DataWriter dw = getWriterForClientComponentPacket(container);
        writeData(dw, id, header, data);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        int direction = dr.readData(DataBitHelper.MENU_TARGET_DIRECTION_ID);
        int headerId = dr.readData(DataBitHelper.MENU_TARGET_TYPE_HEADER);
        DataTypeHeader header = getHeaderFromId(headerId);

        switch (header)
        {
            case ACTIVATE:
                activatedDirections[direction] = dr.readData(header.bits) != 0;
                break;
            case USE_ADVANCED_SETTING:
                useRangeForDirections[direction] = dr.readData(header.bits) != 0;
                if (!useAdvancedSetting(direction))
                {
                    resetAdvancedSetting(direction);
                }
                break;
            default:
                readAdvancedNetworkComponent(dr, header, direction);
        }
    }


    public void writeData(DataTypeHeader header, int data)
    {
        DataWriter dw = getWriterForServerComponentPacket();
        writeData(dw, selectedDirectionId, header, data);
        PacketHandler.sendDataToServer(dw);
    }

    public void writeData(DataWriter dw, int id, DataTypeHeader header, int data)
    {
        dw.writeData(id, DataBitHelper.MENU_TARGET_DIRECTION_ID);
        dw.writeData(header.id, DataBitHelper.MENU_TARGET_TYPE_HEADER);
        dw.writeData(data, header.bits);
    }

    public enum DataTypeHeader
    {
        ACTIVATE(0, DataBitHelper.BOOLEAN),
        USE_ADVANCED_SETTING(1, DataBitHelper.BOOLEAN),
        START_OR_TANK_DATA(2, DataBitHelper.MENU_TARGET_RANGE),
        END(3, DataBitHelper.MENU_TARGET_RANGE);

        public int id;
        public DataBitHelper bits;

        DataTypeHeader(int header, DataBitHelper bits)
        {
            this.id = header;
            this.bits = bits;
        }

        public int getId()
        {
            return id;
        }

        public DataBitHelper getBits()
        {
            return bits;
        }
    }

    public DataTypeHeader getHeaderFromId(int id)
    {
        for (DataTypeHeader header : DataTypeHeader.values())
        {
            if (id == header.id)
            {
                return header;
            }
        }
        return null;
    }

    public static final String NBT_DIRECTIONS = "Directions";
    public static final String NBT_ACTIVE = "Active";
    public static final String NBT_RANGE = "UseRange";


    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, int version, boolean pickup)
    {
        NBTTagList directionTagList = nbtTagCompound.getTagList(NBT_DIRECTIONS, 10);

        for (int i = 0; i < directionTagList.tagCount(); i++)
        {
            NBTTagCompound directionTag = directionTagList.getCompoundTagAt(i);
            activatedDirections[i] = directionTag.getBoolean(NBT_ACTIVE);
            useRangeForDirections[i] = directionTag.getBoolean(NBT_RANGE);
            loadAdvancedComponent(directionTag, i);
        }
    }


    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        NBTTagList directionTagList = new NBTTagList();

        for (int i = 0; i < directions.length; i++)
        {
            NBTTagCompound directionTag = new NBTTagCompound();
            directionTag.setBoolean(NBT_ACTIVE, isActive(i));
            directionTag.setBoolean(NBT_RANGE, useAdvancedSetting(i));
            saveAdvancedComponent(directionTag, i);
            directionTagList.appendTag(directionTag);
        }

        nbtTagCompound.setTag(NBT_DIRECTIONS, directionTagList);
    }


    @Override
    public void addErrors(List<String> errors)
    {
        for (int i = 0; i < directions.length; i++)
        {
            if (isActive(i))
            {
                return;
            }
        }

        errors.add(Localization.NO_DIRECTION_ERROR.toString());
    }

    public void setActive(int side)
    {
        activatedDirections[side] = true;
    }
}
