package advancedsystemsmanager.client.gui;

import advancedsystemsmanager.flow.elements.ScrollVariable;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

public class GuiVariable implements IInterfaceRenderer
{
    private static final int BUTTON_WIDTH = 30;
    private static final int BUTTON_HEIGHT = 18;
    private static int X = 110;
    private static int Y = 64;
    private static int BUTTON_X = X + GuiColourSelector.OUTPUT_X;
    private static int BUTTON_Y = Y + 110;
    protected TileEntityManager manager;
    private GuiColourSelector colourSelector;
    private ScrollVariable variables;

    public GuiVariable(TileEntityManager te)
    {
        this.manager = te;
//        test = new GuiColourSelectorHex();
        variables = new ScrollVariable(null, X + 200, Y, 5, 5)
        {
            @Override
            public TileEntityManager getManager()
            {
                return manager;
            }

            @Override
            public void onClick(Variable variable, int mX, int mY, int button)
            {
                super.onClick(variable, mX, mY, button);
                colourSelector.setRGB(variable.colour);
            }

            @Override
            public void sendUpdate()
            {
            }

            @Override
            public void onRelease(int mX, int mY)
            {
                if (clicked && canScroll)
                {
                    moveOffset(dir * ITEM_SIZE_WITH_MARGIN);
                }
                super.onRelease(mX, mY);
            }
        };
        colourSelector = new GuiColourSelector(X, Y)
        {
            @Override
            protected void drawColourOutput(GuiBase guiBase)
            {
                guiBase.drawSplitColouredTexture(x + OUTPUT_X, y, 30, 30, Variable.VARIABLE_SRC_X, Variable.VARIABLE_SRC_Y, Variable.VARIABLE_SIZE, Variable.VARIABLE_SIZE, colourSelector.getColour(), displaySplitColour() ? new int[]{variables.variable >> 16, variables.variable >> 8 & 0xFF, variables.variable & 0xFF} : null);
            }

            @Override
            public boolean displaySplitColour()
            {
                return variables.variable != -1;
            }
        };
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
//        test.draw(gui, mX, mY, 1);
        if (manager.variableUpdate)
        {
            variables.updateSearch();
            manager.variableUpdate = false;
        }
        colourSelector.draw(gui, mX, mY, 1);
        variables.draw(gui, mX, mY);
        gui.drawRectangle(BUTTON_X, BUTTON_Y, BUTTON_X + BUTTON_WIDTH, BUTTON_Y + BUTTON_HEIGHT, new int[]{0x99, 0x99, 0x99});
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
//        test.onClick(mX, mY, button);
        colourSelector.onClick(mX, mY, button);
        variables.onClick(mX, mY, button);
        if (CollisionHelper.inBounds(BUTTON_X, BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT, mX, mY))
        {
            int colour = colourSelector.getRGB();
            if (manager.getVariable(colour) == null)
            {
                manager.addNewVariable(new Variable(colour));
                variables.updateSearch();
                PacketHandler.sendVariablePacket(colour);
            }
        }
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY)
    {
        colourSelector.drag(mX, mY);
    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY, int button)
    {
        colourSelector.release(mX, mY);
        variables.onRelease(mX, mY);
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
