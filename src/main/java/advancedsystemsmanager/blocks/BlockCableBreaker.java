package advancedsystemsmanager.blocks;


import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.tileentities.TileEntityBreaker;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableBreaker extends BlockTileBase
{
    public BlockCableBreaker()
    {
        super(Names.CABLE_BREAKER, 3);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBreaker();
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        blockIcon = register.registerIcon(Reference.RESOURCE_LOCATION + ":cable_idle");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);

        if (breaker != null)
        {
            int meta = breaker.getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length;
            int direction = breaker.getPlaceDirection();

            if (side == meta && side == direction)
            {
                return icons[0];
            } else if (side == meta)
            {
                return icons[1];
            } else if (side == direction)
            {
                return icons[2];
            }
        }

        return blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta)
    {
        return side == 3 ? icons[0] : blockIcon;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            side = ForgeDirection.VALID_DIRECTIONS[side].getOpposite().ordinal();
        }

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);
        if (breaker != null && !breaker.isBlocked())
        {
            breaker.setPlaceDirection(side);
            return true;
        }


        return false;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);
        if (breaker != null)
        {
            breaker.setMetaData(meta);
            breaker.setPlaceDirection(meta);
        }
    }
}
