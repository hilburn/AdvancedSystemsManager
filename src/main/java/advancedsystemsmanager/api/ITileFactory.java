package advancedsystemsmanager.api;

import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.api.tiletypes.ITileElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public interface ITileFactory
{
    void setBlock(Block block);

    Block getBlock();

    void setMetadata(int metadata);

    int getMetadata();

    String getUnlocalizedName();

    ItemStack getItemStack();

    boolean hasTileEntity();

    boolean canBeAddedToCluster(List<ITileElement> clusterElements);

    TileEntity createTileEntity(World world, int metadata);

    <T extends TileEntity>T getTileEntity(IBlockAccess world, int x, int y, int z);

    boolean isInstance(TileEntity tileEntity);

    @SideOnly(Side.CLIENT)
    void registerIcons(IIconRegister register);

    @SideOnly(Side.CLIENT)
    void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list);

    @SideOnly(Side.CLIENT)
    IIcon getIcon(int side);

    @SideOnly(Side.CLIENT)
    IIcon[] getIcons();
}
