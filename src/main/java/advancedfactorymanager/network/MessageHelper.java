package advancedfactorymanager.network;

public class MessageHelper
{
    public static byte booleanToByte(boolean[] array)
    {
        byte val = 0;
        for (boolean b : array)
        {
            val <<= 1;
            if (b) val |= 1;
        }
        return val;
    }

    public static boolean[] byteToBooleanArray(byte byteVal)
    {
        boolean[] result = new boolean[6];
        for (int i = 5; i > 0; i--)
        {
            result[i] = (byteVal & 0x01) != 0;
            byteVal >>= 1;
        }
        return result;
    }

}
