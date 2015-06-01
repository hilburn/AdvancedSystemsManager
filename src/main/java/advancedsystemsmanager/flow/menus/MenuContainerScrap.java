package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.GuiManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;


public class MenuContainerScrap extends MenuContainer
{
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 25;

    public MenuContainerScrap(FlowComponent parent)
    {
        super(parent, SystemTypeRegistry.INVENTORY);
    }

    @Override
    public String getName()
    {
        return Names.OVERFLOW_MENU;
    }

    @Override
    public void addErrors(List<String> errors)
    {
        if (selectedInventories.isEmpty())
        {
            errors.add(Names.NO_OVERFLOW_ERROR);
        }
    }

    @Override
    public void initRadioButtons()
    {
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiManager gui, int mX, int mY)
    {
        super.draw(gui, mX, mY);

        if (scrollController.getResult().isEmpty())
        {
            gui.drawSplitString(Names.OVERFLOW_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2 - 20, 0.7F, 0x404040);
        }
    }
}
