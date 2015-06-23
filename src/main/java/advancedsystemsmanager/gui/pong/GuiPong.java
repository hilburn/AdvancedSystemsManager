package advancedsystemsmanager.gui.pong;

import advancedsystemsmanager.gui.GuiColourSelector;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.gui.IInterfaceRenderer;

public class GuiPong implements IInterfaceRenderer
{
    private Ball ball;
    private Paddle player;
    private PaddleAI ai;
    private int move = 5;
    private int height, width, x, y;

    public GuiPong()
    {
        x = 16;
        y = 16;
        height = GuiManager.GUI_HEIGHT - 2 * y;
        width = GuiManager.GUI_WIDTH - 2 * x;
        int yBorder = 5;
        int rectHeight = 50;
        ball = new Ball(width/2 - 2, height/2 - 2, 5, y + yBorder, y + height - yBorder);
        player = new Paddle(x * 2, (height - rectHeight) /2, 5, rectHeight, y + yBorder, y + height - yBorder);
        ai = new PaddleAI(width, height/2, 5, rectHeight, y + yBorder, y + height - yBorder);
        resetGame();
    }

    private void resetGame()
    {
        ball.reset();
        player.reset();
        ai.reset();
    }

    private void update()
    {
        if (ball.checkScore(x + 5, player, width + x - 5, ai)) resetGame();
        ai.update(ball, move);
        player.update(move);
        ball.update(player, ai);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        update();
        int border = 5;
        gui.drawRectangle(x, y, x + width, y + height, GuiColourSelector.WHITE);
        gui.drawRectangle(x + border, y + border, x + width - border, y + height - border, GuiColourSelector.BLACK);
        int y = this.y + border;
        int x = this.x + width/2 - 2;
        while (y < this.y + height)
        {
            gui.drawRectangle(x, y, x + 4, y + 8, GuiColourSelector.WHITE);
            y+=16;
        }
        player.draw(gui);
        ball.draw(gui);
        ai.draw(gui);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {

    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button)
    {

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
        return false;
    }

    @Override
    public void onScroll(int scroll)
    {

    }
}
