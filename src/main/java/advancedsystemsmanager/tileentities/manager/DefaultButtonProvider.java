package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuGroup;
import advancedsystemsmanager.helpers.CopyHelper;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.registry.ComponentType;
import advancedsystemsmanager.settings.Settings;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DefaultButtonProvider implements IManagerButtonProvider
{
    @Override
    public List<IManagerButton> getButtons(TileEntityManager manager)
    {
        List<IManagerButton> buttons = new ArrayList<IManagerButton>();
        for (ComponentType type : ComponentType.values())
        {
            buttons.add(new ManagerButtonCreate(manager, type));
        }
        buttons.add(new ManagerButton(manager, Localization.DELETE_COMMAND)
        {
            @Override
            public void onClick(DataReader dr)
            {
                int idToRemove = dr.readComponentId();
                manager.removeFlowComponent(idToRemove);
            }

            @Override
            public boolean onClick(DataWriter dw)
            {
                manager.justSentServerComponentRemovalPacket = true;
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        dw.writeComponentId(manager, item.getId());
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean activateOnRelease()
            {
                return true;
            }
        });
        buttons.add(new ManagerButton(manager, StevesEnum.COPY_COMMAND)
        {
            @Override
            public void onClick(DataReader dataReader)
            {
                if (Settings.isLimitless(manager) || manager.getFlowItems().size() < 511)
                {
                    int id = dataReader.readComponentId();
                    Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                    FlowComponent item;
                    do
                    {
                        if (!itr.hasNext()) return;
                        item = itr.next();
                    } while (item.getId() != id);
                    Collection<FlowComponent> added = CopyHelper.copyConnectionsWithChildren(manager.getFlowItems(), item, Settings.isLimitless(manager));
                    manager.getFlowItems().addAll(added);
                }
            }

            @Override
            public boolean activateOnRelease()
            {
                return true;
            }

            @Override
            public boolean onClick(DataWriter dataWriter)
            {
                Iterator<FlowComponent> itr = manager.getFlowItems().iterator();
                FlowComponent item;
                do
                {
                    if (!itr.hasNext()) return false;
                    item = itr.next();
                } while (!item.isBeingMoved());
                dataWriter.writeComponentId(manager, item.getId());
                return true;
            }

            @Override
            public String getMouseOver()
            {
                return !Settings.isLimitless(manager) && manager.getFlowItems().size() >= 511 ? Localization.MAXIMUM_COMPONENT_ERROR.toString() : super.getMouseOver();
            }
        });
        buttons.add(new ManagerButton(manager, Localization.PREFERENCES)
        {
            @Override
            public void onClick(DataReader dr)
            {

            }

            @Override
            public boolean onClick(DataWriter dw)
            {
                Settings.openMenu(manager);
                return false;
            }
        });

        buttons.add(new ManagerButton(manager, Localization.EXIT_GROUP)
        {
            @Override
            public void onClick(DataReader dr)
            {
                int id = dr.readComponentId();
                FlowComponent component = manager.getFlowItems().get(id);
                boolean moveCluster = dr.readBoolean();
                if (component.getParent() != null)
                {
                    MenuGroup.moveComponents(component, component.getParent().getParent(), moveCluster);
                }
            }

            @Override
            public boolean onClick(DataWriter dw)
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        //For the server only
                        manager.justSentServerComponentRemovalPacket = true;
                        dw.writeComponentId(manager, item.getId());
                        dw.writeBoolean(GuiScreen.isShiftKeyDown());
                        item.resetPosition();
                        return true;
                    }
                }

                //Client only
                manager.selectedComponent = manager.selectedComponent.getParent();
                return false;
            }

            @Override
            public boolean isVisible()
            {
                return manager.selectedComponent != null;
            }

            @Override
            public boolean activateOnRelease()
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getMouseOver()
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        return Localization.EXIT_GROUP_DROP.toString();
                    }
                }
                return super.getMouseOver();
            }
        });
        return buttons;
    }
}
