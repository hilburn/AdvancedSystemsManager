package advancedsystemsmanager.client.gui.elements;

import advancedsystemsmanager.api.gui.IDraggable;
import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.util.MouseCursor;
import org.lwjgl.opengl.GL11;

public class GuiMenuScreen implements IGuiElement, IDraggable
{
    private int x, y, w, h, top_border = 20, border = 2;
    private GuiTabList tabs;
    static int[] BORDER_COLOUR = new int[]{0x1d, 0x1d, 0x1d};
    static int[] BACKGROUND_COLOUR = new int[]{0x30, 0x30, 0x30};
    private byte direction, mouseover;
    private int dragX, dragY;

    public GuiMenuScreen(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.tabs = new GuiTabList(border, top_border - border, getTabWidth());
        this.tabs.addNewTab("Settings");
        this.tabs.addNewTab("Comments");
    }

    private int getTabWidth()
    {
        return w - 5 * border;
    }

    @Override
    public void draw(GuiBase gui, int mouseX, int mouseY, int zLevel)
    {
        gui.translate(x, y);

        gui.drawRectangle(0, 0, w, h, BORDER_COLOUR);
        tabs.draw(gui, mouseX, mouseY, zLevel);

        gui.drawRectangle(border, top_border, w-border, h-border, BACKGROUND_COLOUR);
        gui.setBounds(border, top_border, w - 2 * border, h - top_border - border);
        gui.drawRectangle(-1000, h / 2 - 5, 1000, h / 2 + 5, new int[]{255, 255, 255});
        gui.setBounds(border + 50, top_border, w - 2 * border, h);
        gui.drawRectangle(-1000, h / 2 - 50, 1000, h / 2 - 20, new int[]{0, 255, 255});
        gui.endBounds();
        gui.drawRectangle(-1000, h/2+20, 1000, h/2+50, new int[]{255,0,255});
        gui.endBounds();

        gui.untranslate(x, y);
    }

    @Override
    public boolean drawMouseOver(GuiBase gui, int mouseX, int mouseY)
    {
        mouseover = 0;
        mouseX -= x;
        mouseY -= y;
        if (CollisionHelper.inBounds(0, 0, w, h, mouseX, mouseY))
        {
            if (tabs.drawMouseOver(gui, mouseX, mouseY))
            {
                setCursor();
                return false;
            }
            if (CollisionHelper.inBounds(0, 0, border, h, mouseX, mouseY))
            {
                mouseover |= 2;
            } else if (CollisionHelper.inBounds(w - border, 0, border, h, mouseX, mouseY))
            {
                mouseover |= 8;
            }
            if (CollisionHelper.inBounds(0, h - border, w, border, mouseX, mouseY))
            {
                mouseover |= 4;
            }
            if (CollisionHelper.inBounds(0, 0, w, top_border, mouseX, mouseY))
            {
                mouseover = 1;
            }
        }
        setCursor();
        return false;
    }

    @Override
    public boolean onKeyStroke(GuiBase gui, char character, int key)
    {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button)
    {
        mouseX -= x;
        mouseY -= y;
        if (tabs.onClick(mouseX, mouseY, button))
        {
            return true;
        }
        direction = mouseover;
        if (direction > 0)
        {
            dragX = mouseX + x;
            dragY = mouseY + y;
            return true;
        }
        return false;
    }

    private void setCursor()
    {
        switch (mouseover | direction)
        {
            case 2:
            case 8:
                MouseCursor.LEFT_RIGHT.setCursor();
                return;
            case 4:
                MouseCursor.UP_DOWN.setCursor();
                return;
            case 6:
                MouseCursor.UP_LEFT.setCursor();
                return;
            case 12:
                MouseCursor.UP_RIGHT.setCursor();
                return;
            case 1:
                MouseCursor.MOVE.setCursor();
                return;
            case 0:
            default:
                MouseCursor.resetMouseCursor();
        }
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }

    @Override
    public void drag(int mouseX, int mouseY)
    {
        if (direction > 0)
        {
            int dX = mouseX - dragX;
            int dY = mouseY - dragY;
            dragX = mouseX;
            dragY = mouseY;
            if ((direction & 1) > 0)
            {
                x += dX;
                y += dY;
            } else
            {
                if ((direction & 2) > 0)
                {
                    x += dX;
                    w -= dX;
                    tabs.setWidth(getTabWidth());
                }
                if ((direction & 4) > 0)
                {
                    h += dY;
                }
                if ((direction & 8) > 0)
                {
                    w += dX;
                    tabs.setWidth(getTabWidth());
                }
            }
        }
    }

    @Override
    public void release(int mouseX, int mouseY)
    {
        if (direction > 0)
        {
            MouseCursor.resetMouseCursor();
            direction = 0;
        }
    }
}
