package advancedsystemsmanager.client.render;

import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

public class CamouflageBlockAccess implements IBlockAccess
{
    protected int side;
    private TileEntityCamouflage camouflage;
    private IBlockAccess wrapped;

    public CamouflageBlockAccess(int side, TileEntityCamouflage camo, IBlockAccess wrapped)
    {
        this.side = side;
        this.camouflage = camo;
        this.wrapped = wrapped;
    }

    @Override
    public Block getBlock(int x, int y, int z)
    {
        if (x == camouflage.xCoord && y == camouflage.yCoord && z == camouflage.zCoord)
        {
            return camouflage.getSideBlock(side);
        } else
        {
            return wrapped.getBlock(x, y, z);
        }
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z)
    {
        return wrapped.getTileEntity(x, y, z);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_)
    {
        return wrapped.getLightBrightnessForSkyBlocks(p_72802_1_, p_72802_2_, p_72802_3_, p_72802_4_);
    }

    @Override
    public int getBlockMetadata(int x, int y, int z)
    {
        return camouflage.getSideMetadata(side);
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int side)
    {
        return 0;
    }

    @Override
    public boolean isAirBlock(int x, int y, int z)
    {
        return camouflage != null;
    }

    @Override
    public BiomeGenBase getBiomeGenForCoords(int x, int z)
    {
        return wrapped.getBiomeGenForCoords(x, z);
    }

    @Override
    public int getHeight()
    {
        return wrapped.getHeight();
    }

    @Override
    public boolean extendedLevelsInChunkCache()
    {
        return wrapped.extendedLevelsInChunkCache();
    }

    @Override
    public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default)
    {
        return wrapped.isSideSolid(x, y, z, side, _default);
    }
}
