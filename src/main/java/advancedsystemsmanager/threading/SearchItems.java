package advancedsystemsmanager.threading;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.flow.elements.ScrollController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchItems implements Runnable
{
    public static List<SearchEntry> searchEntries = new ArrayList<SearchEntry>();
    public static List<SearchEntry> searchBlocks = new ArrayList<SearchEntry>();
    private long time;
    private String search;
    private ScrollController controller;
    private boolean showAll;
    private boolean justBlocks;
    private List<ItemStack> items = new ArrayList<ItemStack>();

    public SearchItems(String search, ScrollController controller, boolean showAll)
    {
        this(search, controller, showAll, false);
    }

    public SearchItems(String search, ScrollController controller, boolean showAll, boolean blocks)
    {
        this.search = search;
        this.controller = controller;
        this.showAll = showAll;
        this.justBlocks = blocks;
        this.time = System.currentTimeMillis();
    }

    public static void setItems()
    {
        searchBlocks.clear();
        searchEntries.clear();
        new Thread(new CacheItems()).start();
    }

    @Override
    public void run()
    {
        if (search.equals(".inv"))
        {
            InventoryPlayer inventory = Minecraft.getMinecraft().thePlayer.inventory;
            for (int itemStack = 0; itemStack < inventory.getSizeInventory(); ++itemStack)
            {
                ItemStack stack = inventory.getStackInSlot(itemStack);
                if (stack != null && (!justBlocks || stack.getItem() instanceof ItemBlock))
                {
                    stack = stack.copy();
                    stack.stackSize = 1;
                    items.add(stack);
                }
            }
        } else
        {
            List<SearchEntry> toSearch = justBlocks ? searchBlocks : searchEntries;
            if (!showAll)
            {
                Pattern pattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
                boolean advanced = Minecraft.getMinecraft().gameSettings.advancedItemTooltips;
                for (SearchEntry entry : toSearch) entry.search(pattern, items, advanced);
            } else
            {
                for (SearchEntry entry : toSearch) items.add(entry.getStack());
            }
        }
        if (time > controller.getLastUpdate())
            ThreadSafeHandler.handle(this);
    }

    public void setResult()
    {
        controller.result = items;
        controller.lastUpdate = time;
    }

    public ScrollController getScrollController()
    {
        return controller;
    }

    public long getTime()
    {
        return time;
    }

    public static class CacheItems implements Runnable
    {
        @Override
        public void run()
        {
            long time = System.currentTimeMillis();
            List<ItemStack> stacks = new ArrayList<ItemStack>();
            for (Object anItemRegistry : Item.itemRegistry)
            {
                try
                {
                    Item item = (Item)anItemRegistry;
                    item.getSubItems(item, item.getCreativeTab(), stacks);
                } catch (Exception ignore)
                {
                }
            }
            for (ItemStack stack : stacks)
            {
                SearchEntry entry = getSearch(stack);
                if (entry != null)
                {
                    searchEntries.add(entry);
                    if (stack.getItem() instanceof ItemBlock)
                    {
                        searchBlocks.add(entry);
                    }
                }
            }
            AdvancedSystemsManager.log.info("Search database generated in " + (System.currentTimeMillis() - time) + "ms");
        }

        public SearchEntry getSearch(ItemStack stack)
        {
            try
            {
                List tooltipList = stack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
                List advTooltipList = stack.getTooltip(Minecraft.getMinecraft().thePlayer, true);
                String searchString = "";
                for (Object string : tooltipList)
                {
                    if (string != null)
                        searchString += string + "\n";
                }
                String advSearchString = "";
                for (Object string : advTooltipList)
                {
                    if (string != null)
                        advSearchString += string + "\n";
                }
                return new SearchEntry(searchString, advSearchString, stack);
            } catch (Throwable ignore)
            {
            }
            return null;
        }
    }

    public static class SearchEntry
    {
        private String toolTip;
        private String advToolTip;
        private ItemStack stack;

        public SearchEntry(String searchString, String advSearchString, ItemStack stack)
        {
            this.toolTip = searchString;
            this.advToolTip = advSearchString;
            this.stack = stack;
        }

        public void search(Pattern pattern, List<ItemStack> stacks, boolean advanced)
        {
            if (pattern.matcher(advanced ? advToolTip : toolTip).find()) stacks.add(stack);
        }

        public ItemStack getStack()
        {
            return stack;
        }
    }
}
