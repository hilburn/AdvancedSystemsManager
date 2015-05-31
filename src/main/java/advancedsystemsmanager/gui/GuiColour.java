package advancedsystemsmanager.gui;

import net.minecraft.util.ResourceLocation;

public class GuiColour implements IInterfaceRenderer
{

    private GuiColourSelector colourSelector;

    public GuiColour()
    {
        colourSelector = new GuiColourSelector(100, 100);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        colourSelector.draw(gui, mX, mY, 1);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        colourSelector.drawMouseOver(gui, mX, mY);
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button)
    {
        colourSelector.onClick(mX, mY, button);
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY)
    {
        colourSelector.drag(mX, mY);
    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY)
    {
        colourSelector.release(mX, mY);
    }

    @Override
    public void onKeyTyped(GuiManager gui, char c, int k)
    {

    }

    @Override
    public void onScroll(int scroll)
    {
        colourSelector.onScroll(scroll);
    }
}
