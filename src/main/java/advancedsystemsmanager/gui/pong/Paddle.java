package advancedsystemsmanager.gui.pong;

import advancedsystemsmanager.gui.GuiColourSelector;
import advancedsystemsmanager.gui.GuiManager;
import org.lwjgl.input.Keyboard;

public class Paddle
{
    int sY, x, y, w, h;
    int top, bottom;
    int direction;

    public Paddle(int x, int y, int w, int h, int top, int bottom)
    {
        this.x = x;
        this.sY = y;
        this.w = w;
        this.h = h;
        this.top = top;
        this.bottom = bottom;
    }

    public void spawn()
    {
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
        gui.drawRectangle(x, y, x+w, y+h, GuiColourSelector.WHITE);
    }

    public boolean checkLeft(int x, int y, int w, int h)
    {
        return x <= this.x + this.w && y + h >= this.y && y <= this.y + this.h;
    }

    public boolean checkRight(int x, int y, int w, int h)
    {
        return x + w >= this.x && y + h >= this.y && y <= this.y + this.h;
    }

    public double angleChange(Ball ball, int maxAngle)
    {
        return (((this.y + this.h/2) - ball.y)/(this.h/2))*Math.toRadians(maxAngle);
    }

    public void update(int move)
    {
        if (Keyboard.getEventKey() == Keyboard.KEY_UP)
        {
            if (Keyboard.getEventKeyState())
            {
                direction |= 1;
            } else
            {
                direction &= 2;
            }
        } else if (Keyboard.getEventKey() == Keyboard.KEY_DOWN)
        {
            if (Keyboard.getEventKeyState())
            {
                direction |= 2;
            } else
            {
                direction &= 1;
            }
        }
        if (direction == 1)
        {
            up(move);
        } else if (direction == 2)
        {
            down(move);
        }
    }
}
