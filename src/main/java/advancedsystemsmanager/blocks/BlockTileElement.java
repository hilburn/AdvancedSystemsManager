package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.api.tileentities.*;
import advancedsystemsmanager.helpers.BlockHelper;
import advancedsystemsmanager.items.blocks.ItemTileElement;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
import advancedsystemsmanager.util.SystemCoord;
import cofh.api.block.IDismantleable;
import com.cricketcraft.chisel.api.IFacade;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.*;

@Optional.InterfaceList({
        @Optional.Interface(iface = "com.cricketcraft.chisel.api.IFacade", modid = Mods.CHISEL),
        @Optional.Interface(iface = "cofh.api.block.IDismantleable", modid = Mods.COFHCORE)})
public class BlockTileElement extends Block implements IFacade, ICable, IDismantleable
{
    public static int RENDER_ID;
    private ITileFactory[] factories;
    private int id;

    public BlockTileElement(int id)
    {
        super(Material.iron);
        this.id = id;
        setCreativeTab(AdvancedSystemsManager.creativeTab);
        setStepSound(soundTypeMetal);
        setBlockName("element" + id);
        setHardness(1.2f);
        clearFactories();
    }

    public void clearFactories()
    {
        factories = new ITileFactory[16];
    }

    public void setFactories(Collection<ITileFactory> factories)
    {
        for (ITileFactory factory : factories)
        {
            int factoryId = ClusterRegistry.getId(factory);
            if (factoryId >= id * 16 && factoryId < (id + 1) * 16)
            {
                factoryId &= 0xF;
                this.factories[factoryId] = factory;
                factory.setBlock(this);
                factory.setMetadata(factoryId);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        if (this == BlockRegistry.cableElements[0]) ClusterRegistry.registerIcons(register);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        ITileFactory factory = getTileFactory(world.getBlockMetadata(x, y, z));
        if (factory != null)
        {
            return ((ITileElement)factory.getTileEntity(world, x, y, z)).getIcon(side);
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        ITileFactory factory = getTileFactory(meta);
        return factory != null ? factory.getIcon(side, meta / 16) : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for (ITileFactory element : factories)
        {
            if (element != null) element.getSubBlocks(item, tab, list);
        }
    }

    @Override
    public boolean hasTileEntity(int meta)
    {
        ITileFactory factory = getTileFactory(meta);
        return factory != null && factory.hasTileEntity();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        ITileFactory factory = getTileFactory(meta);
        return factory != null ? factory.createTileEntity(world, meta) : null;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IBUDListener)
        {
            ((IBUDListener) tileEntity).onNeighborBlockChange();
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IActivateListener && ((IActivateListener) tileEntity).onBlockActivated(player, side, xSide, ySide, zSide);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof IPlaceListener)
        {
            ((IPlaceListener) tileEntity).onBlockPlacedBy(entity, item);
        }
    }

    @Override
    public boolean canProvidePower()
    {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener && ((IRedstoneListener) tileEntity).canConnectRedstone(side);
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener ? ((IRedstoneListener) tileEntity).getComparatorInputOverride(side) : 0;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener && ((IRedstoneListener) tileEntity).shouldCheckWeakPower(side);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener ? ((IRedstoneListener) tileEntity).isProvidingWeakPower(side) : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        return tileEntity instanceof IRedstoneListener ? ((IRedstoneListener) tileEntity).isProvidingStrongPower(side) : 0;
    }

    @Override
    public int damageDropped(int meta)
    {
        return meta;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        return getStackData(world, x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>(Collections.singletonList(getPickBlock(null, world, x, y, z, null)));
    }

    public ItemStack getStackData(World world, int x, int y, int z)
    {
        int damage = world.getBlockMetadata(x, y, z);
        TileEntity te = world.getTileEntity(x, y, z);
        NBTTagCompound tag = null;
        if (te instanceof ITileElement)
        {
            damage += 16 * ((ITileElement) te).getSubtype();
            tag = new NBTTagCompound();
            ((ITileElement)te).writeItemNBT(tag);
        }
        ItemStack result = new ItemStack(this, 1, damageDropped(damage));
        result.setTagCompound(tag);
        return result;
    }

    public ITileFactory getTileFactory(ItemStack stack)
    {
        if (stack == null || !(stack.getItem() instanceof ItemTileElement))
        {
            return null;
        }
        return getTileFactory(stack.getItemDamage());
    }

    public ITileFactory getTileFactory(int blockMetadata)
    {
        return factories[blockMetadata & 0xF];
    }


    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess world, int x, int y, int z)
    {
        TileEntityCamouflage camouflage = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
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
        TileEntityCamouflage camouflage = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
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
        TileEntityCamouflage camouflage = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
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
        TileEntityCamouflage camouflage = ClusterRegistry.CAMO.getTileEntity(worldObj, target.blockX, target.blockY, target.blockZ);
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

        TileEntityCamouflage camouflage = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
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
            TileEntityCamouflage camo = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
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
            TileEntityCamouflage camo = ClusterRegistry.CAMO.getTileEntity(world, x, y, z);
            if (camo != null && camo.hasSideBlock(0))
            {
                return camo.getSideMetadata(0);
            }
        }
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z)
    {
        ITileFactory factory = getTileFactory(world.getBlockMetadata(x, y, z));
        return factory == null ? super.getPlayerRelativeBlockHardness(player, world, x, y, z) : factory.getBlockHardness();
    }

    @Override
    public boolean isCable(World world, int x, int y, int z)
    {
        ITileFactory factory = getTileFactory(world.getBlockMetadata(x, y, z));
        return factory != null && factory.isCable((ITileElement)factory.getTileEntity(world, x, y, z));
    }

    @Override
    public void getConnectedCables(World world, SystemCoord coordinate, List<SystemCoord> visited, Queue<SystemCoord> cables)
    {
        BlockHelper.getAdjacentCables(coordinate, visited, cables);
    }

    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock)
    {
        ArrayList<ItemStack> list = getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        world.setBlockToAir(x, y, z);
        if (!returnBlock)
            for (ItemStack item : list)
                dropBlockAsItem(world, x, y, z, item);
        return list;
    }

    public boolean canDismantle(EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        return entityPlayer.isSneaking();
    }
}
