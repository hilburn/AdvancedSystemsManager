package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.api.network.IPacketProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.UpdateElement;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ThemeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class MenuTarget extends Menu
{

    public static final int DIRECTION_SIZE_W = 31;
    public static final int DIRECTION_SIZE_H = 12;
    public static final int DIRECTION_SRC_X = 71;
    public static final int DIRECTION_SRC_Y = 234;
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
    public static final String NBT_DIRECTIONS = "Directions";
    public static final String NBT_ACTIVE = "Active";
    public static final String NBT_ADVANCED = "Advanced";
    public static ForgeDirection[] directions = ForgeDirection.VALID_DIRECTIONS;
    public int selectedDirectionId;
    public Button[] buttons;
    public boolean[] activatedDirections = new boolean[directions.length];
    public boolean[] advancedDirections = new boolean[directions.length];


    public MenuTarget(FlowComponent parent)
    {
        super(parent);
        buttons = new Button[]{new Button(parent, 5)
        {
            @Override
            public String getLabel()
            {
                return isActive(selectedDirectionId) ? Names.DEACTIVATE : Names.ACTIVATE;
            }

            @Override
            public boolean writeData(ASMPacket packet)
            {
                packet.writeByte(selectedDirectionId << 1 | (isActive(selectedDirectionId) ? 0 : 1));
                activatedDirections[selectedDirectionId] = !activatedDirections[selectedDirectionId];
                return true;
            }

            @Override
            public boolean readData(ASMPacket packet)
            {
                int data = packet.readByte();
                activatedDirections[data >> 1] = (data & 1) == 1;
                return false;
            }
        },
                getSecondButton()};
        selectedDirectionId = -1;

    }

    public boolean isActive(int i)
    {
        return activatedDirections[i];
    }

    public abstract Button getSecondButton();

    @Override
    public String getName()
    {
        return Names.TARGET_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        for (int i = 0; i < directions.length; i++)
        {
            int x = getDirectionX(i);
            int y = getDirectionY(i);

            gui.drawRectangle(x, y, x + DIRECTION_SIZE_W, y + DIRECTION_SIZE_H, ThemeHandler.theme.menus.checkboxes.getColour(isActive(i), selectedDirectionId == i));

            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            int color = selectedDirectionId != -1 && selectedDirectionId != i ? 0x70404040 : 0x404040;
            gui.drawString(LocalizationHelper.getDirectionString(i), x + DIRECTION_TEXT_X, y + DIRECTION_TEXT_Y, color);
            GL11.glPopMatrix();
        }

        if (selectedDirectionId != -1)
        {
            for (Button button : buttons)
            {

                gui.drawRectangle(BUTTON_X, button.y, BUTTON_X + BUTTON_SIZE_W, button.y + BUTTON_SIZE_H, ThemeHandler.theme.menus.checkboxes.getColour(false, CollisionHelper.inBounds(BUTTON_X, button.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY)));
                gui.drawCenteredString(button.getLabel(), BUTTON_X, button.y + BUTTON_TEXT_Y, 0.5F, BUTTON_SIZE_W, 0x404040);
            }

            if (useAdvancedSetting(selectedDirectionId))
            {
                drawAdvancedComponent(gui, mX, mY);
            }
        }
    }

    public int getDirectionX(int i)
    {
        return i % 2 == 0 ? DIRECTION_X_LEFT : DIRECTION_X_RIGHT;
    }

    public int getDirectionY(int i)
    {
        return DIRECTION_Y + (DIRECTION_SIZE_H + DIRECTION_MARGIN) * (i / 2);
    }

    public boolean useAdvancedSetting(int i)
    {
        return advancedDirections[i];
    }

    @SideOnly(Side.CLIENT)
    public abstract void drawAdvancedComponent(GuiManager gui, int mX, int mY);

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
                    return;
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
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

                return;
            }
        }

        if (selectedDirectionId != -1)
        {
            for (Button optionButton : buttons)
            {
                if (CollisionHelper.inBounds(BUTTON_X, optionButton.y, BUTTON_SIZE_W, BUTTON_SIZE_H, mX, mY))
                {
                    optionButton.onUpdate();
                    return;
                }
            }

            if (useAdvancedSetting(selectedDirectionId))
            {
                onAdvancedClick(mX, mY, button);
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
            advancedDirections[i] = menuTarget.advancedDirections[i];
            copyAdvancedSetting(menu, i);
        }
    }

    public abstract void copyAdvancedSetting(Menu menuTarget, int i);

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        NBTTagList directionTagList = nbtTagCompound.getTagList(NBT_DIRECTIONS, 10);

        for (int i = 0; i < directionTagList.tagCount(); i++)
        {
            NBTTagCompound directionTag = directionTagList.getCompoundTagAt(i);
            activatedDirections[i] = directionTag.getBoolean(NBT_ACTIVE);
            advancedDirections[i] = directionTag.getBoolean(NBT_ADVANCED);
            loadAdvancedComponent(directionTag, i);
        }
    }

    public abstract void loadAdvancedComponent(NBTTagCompound directionTag, int i);

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound, boolean pickup)
    {
        NBTTagList directionTagList = new NBTTagList();

        for (int i = 0; i < directions.length; i++)
        {
            NBTTagCompound directionTag = new NBTTagCompound();
            directionTag.setBoolean(NBT_ACTIVE, isActive(i));
            directionTag.setBoolean(NBT_ADVANCED, useAdvancedSetting(i));
            saveAdvancedComponent(directionTag, i);
            directionTagList.appendTag(directionTag);
        }

        nbtTagCompound.setTag(NBT_DIRECTIONS, directionTagList);
    }

    public abstract void saveAdvancedComponent(NBTTagCompound directionTag, int i);

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

        errors.add(Names.NO_DIRECTION_ERROR);
    }

    public abstract void refreshAdvancedComponent();

    public abstract void onAdvancedClick(int mX, int mY, int button);

    public void setActive(int side)
    {
        activatedDirections[side] = true;
    }

    public abstract class Button extends UpdateElement
    {
        public int y;

        public Button(IPacketProvider provider, int y)
        {
            super(provider);
            this.y = y;
        }

        public String getMouseOverText()
        {
            return getLabel() + "Long";
        }

        public abstract String getLabel();
    }
}
