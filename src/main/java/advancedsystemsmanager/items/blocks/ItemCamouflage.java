package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.tileentities.TileEntityCamouflage;
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
        return "tile." + AdvancedSystemsManager.UNLOCALIZED_START + TileEntityCamouflage.CamouflageType.values()[BlockRegistry.blockCableCamouflage.getId(item.getItemDamage())].getUnlocalized();
    }

}
