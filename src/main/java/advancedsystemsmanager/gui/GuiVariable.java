package advancedsystemsmanager.gui;

import advancedsystemsmanager.flow.elements.ScrollVariable;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class GuiVariable implements IInterfaceRenderer
{
    private GuiColourSelector colourSelector;
    protected TileEntityManager manager;
    private ScrollVariable variables;

    public GuiVariable(TileEntityManager te)
    {
        this.manager = te;
        variables = new ScrollVariable(null, 230, 20, 5, 5)
        {
            @Override
            public TileEntityManager getManager()
            {
                return manager;
            }

            @Override
            public void onClick(Variable variable, int mX, int mY, int button)
            {
                colourSelector.setRGB(variable.colour);
            }

            @Override
            public void sendUpdate()
            {
            }
        };
        colourSelector = new GuiColourSelector(20, 20)
        {
            @Override
            protected void drawColourOutput(GuiBase guiBase)
            {
                guiBase.drawColouredTexture(x + OUTPUT_X, y, Variable.VARIABLE_SRC_X, Variable.VARIABLE_SRC_Y, Variable.VARIABLE_SIZE, Variable.VARIABLE_SIZE, 30F/Variable.VARIABLE_SIZE, colourSelector.getColour());
            }
        };
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        colourSelector.draw(gui, mX, mY, 1);
        variables.draw(gui, mX, mY);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        colourSelector.drawMouseOver(gui, mX, mY);
        variables.drawMouseOver(gui, mX, mY);
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button)
    {
        colourSelector.onClick(mX, mY, button);
        variables.onClick(mX, mY, button);
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
    public boolean onKeyTyped(GuiManager gui, char c, int k)
    {
        return colourSelector.onKeyStroke(gui, c, k) || variables.onKeyStroke(gui, c, k);
    }

    @Override
    public void onScroll(int scroll)
    {
        colourSelector.onScroll(scroll);
        variables.doScroll(scroll);
    }
}
