package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.RadioButton;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.threading.SearchItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;

import java.util.List;

public class MenuCamouflageItems extends MenuItem
{
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 40;
    public static final int MENU_WIDTH = 120;

    public MenuCamouflageItems(FlowComponent parent)
    {
        super(parent);
        scrollControllerSelected.setDisabledScroll(true);
    }

    @Override
    public int getSettingCount()
    {
        return 1;
    }

    @Override
    public void initRadioButtons()
    {
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_LEFT, RADIO_BUTTON_Y, Names.CLEAR_CAMOUFLAGE));
        radioButtons.add(new RadioButton(RADIO_BUTTON_X_RIGHT, RADIO_BUTTON_Y, Names.SET_CAMOUFLAGE));
    }

    @Override
    public boolean doAllowEdit()
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiBase gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);

        if (!isEditing() && !isSearching())
        {
            gui.drawSplitString(Names.CAMOUFLAGE_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 0.7F, 0x404040);
        }
    }

    @Override
    public boolean isListVisible()
    {
        return isSearching() || !isFirstRadioButtonSelected();
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (!isFirstRadioButtonSelected() && !getSettings().get(0).isValid())
        {
            errors.add(Names.NO_CAMOUFLAGE_SETTING);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public List<ItemStack> updateSearch(String search, boolean showAll)
    {
        Thread thread = new Thread(new SearchItems(search, scrollControllerSearch, showAll, true));
        thread.start();
        return scrollControllerSearch.getResult();
    }

    @Override
    public String getName()
    {
        return Names.CAMOUFLAGE_ITEM_MENU;
    }
}
