package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.registry.ModBlocks;
import advancedsystemsmanager.tileentities.TileEntityCreative;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;


public class BlockCableCreative extends BlockClusterElement
{
    public BlockCableCreative()
    {
        super(AdvancedSystemsManager.UNLOCALIZED_START + ModBlocks.CABLE_CREATIVE_UNLOCALIZED_NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityCreative();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        blockIcon = register.registerIcon(AdvancedSystemsManager.RESOURCE_LOCATION + ":cable_creative");
    }
}