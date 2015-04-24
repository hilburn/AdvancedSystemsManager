package advancedsystemsmanager.util;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.ScrollController;
import advancedsystemsmanager.flow.menus.MenuItem;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import advancedsystemsmanager.api.execution.IHiddenInventory;
import advancedsystemsmanager.api.execution.IHiddenTank;
import advancedsystemsmanager.flow.menus.MenuTriggered;
import advancedsystemsmanager.naming.BlockCoord;
import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.reference.Null;
import advancedsystemsmanager.threading.SearchItems;
import advancedsystemsmanager.util.ConnectionBlock;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;

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

    public static void removeFlowComponent(TileEntityManager manager, int idToRemove)
    {
        for (int id : getIdsToRemove(idToRemove, manager.getFlowItems()))
        {
            manager.removeFlowComponent(id, manager.getFlowItems());
            if (!manager.getWorldObj().isRemote)
            {
                manager.getRemovedIds().add(id);
            } else
            {
                for (int i = 0; i < manager.getZLevelRenderingList().size(); ++i)
                {
                    if ((manager.getZLevelRenderingList().get(i)).getId() == id)
                    {
                        manager.getZLevelRenderingList().remove(i);
                        break;
                    }
                }
            }
            manager.updateVariables();
        }
    }

    public static List<Integer> getIdsToRemove(int idToRemove, List<FlowComponent> items)
    {
        List<Integer> ids = new ArrayList<Integer>();
        getIdsToRemove(ids, idToRemove, items);
        Collections.sort(ids);
        Collections.reverse(ids);
        return ids;
    }
    
    private static void getIdsToRemove(List<Integer> ids, int idToRemove, List<FlowComponent> items)
    {
        for (FlowComponent component : items)
        {
            if (component.getParent() != null && component.getParent().getId() == idToRemove) getIdsToRemove(ids, component.getId(), items);
        }
        ids.add(idToRemove);
    }

    public static boolean instanceOf(Class clazz, TileEntity entity)
    {
        return clazz.isInstance(entity) || entity instanceof IHiddenTank && clazz == IFluidHandler.class || entity instanceof IHiddenInventory && clazz == IInventory.class || clazz == IEnergyConnection.class && (entity instanceof IEnergyProvider || entity instanceof IEnergyReceiver);
    }

    public static String getContentString(TileEntity tileEntity)
    {
        String result = "";
        if (tileEntity instanceof IDeepStorageUnit)
        {
            ItemStack stack = ((IDeepStorageUnit)tileEntity).getStoredItemType();
            String contains = "\n";
            if (stack == null || stack.isItemEqual(Null.NULL_STACK))
                contains += StatCollector.translateToLocal("stevesaddons.idsucompat.isEmpty");
            else
                contains += StatCollector.translateToLocalFormatted("stevesaddons.idsucompat.contains", stack.getDisplayName());
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
                    if (info.fluid == null || info.fluid.getFluid() == null) return result;
                    tankInfo += info.fluid.getLocalizedName() + (i++ < fluidTankInfo.length ? ", " : "");
                }
            }
            if (tankInfo.isEmpty()) result += "\n" + StatCollector.translateToLocal("stevesaddons.idsucompat.isEmpty");
            else result += "\n" + StatCollector.translateToLocalFormatted("stevesaddons.idsucompat.contains", tankInfo);
        }
        return result;
    }

    private static String getLabel(TileEntity tileEntity)
    {
        BlockCoord coord = new BlockCoord(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
        return NameRegistry.getSavedName(tileEntity.getWorldObj().provider.dimensionId, coord);
    }

    public static boolean containerAdvancedSearch(ConnectionBlock block, String search)
    {
        TileEntity tileEntity = block.getTileEntity();
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

    public static TileEntityManager tickTriggers(TileEntityManager manager)
    {
        tick(delayedRegistry.get(manager));
        return manager;
    }

    private static void tick(Collection<FlowComponent> triggers)
    {
        if (triggers != null)
        {
            for (Iterator<FlowComponent> itr = triggers.iterator(); itr.hasNext();)
            {
                MenuTriggered toTrigger = (MenuTriggered)itr.next().getMenus().get(6);
                if (toTrigger.isVisible())
                {
                    toTrigger.tick();
                    if (toTrigger.remove()) itr.remove();
                }else
                {
                    itr.remove();
                }
            }
        }
    }

    private static Multimap<TileEntityManager, FlowComponent> getRegistry(MenuTriggered menu)
    {
        return delayedRegistry;
    }
}
