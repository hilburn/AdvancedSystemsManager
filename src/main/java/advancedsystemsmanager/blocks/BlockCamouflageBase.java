package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import com.cricketcraft.chisel.api.IFacade;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = Mods.CHISEL)
public abstract class BlockCamouflageBase extends BlockClusterElementBase<TileEntityCamouflage> implements IFacade
{
    public static int RENDER_ID;

    public BlockCamouflageBase(String name)
    {
        super(name);
    }

    public BlockCamouflageBase(String name, int extraIcons)
    {
        super(name, extraIcons);
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z)
    {
        TileEntityCamouflage camouflage = getTileEntity(world, x, y, z);
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
        TileEntityCamouflage camouflage = getTileEntity(world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape() && !camouflage.isUseCollision())
        {
            return 600000;
        }
        return super.getBlockHardness(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public final IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntityCamouflage te = getTileEntity(world, x, y, z);
        if (te != null)
        {
            IIcon icon = te.getIconWithDefault(world, x, y, z, this, side, false);

            if (icon != null)
            {
                return icon;
            }
        }
        return getDefaultIcon(side, world.getBlockMetadata(x, y, z), 0);
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
        TileEntityCamouflage camouflage = getTileEntity(world, x, y, z);
        if (camouflage != null && camouflage.getCamouflageType().useSpecialShape())
        {
            camouflage.setBlockBounds(this);
        } else
        {
            setBlockBoundsForItemRender();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
//        TileEntityCamouflage camouflage = getTileEntity(world, x, y, z);
//        if (camouflage != null)
//        {
//            if (camouflage.hasSideBlock(side))
//            {
//                return camouflage.getSideBlock(side).shouldSideBeRendered(world, x, y, z, side);
//            }
//        }
        return super.shouldSideBeRendered(world, x, y, z, side);
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
        TileEntityCamouflage camouflage = getTileEntity(worldObj, target.blockX, target.blockY, target.blockZ);
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

        TileEntityCamouflage camouflage = getTileEntity(world, x, y, z);
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

    @SideOnly(Side.CLIENT)
    public abstract IIcon getDefaultIcon(int side, int blockMeta, int camoMeta);

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityCamouflage;
    }

    @Override
    @Optional.Method(modid = Mods.CHISEL)
    public Block getFacade(IBlockAccess world, int x, int y, int z, int side)
    {
        if (side != -1)
        {
            TileEntityCamouflage camo = getTileEntity(world, x, y, z);
            if (camo != null)
            {
                return camo.getSideBlock(side);
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
            TileEntityCamouflage camo = getTileEntity(world, x, y, z);
            if (camo != null)
            {
                return camo.getSideMetadata(side);
            }
        }
        return world.getBlockMetadata(x, y, z);
    }
}
