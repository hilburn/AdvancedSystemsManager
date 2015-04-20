package advancedfactorymanager.threading;

import advancedfactorymanager.components.ScrollController;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ThreadSafeHandler
{
    public static Map<ScrollController, List<ItemStack>> handle = new LinkedHashMap<ScrollController, List<ItemStack>>();

    @SubscribeEvent
    public void renderEvent(RenderWorldLastEvent e)
    {
        if (!handle.isEmpty())
        {
            for (Map.Entry<ScrollController, List<ItemStack>> entry : handle.entrySet())
            {
                setResult(entry.getKey(), entry.getValue());
            }
            handle.clear();
        }
    }

    public static void setResult(ScrollController controller, List<ItemStack> stackList)
    {
        controller.getResult().clear();
        controller.getResult().addAll(stackList);
    }
}
