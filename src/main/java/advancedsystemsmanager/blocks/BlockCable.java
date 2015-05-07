package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ICable;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

public class BlockCable extends BlockBase implements ICable
{
    public BlockCable()
    {
        super(Names.CABLE, 0.4F);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        super.onNeighborBlockChange(world, x, y, z, block);

        updateInventories(world, x, y, z);
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z)
    {
        super.onBlockAdded(world, x, y, z);

        updateInventories(world, x, y, z);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        super.breakBlock(world, x, y, z, block, meta);

        updateInventories(world, x, y, z);
    }

    public void updateInventories(World world, int blockX, int blockY, int blockZ)
    {
        List<SystemCoord> visited = new ArrayList<SystemCoord>();
        List<TileEntityManager> managers = new ArrayList<TileEntityManager>();
        Queue<SystemCoord> queue = new PriorityQueue<SystemCoord>();
        SystemCoord start = new SystemCoord(blockX, blockY, blockZ, 0);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty())
        {
            SystemCoord element = queue.poll();

            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
            {
                SystemCoord target = new SystemCoord(element, direction);

                if (!visited.contains(target))
                {
                    visited.add(target);
                    //if (element.getDepth() < TileEntityManager.MAX_CABLE_LENGTH){
                    Block block = world.getBlock(target.x, target.y, target.z);
                    int meta = world.getBlockMetadata(target.x, target.y, target.z);
                    if (block == BlockRegistry.blockManager)
                    {
                        TileEntity tileEntity = world.getTileEntity(target.x, target.y, target.z);
                        if (tileEntity != null && tileEntity instanceof TileEntityManager)
                        {
                            managers.add((TileEntityManager)tileEntity);
                        }
                    } else if (isCable(block, meta) /*&& target.getDepth() < TileEntityManager.MAX_CABLE_LENGTH*/)
                    {
                        queue.add(target);
                        ((ICable)block).getConnectedCables(world, target, visited, queue);
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

    public boolean isCable(Block block, int meta)
    {
        return block instanceof ICable && ((ICable)block).isCable(meta);
    }

    @Override
    public boolean isCable(int meta)
    {
        return true;
    }

    @Override
    public void getConnectedCables(World world, SystemCoord coordinate, List<SystemCoord> visited, Queue<SystemCoord> cables)
    {
        for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS)
        {
            SystemCoord target = new SystemCoord(coordinate, direction);
            //target.setTileEntity(world.getTileEntity(target.x, target. y, target.z));
            if (!visited.contains(target))
            {
                visited.add(target);
                Block block = world.getBlock(target.x, target.y, target.z);
                int meta = world.getBlockMetadata(target.x, target.y, target.z);
                if (isCable(block, meta))
                {
                    cables.add(target);
                }
            }
        }
    }
}
