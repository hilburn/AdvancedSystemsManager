package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;

public abstract class BlockClusterElement extends BlockContainer
{
    protected BlockClusterElement(String name)
    {
        super(Material.iron);
        this.setCreativeTab(AdvancedSystemsManager.creativeTab);
        this.setStepSound(soundTypeMetal);
        this.setBlockName(name);
        this.setHardness(1.2F);
    }
}
