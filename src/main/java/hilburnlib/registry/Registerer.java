package hilburnlib.registry;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import hilburnlib.utils.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Registerer
{
    private static IConfigLock NULL_LOCK = new IConfigLock()
    {
        @Override
        public boolean shouldRegister(String string, boolean defaultValue)
        {
            return true;
        }
    };
    private LogHelper log;
    private IRenderRegistry renderRegistry;
    private IConfigLock configLock;

    public Registerer(LogHelper log, IRenderRegistry renderRegistry)
    {
        this(log, renderRegistry, NULL_LOCK);
    }

    public Registerer(LogHelper log, IRenderRegistry renderRegistry, IConfigLock configLock)
    {
        this.log = log;
        this.renderRegistry = renderRegistry;
        this.configLock = configLock;
    }

    public void scan(Class<?> targetClass, Side side)
    {
        for (Field field : targetClass.getFields())
        {
            Register annotation = field.getAnnotation(Register.class);
            if (annotation == null) continue;
            ConfigKey configKey = field.getAnnotation(ConfigKey.class);
            if ((annotation.dependency().isEmpty() || Loader.isModLoaded(annotation.dependency()) || ModAPIManager.INSTANCE.hasAPI(annotation.dependency())) &&
                    (configKey == null || configLock.shouldRegister(configKey.value(), configKey.configDefault())))
            {
                Class clazz = field.getType();
                if (Modifier.isStatic(field.getModifiers()))
                {
                    if (Item.class.isAssignableFrom(clazz))
                    {
                        registerItem(field, annotation, clazz, side);
                    } else if (Block.class.isAssignableFrom(clazz))
                    {
                        registerBlock(field, annotation, clazz, side);
                    } else
                    {
                        log.warn("Can only register Blocks and Items - " + field.getName() + " unrecognised");
                    }
                } else
                {
                    log.warn("Can't register non-static field " + field.getName());
                }
            }
        }
    }

    private void registerItem(Field field, Register annotation, Class<? extends Item> clazz, Side side)
    {
        try
        {
            Item item;
            if ((item = (Item)field.get(null)) == null)
            {
                item = getConstructed(clazz);
                field.set(null, item);
            }
            if (!annotation.unlocalizedName().isEmpty()) item.setUnlocalizedName(annotation.unlocalizedName());
            GameRegistry.registerItem(item, getName(annotation).isEmpty() ? item.getUnlocalizedName() : getName(annotation));
            if (side == Side.CLIENT)
            {
                if (annotation.IItemRenderer() != Object.class)
                    renderRegistry.registerItemRenderer(item, (IItemRenderer)annotation.IItemRenderer().newInstance());
            }
        } catch (Exception e)
        {
            log.warn("Failed to register item " + annotation.name());
        }
    }

    private static String getName(Register annotation)
    {
        return annotation.name().isEmpty() ? annotation.unlocalizedName() : annotation.name();
    }

    private void registerBlock(Field field, Register annotation, Class<? extends Block> clazz, Side side)
    {
        try
        {
            Block block;
            if ((block = (Block)field.get(null)) == null)
            {
                block = getConstructed(clazz);
                field.set(null, block);
            }
            if (!annotation.unlocalizedName().isEmpty()) block.setBlockName(annotation.unlocalizedName());
            GameRegistry.registerBlock(block, annotation.itemBlock(), getName(annotation).isEmpty() ? block.getUnlocalizedName() : getName(annotation));
            if (annotation.tileEntity() != TileEntity.class)
                GameRegistry.registerTileEntity(annotation.tileEntity(), annotation.name());
            if (side == Side.CLIENT)
            {
                if (annotation.SBRH() != Object.class)
                {
                    ISimpleBlockRenderingHandler handler = getConstructed(annotation.SBRH());
                    renderRegistry.registerSimpleBlockRenderer(block.getRenderType(), handler);
                } else if (annotation.tileEntity() != TileEntity.class && annotation.TESR() != Object.class)
                    renderRegistry.registerTileEntityRenderer(annotation.tileEntity(), (TileEntitySpecialRenderer)annotation.TESR().newInstance());
                if (annotation.IItemRenderer() != Object.class)
                    renderRegistry.registerItemRenderer(Item.getItemFromBlock(block), (IItemRenderer)annotation.IItemRenderer().newInstance());
            }
        } catch (Exception e)
        {
            log.warn("Failed to register block " + annotation.name());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getConstructed(Class clazz)
    {
        try
        {
            return (T)clazz.newInstance();

        } catch (Exception e)
        {
            return null;
        }
    }
}
