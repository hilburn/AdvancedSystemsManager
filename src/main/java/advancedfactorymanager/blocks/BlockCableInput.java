package advancedfactorymanager.blocks;

import advancedfactorymanager.tileentities.TileEntityCluster;
import advancedfactorymanager.tileentities.TileEntityInput;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import advancedfactorymanager.AdvancedFactoryManager;


public class BlockCableInput extends BlockContainer {
    public BlockCableInput() {
        super(Material.iron);
        setCreativeTab(ModBlocks.creativeTab);
        setStepSound(soundTypeMetal);
        setBlockName(AdvancedFactoryManager.UNLOCALIZED_START + ModBlocks.CABLE_INPUT_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityInput();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register) {
        blockIcon = register.registerIcon(AdvancedFactoryManager.RESOURCE_LOCATION + ":cable_input");
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);

        updateRedstone(world, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);

        updateRedstone(world, x, y, z);
    }

    private void updateRedstone(World world, int x, int y, int z) {
        TileEntityInput input = TileEntityCluster.getTileEntity(TileEntityInput.class, world, x, y, z);
        if (input != null) {
            input.triggerRedstone();
        }
    }
}
