package advancedsystemsmanager.items.blocks;

import advancedsystemsmanager.api.ITileFactory;
import advancedsystemsmanager.api.items.IElementItem;
import advancedsystemsmanager.api.items.ITooltipFactory;
import advancedsystemsmanager.api.tiletypes.ITileElement;
import advancedsystemsmanager.blocks.BlockTileElement;
import advancedsystemsmanager.blocks.TileFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemTileElement extends ItemBlock implements IElementItem
{
    private BlockTileElement block;
    public static Map<ITileFactory, ITooltipFactory> tooltipFactoryMap = new HashMap<ITileFactory, ITooltipFactory>();

    public ItemTileElement(Block block)
    {
        super(block);
        this.block = (BlockTileElement)block;
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getTileFactory(stack).getUnlocalizedName();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List information, boolean advanced)
    {
        ITileFactory factory = getTileFactory(stack);
        if (tooltipFactoryMap.containsKey(factory))
        {
            tooltipFactoryMap.get(factory).addInformation(stack, player, (List<String>) information, advanced);
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
        {
            return false;
        } else if (stack.hasTagCompound())
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof ITileElement)
            {
                ((ITileElement)te).readContentFromNBT(stack.getTagCompound());
            }
        }
        return true;
    }

    @Override
    public ITileFactory getTileFactory(ItemStack stack)
    {
        return block.getTileFactory(stack);
    }
}
