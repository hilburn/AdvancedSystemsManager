package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.IComponentType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuCraftingPriority;
import advancedsystemsmanager.flow.menus.MenuStuff;
import advancedsystemsmanager.flow.menus.MenuTarget;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.settings.Settings;
import net.minecraft.util.ResourceLocation;

public class ManagerButtonCreate extends ManagerButton
{
    private IComponentType type;

    public ManagerButtonCreate(TileEntityManager manager, IComponentType type)
    {
        super(manager, type.getLongName(), type.getX(), type.getY());
        this.type = type;
    }

    @Override
    public void onClick(DataReader dr)
    {
        if (Settings.isLimitless(manager) || manager.getFlowItems().size() < TileEntityManager.MAX_COMPONENT_AMOUNT)
        {
            FlowComponent component = new FlowComponent(manager, 50, 50, type);

            boolean hasParent = dr.readBoolean();
            if (hasParent)
            {
                component.setParent(manager.items.get(dr.readComponentId()));
            }

            boolean autoSide = dr.readBoolean();
            boolean autoBlackList = dr.readBoolean();
            boolean moveFirst = dr.readBoolean();
            boolean isInput = type.getId() == 1 || type.getId() == 5;
            boolean isOutput = type.getId() == 2 || type.getId() == 6;
            if (autoSide)
            {
                for (Menu menu : component.getMenus())
                {
                    if (menu instanceof MenuTarget)
                    {
                        ((MenuTarget)menu).setActive(isOutput ? 1 : 0);
                    }
                }
            }
            if (autoBlackList && isInput)
            {
                for (Menu menu : component.getMenus())
                {
                    if (menu instanceof MenuStuff)
                    {
                        ((MenuStuff)menu).setBlackList();
                    }
                }
            }
            if (type.getId() == 12)
            {
                for (Menu menu : component.getMenus())
                {
                    if (menu instanceof MenuCraftingPriority)
                    {
                        ((MenuCraftingPriority)menu).setPrioritizeCrafting(!moveFirst);
                    }
                }
            }

            manager.getFlowItems().add(component);
        }
    }

    @Override
    public boolean onClick(DataWriter dw)
    {
        if (manager.selectedComponent != null)
        {
            dw.writeBoolean(true);
            dw.writeComponentId(manager, manager.selectedComponent.getId());
        } else
        {
            dw.writeBoolean(false);
        }

        //these are written for all different types, that's because the type itself doesn't really know what menus
        //it will use, this will create a super tiny overhead (each setting is a bit) and could be eliminated with
        //some semi-ugly code, I decided this approach was fine
        dw.writeBoolean(Settings.isAutoSide());
        dw.writeBoolean(Settings.isAutoBlacklist());
        dw.writeBoolean(Settings.isPriorityMoveFirst());

        return true;
    }

    @Override
    public String getMouseOver()
    {
        if (!Settings.isLimitless(manager) && manager.getFlowItems().size() == TileEntityManager.MAX_COMPONENT_AMOUNT)
        {
            return Localization.MAXIMUM_COMPONENT_ERROR.toString();
        } else
        {
            return Localization.CREATE_COMMAND.toString() + " " + super.getMouseOver();
        }
    }

    @Override
    public ResourceLocation getTexture()
    {
        return type.getTexture();
    }
}
