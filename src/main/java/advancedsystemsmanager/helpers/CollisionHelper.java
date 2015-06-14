package advancedsystemsmanager.helpers;

public class CollisionHelper
{
    private static boolean disableInBoundsCheck;

    public static boolean inBounds(int leftX, int topY, int width, int height, int mX, int mY)
    {
        return !disableInBoundsCheck && !(leftX > mX || mX > leftX + width || topY > mY || mY > topY + height);
    }

    public static void enable()
    {
        disableInBoundsCheck = false;
    }

    public static void disable()
    {
        disableInBoundsCheck = true;
    }
}
