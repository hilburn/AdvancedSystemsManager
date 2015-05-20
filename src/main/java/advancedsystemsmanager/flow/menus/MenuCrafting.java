package advancedsystemsmanager.flow.menus;


import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.execution.CraftingDummy;
import advancedsystemsmanager.flow.setting.CraftingSetting;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

public class MenuCrafting extends MenuItem
{
    public CraftingSetting resultItem;
    public CraftingDummy dummy;

    public MenuCrafting(FlowComponent parent)
    {
        super(parent);

        resultItem = new CraftingSetting(9)
        {
            @Override
            public boolean canChangeMetaData()
            {
                return false;
            }

            @Override
            public void delete()
            {
                for (Setting setting : settings)
                {
                    setting.clear();

                }
            }
        };
        settings.add(resultItem);
        dummy = new CraftingDummy(this);


        scrollControllerSelected.setItemsPerRow(3);
        scrollControllerSelected.setVisibleRows(3);
        scrollControllerSelected.setItemUpperLimit(2);
        scrollControllerSelected.setDisabledScroll(true);
    }

    @Override
    public Setting<ItemStack> getSetting(int id)
    {
        return new CraftingSetting(id);
    }

    @Override
    public String getName()
    {
        return Names.CRAFTING_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        super.onClick(mX, mY, button);
        if (!isEditing() && !isSearching() && resultItem.getItem() != null)
        {
            if (button == 1 && CollisionHelper.inBounds(getResultX(), getResultY(), ITEM_SIZE, ITEM_SIZE, mX, mY))
            {
                scrollControllerSelected.onClick(resultItem, mX, mY, 1);
            }
        }
    }

    @Override
    public void initRadioButtons()
    {
        //no radio buttons
    }

    @Override
    public int getSettingCount()
    {
        return 9;
    }

    @Override
    public void onSettingContentChange()
    {
        resultItem.setContent(dummy.getResult());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem.getItem() != null)
        {
            drawResultObject(gui, resultItem.getItem(), getResultX(), getResultY());
            gui.drawItemAmount(resultItem.getItem(), getResultX(), getResultY());
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY)
    {
        super.drawMouseOver(gui, mX, mY);
        if (!isEditing() && !isSearching() && resultItem.getItem() != null)
        {
            if (CollisionHelper.inBounds(getResultX(), getResultY(), ITEM_SIZE, ITEM_SIZE, mX, mY))
            {
                gui.drawMouseOver(getResultObjectMouseOver(resultItem.getItem()), mX, mY);
            }
        }
    }

    public int getResultX()
    {
        return ITEM_X + ITEM_SIZE_WITH_MARGIN * 3;
    }

    public int getResultY()
    {
        return scrollControllerSelected.getScrollingStartY() + ITEM_SIZE_WITH_MARGIN;
    }

    public CraftingDummy getDummy()
    {
        return dummy;
    }

    public CraftingSetting getResultItem()
    {
        return resultItem;
    }
}
