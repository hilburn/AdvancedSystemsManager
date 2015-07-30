package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.ThemeHandler;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import static advancedsystemsmanager.api.gui.IManagerButton.BUTTON_ICON_SIZE;
import static advancedsystemsmanager.api.gui.IManagerButton.BUTTON_SIZE;
import static advancedsystemsmanager.reference.Textures.BUTTONS;

public class ManagerButtonList extends ArrayList<IManagerButton> implements IGuiElement<GuiManager>
{
    private static final int BUTTON_SPACING = 0;
    private int x = 20, y = 20, maxHeight = 218;

    public ManagerButtonList()
    {
    }

    public ManagerButtonList(int x, int y, int maxHeight)
    {
        this.x = x;
        this.y = y;
        this.maxHeight = maxHeight - BUTTON_SIZE;
    }

    @Override
    public void draw(GuiManager guiManager, int mouseX, int mouseY, int zLevel)
    {
        GuiManager.bindTexture(BUTTONS);
        for (VisibleIterator itr = new VisibleIterator(); itr.hasNext(); )
        {
            IManagerButton button = itr.next();
            boolean selected = CollisionHelper.inBounds(itr.x, itr.y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY);
            guiManager.drawColouredTexture(itr.x, itr.y, TileEntityManager.BUTTON_SRC_X, TileEntityManager.BUTTON_SRC_Y, BUTTON_SIZE, BUTTON_SIZE, (selected ? ThemeHandler.theme.buttons.backgroundMouseover : ThemeHandler.theme.buttons.background).getColour());
            guiManager.drawColouredTexture(itr.x, itr.y, TileEntityManager.BUTTON_SRC_X + BUTTON_SIZE, TileEntityManager.BUTTON_SRC_Y, BUTTON_SIZE, BUTTON_SIZE, (selected ? ThemeHandler.theme.buttons.foregroundMouseover : ThemeHandler.theme.buttons.foreground).getColour());
            ResourceLocation location = button.getTexture();
            if (location == BUTTONS)
                guiManager.drawTexture(itr.x, itr.y, button.getX(), button.getY(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE);
            else
            {
                GuiManager.bindTexture(location);
                guiManager.drawTexture(itr.x, itr.y, button.getX(), button.getY(), BUTTON_ICON_SIZE, BUTTON_ICON_SIZE);
                GuiManager.bindTexture(BUTTONS);
            }
            itr.nextPosition();
        }
    }

    @Override
    public void drawMouseOver(GuiManager guiManager, int mouseX, int mouseY)
    {
        for (VisibleIterator itr = new VisibleIterator(); itr.hasNext(); )
        {
            IManagerButton button = itr.next();
            if (CollisionHelper.inBounds(itr.x, itr.y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY))
            {
                guiManager.drawMouseOver(button.getMouseOver(), mouseX, mouseY);
                break;
            }
            itr.nextPosition();
        }
    }

    @Override
    public boolean onKeyStroke(GuiManager guiManager, char character, int key)
    {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button)
    {
        return onClick(mouseX, mouseY, button, false);
    }

    public boolean onClick(int mouseX, int mouseY, int button, boolean release)
    {
        for (VisibleIterator itr = new VisibleIterator(); itr.hasNext(); )
        {
            IManagerButton managerButton = itr.next();
            if (CollisionHelper.inBounds(itr.x, itr.y, BUTTON_SIZE, BUTTON_SIZE, mouseX, mouseY) && managerButton.activateOnRelease() == release)
            {
                if (managerButton.validClick())
                {
                    managerButton.setClicked(button);
                    PacketHandler.sendButtonPacket(itr.index, managerButton);
                }
                return true;
            }
            itr.nextPosition();
        }
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public void setMaxHeight(int height)
    {
        this.maxHeight = height;
    }

    private class VisibleIterator implements Iterator<IManagerButton>
    {
        int x, y;
        int index;
        private ListIterator<IManagerButton> itr = ManagerButtonList.this.listIterator();

        private VisibleIterator()
        {
            this.x = ManagerButtonList.this.x;
            this.y = ManagerButtonList.this.y;
        }

        @Override
        public boolean hasNext()
        {
            while (itr.hasNext() && !get(itr.nextIndex()).isVisible()) itr.next();
            return itr.hasNext();
        }

        @Override
        public IManagerButton next()
        {
            index = itr.nextIndex();
            return itr.next();
        }

        @Override
        public void remove()
        {
            itr.remove();
        }

        public void nextPosition()
        {
            y += BUTTON_SIZE + BUTTON_SPACING;
            if (y > maxHeight)
            {
                y = ManagerButtonList.this.y;
                x += BUTTON_SIZE + BUTTON_SPACING;
            }
        }
    }
}
