package advancedsystemsmanager.helpers;

import advancedsystemsmanager.api.tileentities.ICable;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

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

    public static void getAdjacentCables(SystemCoord coordinate, List<SystemCoord> visited, Queue<SystemCoord> cables)
    {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            SystemCoord target = new SystemCoord(coordinate, direction);
            if (!visited.contains(target))
            {
                visited.add(target);
                if (isCable(target.getBlock(), target.getWorld(), target.getX(), target.getY(), target.getZ()))
                {
                    cables.add(target);
                }
            }
        }
    }

    public static void updateInventories(SystemCoord start)
    {
        List<SystemCoord> visited = new ArrayList<SystemCoord>();
        List<TileEntityManager> managers = new ArrayList<TileEntityManager>();
        Queue<SystemCoord> queue = new PriorityQueue<SystemCoord>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty())
        {
            SystemCoord element = queue.poll();

            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                SystemCoord target = new SystemCoord(element, direction);

                if (target.isValid() && !visited.contains(target))
                {
                    visited.add(target);
                    //if (element.getDepth() < TileEntityManager.MAX_CABLE_LENGTH){
                    Block block = target.getBlock();
                    if (block == ClusterRegistry.MANAGER.getBlock())
                    {
                        TileEntity tileEntity = target.getWorldTE();
                        if (tileEntity != null && tileEntity instanceof TileEntityManager)
                        {
                            managers.add((TileEntityManager)tileEntity);
                        }
                    } else if (isCable(block, target.getWorld(), target.getX(), target.getY(), target.getZ()) /*&& target.getDepth() < TileEntityManager.MAX_CABLE_LENGTH*/)
                    {
                        queue.add(target);
                        ((ICable)block).getConnectedCables(target.getWorld(), target, visited, queue);
                    }
                    //}
                }
            }
        }
        for (TileEntityManager manager : managers)
        {
            manager.updateInventories();
        }
    }

    public static boolean isCable(Block block, World world, int x, int y, int z)
    {
        return block instanceof ICable && ((ICable)block).isCable(world, x, y, z);
    }
}
