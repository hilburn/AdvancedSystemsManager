package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.util.ItemUtils;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

@Optional.Interface(iface = "cofh.api.block.IDismantleable", modid = Mods.COFH_BLOCK)
public abstract class BlockTileBase extends BlockBase implements ITileEntityProvider, IDismantleable
{
    protected BlockTileBase(String name)
    {
        super(name);
        this.isBlockContainer = true;
    }

    protected BlockTileBase(String name, float hardness)
    {
        super(name, hardness);
        this.isBlockContainer = true;
    }

    protected BlockTileBase(String name, int extraIcons)
    {
        super(name, extraIcons);
        this.isBlockContainer = true;
    }

    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        super.breakBlock(world, x, y, z, block, meta);
        world.removeTileEntity(x, y, z);
    }

    public boolean onBlockEventReceived(World world, int x, int y, int z, int p_149696_5_, int p_149696_6_)
    {
        super.onBlockEventReceived(world, x, y, z, p_149696_5_, p_149696_6_);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(p_149696_5_, p_149696_6_);
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock)
    {
        ArrayList<ItemStack> list = getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        world.setBlockToAir(x, y, z);
        if (!returnBlock)
            for (ItemStack item : list)
                dropBlockAsItem(world, x, y, z, item);
        return list;
    }

    @Override
    @Optional.Method(modid = Mods.COFH_BLOCK)
    public boolean canDismantle(EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        return entityPlayer.isSneaking();
    }
}
