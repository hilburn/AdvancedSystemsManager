package advancedsystemsmanager.blocks;


import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.registry.ModBlocks;
import advancedsystemsmanager.tileentities.TileEntityBUD;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableBUD extends BlockClusterElement
{
    public BlockCableBUD()
    {
        super(AdvancedSystemsManager.UNLOCALIZED_START + ModBlocks.CABLE_BUD_UNLOCALIZED_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityBUD();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(AdvancedSystemsManager.RESOURCE_LOCATION + ":cable_bud");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
    {
        TileEntityBUD bud = TileEntityCluster.getTileEntity(TileEntityBUD.class, world, x, y, z);
        if (bud != null)
        {
            bud.onTrigger();
        }
    }


}
