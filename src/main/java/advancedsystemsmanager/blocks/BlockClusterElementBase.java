package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ICluster;
import advancedsystemsmanager.api.tileentities.IClusterElement;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.registry.ClusterRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockClusterElementBase<T extends TileEntity & IClusterTile> extends BlockTileBase implements IClusterElement
{
    private static byte registerID;
    private byte id;

    public BlockClusterElementBase(String name)
    {
        super(name);
        register();
    }

    public void register()
    {
        ClusterRegistry.register(id = registerID++, this);
    }

    public BlockClusterElementBase(String name, float hardness)
    {
        super(name, hardness);
        register();
    }

    public BlockClusterElementBase(String name, int extraIcons)
    {
        super(name, extraIcons);
        register();
    }

    @Override
    public ItemStack getItemStack(int metadata)
    {
        return new ItemStack(this, 1, metadata);
    }

    @Override
    public Block getBlock()
    {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getClusterTile(World world, int metadata)
    {
        return (T)createNewTileEntity(world, metadata);
    }

    @Override
    public byte getId()
    {
        return id;
    }

    @SuppressWarnings("unchecked")
    public T getTileEntity(IBlockAccess world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IClusterTile && isInstance((IClusterTile)tileEntity)) return (T)tileEntity;
        if (tileEntity instanceof ICluster)
        {
            for (IClusterTile tile : ((ICluster)tileEntity).getTiles())
            {
                if (isInstance(tile)) return (T)tile;
            }
        }
        return null;
    }
}
