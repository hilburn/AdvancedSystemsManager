package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityBaseGate;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class BlockCableGate extends BlockTileBase
{
    public BlockCableGate(String name, int icons)
    {
        super(name, icons);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            side = ForgeDirection.VALID_DIRECTIONS[side].getOpposite().ordinal();
        }

        TileEntityBaseGate breaker = TileEntityCluster.getTileEntity(TileEntityBaseGate.class, world, x, y, z);
        if (breaker != null && !breaker.isBlocked())
        {
            breaker.setPlaceDirection(side);
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return side == 3 ? icons[0] : blockIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntityBaseGate breaker = TileEntityCluster.getTileEntity(TileEntityBaseGate.class, world, x, y, z);
        if (breaker != null)
        {
            breaker.setMetaData(meta);
            breaker.setPlaceDirection(meta);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        TileEntityBaseGate gate = TileEntityCluster.getTileEntity(TileEntityBaseGate.class, world, x, y, z);
        if (gate != null)
        {
            gate.onNeighbourBlockChange(world, x, y, z, block);
        }
    }
}
