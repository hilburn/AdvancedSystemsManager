package hilburnlib.registry;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Register
{
    public String name() default "";

    public String unlocalizedName() default "";

    public String dependency() default "";

    public Class<? extends TileEntity> tileEntity() default TileEntity.class;

    public Class<? extends TileEntitySpecialRenderer> TESR() default TileEntitySpecialRenderer.class;

    public Class<IItemRenderer> IItemRenderer() default IItemRenderer.class;

    public Class<? extends ISimpleBlockRenderingHandler> SBRH() default ISimpleBlockRenderingHandler.class;

    public Class<? extends ItemBlock> itemBlock() default ItemBlock.class;
}
