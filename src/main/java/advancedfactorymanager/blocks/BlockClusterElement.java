package advancedfactorymanager.blocks;

import advancedfactorymanager.registry.ModBlocks;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class BlockClusterElement extends BlockContainer
{
    protected BlockClusterElement(String name)
    {
        super(Material.iron);
        this.setCreativeTab(ModBlocks.creativeTab);
        this.setStepSound(soundTypeMetal);
        this.setBlockName(name);
        this.setHardness(1.2F);
    }
}
