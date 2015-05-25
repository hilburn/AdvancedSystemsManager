package advancedsystemsmanager.flow;


public class Point
{
    public int x, y;

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Point()
    {
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public Point copy()
    {
        return new Point(x, y);
    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || !(o instanceof Point)) return false;

        Point point = (Point)o;

        return x == point.x && y == point.y;
    }

    public void adjustToGrid(int grid)
    {
        x = Math.round((x - 2) / grid) * grid + 2;
        y = Math.round((y - 4) / grid) * grid + 4;
    }
}
