package thevault.registry;

import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;

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

    public Class TESR() default Object.class;

    public Class IItemRenderer() default Object.class;

    public Class SBRH() default Object.class;

    public Class<? extends ItemBlock> itemBlock() default ItemBlock.class;
}
