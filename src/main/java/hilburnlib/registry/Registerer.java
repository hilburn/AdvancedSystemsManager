package hilburnlib.registry;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.API;
import cpw.mods.fml.common.FMLCommonHandler;
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
import net.minecraftforge.client.MinecraftForgeClient;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Registerer
{
    private LogHelper log;

    public Registerer(LogHelper log)
    {
        this.log = log;
    }

    public void scan(Class<?> targetClass)
    {
        Side side = FMLCommonHandler.instance().getSide();
        for (Field field : targetClass.getFields())
        {
            Register annotation = field.getAnnotation(Register.class);
            Class clazz = field.getType();
            if (annotation == null) continue;
            if (!annotation.dependency().isEmpty() && !Loader.isModLoaded(annotation.dependency()) && !ModAPIManager.INSTANCE.hasAPI(annotation.dependency())) continue;
            if (Modifier.isStatic(field.getModifiers()))
            {
                if (Item.class.isAssignableFrom(clazz))
                {
                    registerItem(side, field, annotation, clazz);
                } else if (Block.class.isAssignableFrom(clazz))
                {
                    registerBlock(side, field, annotation, clazz);
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

    private void registerItem(Side side, Field field, Register annotation, Class<? extends Item> clazz)
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
                if (annotation.IItemRenderer() != IItemRenderer.class)
                    MinecraftForgeClient.registerItemRenderer(item, annotation.IItemRenderer().newInstance());
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

    private void registerBlock(Side side, Field field, Register annotation, Class<? extends Block> clazz)
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
                if (annotation.SBRH() != ISimpleBlockRenderingHandler.class)
                {
                    ISimpleBlockRenderingHandler handler = annotation.SBRH().newInstance();
                    RenderingRegistry.registerBlockHandler(block.getRenderType(), handler);
                }
                else if (annotation.tileEntity() != TileEntity.class && annotation.TESR() != TileEntitySpecialRenderer.class)
                    ClientRegistry.bindTileEntitySpecialRenderer(annotation.tileEntity(), annotation.TESR().newInstance());
                if (annotation.IItemRenderer() != IItemRenderer.class)
                    MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), annotation.IItemRenderer().newInstance());
            }
        } catch (Exception e)
        {
            log.warn("Failed to register block " + annotation.name());
        }
    }

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
