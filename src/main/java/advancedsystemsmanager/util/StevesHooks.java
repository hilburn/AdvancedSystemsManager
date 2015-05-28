package advancedsystemsmanager.util;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuTriggered;
import advancedsystemsmanager.naming.BlockCoord;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

import java.util.*;
import java.util.regex.Pattern;

public class StevesHooks
{
    public static final Multimap<TileEntityManager, FlowComponent> delayedRegistry = HashMultimap.create();

    public static String fixToolTip(String string, TileEntity tileEntity)
    {
        if (tileEntity != null && tileEntity.hasWorldObj())
        {
            String label = getLabel(tileEntity);
            if (label != null) string = "§3" + label;
            string += getContentString(tileEntity);
        }
        return string;
    }

    public static String getContentString(TileEntity tileEntity)
    {
        String result = "";
        if (tileEntity instanceof IDeepStorageUnit)
        {
            ItemStack stack = ((IDeepStorageUnit)tileEntity).getStoredItemType();
            String contains = "\n";
            if (stack == null || stack.isItemEqual(Null.NULL_STACK))
                contains += StatCollector.translateToLocal(Names.BARREL_EMTPTY);
            else
                contains += StatCollector.translateToLocalFormatted(Names.BARREL_CONTAINS, stack.getDisplayName());
            result += contains;
        } else if (tileEntity instanceof IFluidHandler)
        {
            String tankInfo = "";
            int i = 1;
            FluidTankInfo[] fluidTankInfo = ((IFluidHandler)tileEntity).getTankInfo(ForgeDirection.UNKNOWN);
            if (fluidTankInfo != null)
            {
                for (FluidTankInfo info : fluidTankInfo)
                {
                    if (info.fluid == null || info.fluid.getFluid() == null) continue;
                    tankInfo += info.fluid.getLocalizedName() + (i++ < fluidTankInfo.length ? ", " : "");
                }
            }
            if (tankInfo.isEmpty()) result += "\n" + StatCollector.translateToLocal(Names.BARREL_EMTPTY);
            else result += "\n" + StatCollector.translateToLocalFormatted(Names.BARREL_CONTAINS, tankInfo);
        }
        return result;
    }

    private static String getLabel(TileEntity tileEntity)
    {
        BlockCoord coord = new BlockCoord(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        return NameRegistry.getSavedName(tileEntity.getWorldObj().provider.dimensionId, coord);
    }

    public static List<Integer> getIdsToRemove(int idToRemove, Collection<FlowComponent> items)
    {
        List<Integer> ids = new ArrayList<Integer>();
        getIdsToRemove(ids, idToRemove, items);
        Collections.sort(ids);
        Collections.reverse(ids);
        return ids;
    }

    private static void getIdsToRemove(List<Integer> ids, int idToRemove, Collection<FlowComponent> items)
    {
        for (FlowComponent component : items)
        {
            if (component.getParent() != null && component.getParent().getId() == idToRemove)
                getIdsToRemove(ids, component.getId(), items);
        }
        ids.add(idToRemove);
    }

    public static boolean containerAdvancedSearch(SystemCoord block, String search)
    {
        TileEntity tileEntity = block.tileEntity;
        String toSearch = getLabel(tileEntity);
        Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
        return (toSearch != null && pattern.matcher(toSearch).find()) || pattern.matcher(getContentString(tileEntity)).find();
    }

    public static void registerTicker(FlowComponent component, MenuTriggered menu)
    {
        if (!getRegistry(menu).containsEntry(component.getManager(), component))
        {
            getRegistry(menu).put(component.getManager(), component);
        }
    }

    private static Multimap<TileEntityManager, FlowComponent> getRegistry(MenuTriggered menu)
    {
        return delayedRegistry;
    }

    public static TileEntityManager tickTriggers(TileEntityManager manager)
    {
        tick(delayedRegistry.get(manager));
        return manager;
    }

    private static void tick(Collection<FlowComponent> triggers)
    {
        if (triggers != null)
        {
            for (Iterator<FlowComponent> itr = triggers.iterator(); itr.hasNext(); )
            {
                MenuTriggered toTrigger = (MenuTriggered)itr.next().getMenus().get(6);
                if (toTrigger.isVisible())
                {
                    toTrigger.tick();
                    if (toTrigger.remove()) itr.remove();
                } else
                {
                    itr.remove();
                }
            }
        }
    }
}
