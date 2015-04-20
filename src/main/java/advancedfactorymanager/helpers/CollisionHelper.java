package advancedfactorymanager.helpers;

public class CollisionHelper
{
    public static boolean disableInBoundsCheck;

    public static boolean inBounds(int leftX, int topY, int width, int height, int mX, int mY)
    {
        if (disableInBoundsCheck)
        {
            return false;
        }
        return !(leftX > mX || mX > leftX + width || topY > mY || mY > topY + height);
    }
}
