package advancedsystemsmanager.gui;

import advancedsystemsmanager.pong.Ball;
import advancedsystemsmanager.pong.Beam;
import advancedsystemsmanager.pong.BeamAI;
import org.lwjgl.input.Keyboard;

public class GuiPong implements IInterfaceRenderer
{
    public GuiPong(GuiManager gui)
    {
        height = gui.height;
        width = gui.width;
        resetGame();
        Keyboard.enableRepeatEvents(true);
    }

    private Ball ball;
    private Beam player;
    private BeamAI ai;
    private int move = 5;
    private int height, width;

    private void resetGame()
    {
        int rectHeight = 50;
        ball = new Ball(width/2, height/2, 5, 0, height);
        player = new Beam(0, height/2, 5, rectHeight, 0, height);
        ai = new BeamAI(width, height/2, 5, rectHeight, 0, height);
        ball.spawn();
        player.spawn();
        ai.spawn();
    }

    private void update()
    {
        if (ball.outOfBounds(0, width)) resetGame();
        ai.update(ball, move);
        ball.update(player, ai);
    }

    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        update();
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
        if (k == 200) player.up(move);
        if (k == 208) player.down(move);
        return false;
    }

    @Override
    public void onScroll(int scroll)
    {

    }
}
