package hilburnlib.registry;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public interface IRenderRegistry
{
    void registerItemRenderer(Item item, IItemRenderer renderer);

    void registerSimpleBlockRenderer(int id, ISimpleBlockRenderingHandler renderer);

    void registerTileEntityRenderer(Class<? extends TileEntity> tileEntity, TileEntitySpecialRenderer renderer);
}
