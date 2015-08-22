package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.CollisionHelper;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.manager.Settings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MenuGroup extends Menu
{
    public static final int MENU_WIDTH = 120;
    public static final int TEXT_MARGIN_X = 5;
    public static final int TEXT_Y = 5;

    public MenuGroup(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public String getName()
    {
        return Names.GROUP_MENU;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void draw(GuiBase gui, int mX, int mY)
    {
        gui.drawSplitString(Names.GROUP_INFO, TEXT_MARGIN_X, TEXT_Y, MENU_WIDTH - TEXT_MARGIN_X * 2, 1F, 0x404040);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onClick(int mX, int mY, int button)
    {
        if (inBounds(mX, mY)) getParent().getManager().setSelectedGroup(getParent());
    }

    public boolean inBounds(int mX, int mY)
    {
        return (CollisionHelper.inBounds(0, 0, MENU_WIDTH, FlowComponent.getMenuOpenSize(), mX, mY));
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void onRelease(int mX, int mY, int button, boolean isMenuOpen)
    {
        if (isMenuOpen && inBounds(mX, mY))
        {
            for (FlowComponent component : getParent().getManager().getFlowItems())
            {
                if (component.isBeingMoved())
                {
                    if (!component.equals(getParent()))
                    {
                        boolean group = button == 2 && Settings.getSetting(Settings.MIDDLE_CLICK);
                        component.sendNewParentData(getParent(), group);
                        FlowComponent.moveComponents(component, getParent(), group);
                    }
                    break;
                }
            }
        }
    }
}
