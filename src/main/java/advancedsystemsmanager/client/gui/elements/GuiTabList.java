package advancedsystemsmanager.client.gui.elements;

import advancedsystemsmanager.api.gui.IGuiElement;
import advancedsystemsmanager.client.gui.GuiBase;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GuiTabList extends ArrayList<GuiTab> implements IGuiElement<GuiBase>
{
    private static int TAB_WIDTH_MAX = 80;
    private static int MAX_TABS = 8;
    private static int WIDTH_BORDER = 40;
    protected int tabHeight = 15, height = 15, width = 512;
    protected float tabWidth = TAB_WIDTH_MAX;
    protected int x, y;
    private int startIndex;
    private GuiTab selected;

    public GuiTabList()
    {
        x = 1; y = 100;
    }

    public GuiTab addNewTab()
    {
        GuiTab result = new GuiTab(this);
        selected = result;
        recalculateWidths();
        return result;
    }

    public int getStartIndex()
    {
        return startIndex;
    }

    public boolean isSelected(GuiTab tab)
    {
        return selected == tab;
    }

    public void setSelected(GuiTab tab)
    {
        selected = tab;
    }

    @Override
    public void draw(GuiBase guiBase, int mouseX, int mouseY, int zLevel)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        guiBase.setLineWidth(2);
        GuiTab tab;
        for (int i = size(); --i >= 0;)
        {
            tab = get(i);
            if (tab.isVisible())
            {
                if (isSelected(tab))
                {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(0, 0, 5);
                    tab.draw(guiBase, mouseX, mouseY, zLevel);
                    GL11.glPopMatrix();
                } else
                {
                    tab.draw(guiBase, mouseX, mouseY, zLevel);
                }
            }
        }
        GL11.glPopMatrix();
    }

    public void removeTab(GuiTab tab)
    {
        remove(tab);
        recalculateWidths();
    }

    private void recalculateWidths()
    {
        boolean update = false;
        float tabsWidth = Math.min(size(), MAX_TABS) * (this.tabWidth - tabHeight * GuiTab.SLOPE);
        if (tabsWidth > width - WIDTH_BORDER || tabsWidth < width - WIDTH_BORDER - tabWidth)
        {
            tabWidth = Math.min((width - WIDTH_BORDER) / Math.min(size(), MAX_TABS), TAB_WIDTH_MAX);
            update = true;
        }
        int newStart = Math.max(size() - MAX_TABS, 0);
        if (update || newStart != startIndex)
        {
            startIndex = newStart;
            for (GuiTab tab : this)
            {
                tab.setX();
            }
        }
    }

    @Override
    public void drawMouseOver(GuiBase guiBase, int mouseX, int mouseY)
    {

    }

    @Override
    public boolean onKeyStroke(GuiBase guiBase, char character, int key)
    {
        return false;
    }

    @Override
    public boolean onClick(int mouseX, int mouseY, int button)
    {
        mouseX -= x;
        mouseY -= y;
        for (GuiTab tab : this)
        {
            if (tab.onClick(mouseX, mouseY, button))
            {
                setSelected(tab);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisible()
    {
        return true;
    }
}
