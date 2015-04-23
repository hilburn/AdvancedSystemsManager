package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.CopyHelper;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.helpers.StevesEnum;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.settings.Settings;

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
        for (ICommand type : CommandRegistry.getComponents())
        {
            buttons.add(new ManagerButtonCreate(manager, type));
        }
        buttons.add(new ManagerButton(manager, Localization.DELETE_COMMAND.toString(), 230 - IManagerButton.BUTTON_ICON_SIZE, 0)
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
        buttons.add(new ManagerButton(manager, StevesEnum.COPY_COMMAND.toString(), 230 - IManagerButton.BUTTON_ICON_SIZE, IManagerButton.BUTTON_ICON_SIZE)
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
        return buttons;
    }
}
