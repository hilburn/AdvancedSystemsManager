package advancedsystemsmanager.pong;

import java.util.Random;

public class BeamAI extends Beam
{
    private Random r = new Random();

    public BeamAI(int x, int y, int w, int h, int top, int bottom)
    {
        super(x, y, w, h, top, bottom);
    }

    public void update(Ball ball, int move)
    {
        double random = r.nextGaussian();
        if (random > 0.8 || random < -0.8) return;
        if (this.y + this.h/2 < ball.y) this.down(move);
        if (this.y + this.h/2 > ball.y) this.up(move);
    }
}
