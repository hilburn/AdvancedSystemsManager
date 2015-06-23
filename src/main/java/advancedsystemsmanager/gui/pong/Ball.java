package advancedsystemsmanager.gui.pong;

import advancedsystemsmanager.gui.GuiColourSelector;
import advancedsystemsmanager.gui.GuiManager;

public class Ball
{
    int sX, sY, v, x, y, vX, vY, top, bottom;
    int h = 10;
    int w = 10;
    double angle;

    public Ball(int x, int y, int v, int top, int bottom)
    {
        this.sX = x;
        this.sY = y;
        this.v = v;
        this.top = top;
        this.bottom = bottom-h;
    }

    public void draw(GuiManager gui)
    {
        gui.drawRectangle(x, y, x+w, y+h, GuiColourSelector.WHITE);
    }

    public void reset()
    {
        this.angle = Math.random() * 45;
        this.angle = Math.toRadians(angle + 250);
        this.x = this.sX;
        this.y = this.sY;
        this.vX = (int)Math.round(this.v * Math.sin(this.angle));
        this.vY = (int)Math.round(this.v * Math.cos(this.angle));
    }

    public void update(Paddle left, Paddle right)
    {
        this.x += this.vX;
        this.y += this.vY;
        if (this.y < top)
        {
            this.y = top;
            this.vY = -this.vY;
        }
        if (this.y > bottom)
        {
            this.y = bottom;
            this.vY = -this.vY;
        }
        if (left.checkLeft(this.x, this.y, this.w, this.h))
        {
            this.x = left.x + left.w;
            this.vX = -this.vX;
            this.vY += Math.cos(left.angleChange(this, 75));
        }
        if (right.checkRight(this.x, this.y, this.w, this.h))
        {
            this.x = right.x - w;
            this.vX = -this.vX;
            this.vY += Math.cos(right.angleChange(this, 75));
        }
    }

    public boolean checkScore(int left, Paddle leftPaddle, int right, Paddle rightPaddle)
    {
        if (this.x > right)
        {
            leftPaddle.score();
        } else if (this.x < left)
        {
            rightPaddle.score();
        }
        return false;
    }
}
