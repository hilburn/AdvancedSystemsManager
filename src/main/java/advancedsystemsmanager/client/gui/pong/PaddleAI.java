package advancedsystemsmanager.client.gui.pong;

import java.util.Random;

public class PaddleAI extends Paddle
{
    private Random r = new Random();

    public PaddleAI(int x, int y, int w, int h, int top, int bottom)
    {
        super(x - w, y, w, h, top, bottom);
    }

    public void update(Ball ball, int move)
    {
        double random = r.nextGaussian();
        if (random > 0.8 || random < -0.8) return;
        if (this.y + this.h / 2 + move < ball.y) this.down(move);
        else if (this.y + this.h / 2 - move > ball.y) this.up(move);
    }
}
