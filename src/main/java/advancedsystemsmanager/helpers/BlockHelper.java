package advancedsystemsmanager.helpers;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockHelper
{
    public static int getTwoAxisDirection(EntityLivingBase entity)
    {
        switch (MathHelper.floor_double((double) ((entity.rotationYaw * 4F) / 360F) + 2.5D) & 3)
        {
            case 0:
                return 3;
            case 1:
                return 4;
            case 2:
                return 2;
            default:
                return 5;
        }
    }

    public static int getThreeAxisDirection(EntityLivingBase entity)
    {
        if (entity.rotationPitch > 60.0F)
        {
            return 1;
        } else if (entity.rotationPitch < -60.0F)
        {
            return 0;
        }
        return getTwoAxisDirection(entity);
    }

    public static int getReverseDirection(int dir)
    {
        return ForgeDirection.OPPOSITES[dir];
    }
}
