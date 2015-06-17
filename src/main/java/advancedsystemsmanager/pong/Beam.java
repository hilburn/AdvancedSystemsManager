package advancedsystemsmanager.pong;

import advancedsystemsmanager.gui.GuiManager;

public class Beam
{
    int sX, sY, x, y, w, h;
    int top, bottom;

    public Beam(int x, int y, int w, int h, int top, int bottom)
    {
        this.sX = x;
        this.sY = y;
        this.w = w;
        this.h = h;
        this.top = top;
        this.bottom = bottom;
    }

    public void spawn()
    {
        this.x = sX;
        this.y = sY;
    }

    public void up(int amount)
    {
        this.y -= amount;
        if (this.y < this.top) this.y = this.top;
    }

    public void down(int amount)
    {
        this.y += amount;
        if (this.y + this.h > this.bottom) this.y = this.bottom - this.h;
    }

    public void draw(GuiManager gui)
    {
        gui.drawRectangle(x, y, x+w, y+h, new int[]{255, 255, 255});
    }

    public boolean impactR(int x, int y)
    {
        return x < this.x+ this.w && y > this.y && y < this.y + this.h;
    }

    public boolean impactL(int x, int y)
    {
        return x > this.x - this.w && y > this.y && y < this.y + this.h;
    }

    public double angleChange(Ball ball, int maxAngle)
    {
        return (((this.y + this.h/2) - ball.y)/(this.h/2))*Math.toRadians(maxAngle);
    }
}
