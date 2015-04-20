package advancedfactorymanager.blocks;

import advancedfactorymanager.AdvancedFactoryManager;
import advancedfactorymanager.tileentities.TileEntityCamouflage;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;


public class ItemCamouflage extends ItemBlock
{

    public ItemCamouflage(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item)
    {
        return "tile." + AdvancedFactoryManager.UNLOCALIZED_START + TileEntityCamouflage.CamouflageType.values()[ModBlocks.blockCableCamouflage.getId(item.getItemDamage())].getUnlocalized();
    }

}
