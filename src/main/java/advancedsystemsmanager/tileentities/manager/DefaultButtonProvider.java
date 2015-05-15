package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.CopyHelper;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.settings.Settings;
import io.netty.buffer.ByteBuf;

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
        buttons.add(new ManagerButton(manager, Names.DELETE_COMMAND, 230 - IManagerButton.BUTTON_ICON_SIZE, 0)
        {
            @Override
            public void readData(ByteBuf buf)
            {
                int idToRemove = buf.readInt();
                if (idToRemove != -1)
                    manager.removeFlowComponent(idToRemove);
            }

            @Override
            public boolean validClick()
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
            public void writeNetworkComponent(ByteBuf buf)
            {
                manager.justSentServerComponentRemovalPacket = true;
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        buf.writeInt(item.getId());
                        return;
                    }
                }
                buf.writeInt(-1);
            }

            @Override
            public boolean activateOnRelease()
            {
                return true;
            }
        });
        buttons.add(new ManagerButton(manager, Names.COPY_COMMAND, 230 - IManagerButton.BUTTON_ICON_SIZE, IManagerButton.BUTTON_ICON_SIZE)
        {
            @Override
            public String getMouseOver()
            {
                return !Settings.isLimitless(manager) && manager.getFlowItems().size() >= 511 ? Names.MAXIMUM_COMPONENT_ERROR : super.getMouseOver();
            }

            @Override
            public void readData(ByteBuf buf)
            {
                if (Settings.isLimitless(manager) || manager.getFlowItems().size() < 511)
                {
                    int id = buf.readInt();
                    if (id != -1)
                    {
                        FlowComponent item = manager.getFlowItem(id);
                        Collection<FlowComponent> added = CopyHelper.copyConnectionsWithChildren(manager.getFlowItems(), item, Settings.isLimitless(manager));
                        manager.getFlowItems().addAll(added);
                    }
                }
            }

            @Override
            public boolean validClick()
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
            public boolean activateOnRelease()
            {
                return true;
            }

            @Override
            public void writeNetworkComponent(ByteBuf buf)
            {
                for (FlowComponent item : manager.getFlowItems())
                {
                    if (item.isBeingMoved())
                    {
                        buf.writeInt(item.getId());
                        return;
                    }
                }
                buf.writeInt(-1);
            }


        });
        return buttons;
    }
}
