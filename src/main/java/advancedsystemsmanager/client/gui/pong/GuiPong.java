package advancedsystemsmanager.client.gui.pong;

import advancedsystemsmanager.client.gui.GuiColourSelector;
import advancedsystemsmanager.client.gui.GuiManager;
import advancedsystemsmanager.client.gui.IInterfaceRenderer;
import advancedsystemsmanager.client.gui.fonts.FontRenderer;

import java.awt.*;

public class GuiPong implements IInterfaceRenderer
{
    private Ball ball;
    private Paddle player;
    private PaddleAI ai;
    private int move = 5;
    private int winCondition = 9;
    private int height, width, x, y;
    private FontRenderer fontRenderer;
    private boolean ended;

    public GuiPong()
    {
        ended = false;
        fontRenderer = new FontRenderer(new Font(Font.SANS_SERIF, Font.PLAIN, 32), false); // TODO proper pong font
        x = 16;
        y = 16;
        height = GuiManager.GUI_HEIGHT - 2 * y;
        width = GuiManager.GUI_WIDTH - 2 * x;
        int yBorder = 5;
        int rectHeight = 50;
        ball = new Ball(width / 2 - 2, height / 2 - 2, 5, y + yBorder, y + height - yBorder);
        player = new Paddle(x * 2, (height - rectHeight) / 2, 5, rectHeight, y + yBorder, y + height - yBorder);
        ai = new PaddleAI(width, height / 2, 5, rectHeight, y + yBorder, y + height - yBorder);
        resetGame();
    }

    private void resetGame()
    {
        if (player.score > winCondition || ai.score > winCondition)
        {
            finish();
            return;
        }
        ball.reset();
        player.reset();
        ai.reset();
    }

    private void finish()
    {
        ended = true;
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        if (!ended)
        {
            update();
            int border = 5;
            gui.drawRectangle(x, y, x + width, y + height, GuiColourSelector.WHITE);
            gui.drawRectangle(x + border, y + border, x + width - border, y + height - border, GuiColourSelector.BLACK);
            int y = this.y + border;
            int x = this.x + width / 2 - 2;
            while (y < this.y + height)
            {
                gui.drawRectangle(x, y, x + 4, y + 8, GuiColourSelector.WHITE);
                y += 16;
            }
            ball.draw(gui);
            player.draw(gui);
            fontRenderer.drawString(this.x - 40 + width / 2, this.y, player.getScore(), 0xFFFFFF);
            ai.draw(gui);
            fontRenderer.drawString(this.x + 20 + width / 2, this.y, ai.getScore(), 0xFFFFFF);
        } else
        {
            fontRenderer.drawString(this.x + width / 4, this.y, "Thx for playing", 0xFFFFFF);
            fontRenderer.drawScaledString(this.x + width / 2 - 40, this.y + 40, (player.score > winCondition ? "Player" : "AI") + " wins", 0xFFFFFF, 26);
        }
    }

    private void update()
    {
        if (ball.checkScore(x + 5, player, width + x - 5, ai)) resetGame();
        ai.update(ball, move);
        player.update(move);
        ball.update(player, ai);
    }

    @Override
    public boolean drawMouseOver(GuiManager gui, int mX, int mY)
    {
        return false;
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
