package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ICable;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import advancedsystemsmanager.util.SystemCoord;
import com.cricketcraft.chisel.api.IFacade;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Queue;

@Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = Mods.CHISEL)
public class BlockCamouflaged extends BlockTileElement implements IFacade, ICable
{
    public static int RENDER_ID;

    public BlockCamouflaged(String name)
    {
        super(name);
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z)
    {
        TileEntityCamouflage camouflage = TileFactories.CAMO.getTileEntity(world, x, y, z);
        return camouflage == null || camouflage.isNormalBlock();
    }

    @Override
    public int getRenderType()
    {
        return RENDER_ID;
    }

    @Override
    public float getBlockHardness(World world, int x, int y, int z)
    {
        TileEntityCamouflage camouflage = TileFactories.CAMO.getTileEntity(world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape() && !camouflage.isUseCollision())
        {
            return 600000;
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        if (!setBlockCollisionBoundsBasedOnState(world, x, y, z))
        {
            return null;
        }
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
        if (!setBlockCollisionBoundsBasedOnState(world, x, y, z))
        {
            setBlockBounds(0, 0, 0, 0, 0, 0);
        }
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
    {
        if (!setBlockCollisionBoundsBasedOnState(world, x, y, z))
        {
            setBlockBounds(0, 0, 0, 0, 0, 0);
        }
        return super.collisionRayTrace(world, x, y, z, start, end);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        TileEntityCamouflage camouflage = TileFactories.CAMO.getTileEntity(world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape())
        {
            camouflage.setBlockBounds(this);
        } else
        {
            setBlockBoundsForItemRender();
        }
    }

    @Override
    public void setBlockBoundsForItemRender()
    {
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer)
    {
        TileEntityCamouflage camouflage = TileFactories.CAMO.getTileEntity(worldObj, target.blockX, target.blockY, target.blockZ);
        if (camouflage != null)
        {
            if (camouflage.addBlockEffect(this, target.sideHit, effectRenderer))
            {
                return true;
            }
        }
        return false;
    }

    private boolean setBlockCollisionBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
    {
        setBlockBoundsBasedOnState(world, x, y, z);

        TileEntityCamouflage camouflage = TileFactories.CAMO.getTileEntity(world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape())
        {
            if (!camouflage.isUseCollision())
            {
                return false;
            } else if (camouflage.isFullCollision())
            {
                setBlockBoundsForItemRender();
            }
        }

        return true;
    }

    @Override
    @Optional.Method(modid = Mods.CHISEL)
    public Block getFacade(IBlockAccess world, int x, int y, int z, int side)
    {
        if (side != -1)
        {
            TileEntityCamouflage camo = TileFactories.CAMO.getTileEntity(world, x, y, z);
            if (camo != null && camo.hasSideBlock(0))
            {
                return camo.getSideBlock(0);
            }
        }
        return this;
    }

    @Override
    @Optional.Method(modid = Mods.CHISEL)
    public int getFacadeMetadata(IBlockAccess world, int x, int y, int z, int side)
    {
        if (side != -1)
        {
            TileEntityCamouflage camo = TileFactories.CAMO.getTileEntity(world, x, y, z);
            if (camo != null && camo.hasSideBlock(0))
            {
                return camo.getSideMetadata(0);
            }
        }
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public boolean isCable(int meta)
    {
        return false;//meta == TileFactories.CLUSTER_ADVANCED.getMetadata();
    }

    @Override
    public void getConnectedCables(World world, SystemCoord coordinate, List<SystemCoord> visited, Queue<SystemCoord> cables)
    {
        BlockRegistry.cable.getConnectedCables(world, coordinate, visited, cables);
    }
}
