package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.client.gui.elements.GuiMenuScreen;

public class GuiTestBed implements IInterfaceRenderer
{
    GuiMenuScreen testElement;

    public GuiTestBed()
    {
        testElement = new GuiMenuScreen(0, 0, 200, 300);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        testElement.draw(gui, mX, mY, 1);
    }

    @Override
    public boolean drawMouseOver(GuiManager gui, int mX, int mY)
    {
        testElement.drawMouseOver(gui, mX, mY);
        return false;
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button)
    {
        testElement.onClick(mX, mY, button);
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY)
    {
        testElement.drag(mX, mY);
    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY, int button)
    {
        testElement.release(mX, mY);
    }

    @Override
    public boolean onKeyTyped(GuiManager gui, char c, int k)
    {
//        tabs.addNewTab();
        return false;
    }

    @Override
    public void onScroll(int scroll)
    {

    }
}
