package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.client.gui.elements.GuiTabList;

public class GuiTestBed implements IInterfaceRenderer
{
    GuiTabList tabs;

    public GuiTestBed()
    {
        tabs = new GuiTabList();
        tabs.addNewTab();
        tabs.addNewTab();
        tabs.addNewTab();
        tabs.addNewTab();
        tabs.addNewTab();
        tabs.addNewTab();
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        tabs.draw(gui, mX, mY, 1);
    }

    @Override
    public boolean drawMouseOver(GuiManager gui, int mX, int mY)
    {
        return false;
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button)
    {
        tabs.onClick(mX, mY, button);
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY)
    {

    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY, int button)
    {

    }

    @Override
    public boolean onKeyTyped(GuiManager gui, char c, int k)
    {
        tabs.addNewTab();
        return false;
    }

    @Override
    public void onScroll(int scroll)
    {

    }
}
