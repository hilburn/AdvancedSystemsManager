package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.CopyHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.settings.Settings;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collection;
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
            public boolean readData(ASMPacket packet)
            {
                int idToRemove = packet.readVarIntFromBuffer();
                if (idToRemove != -1)
                {
                    manager.removeFlowComponent(idToRemove);
                    return true;
                }
                return false;
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
            public boolean writeData(ASMPacket packet)
            {
                manager.justSentServerComponentRemovalPacket = true;
                for (FlowComponent item : manager.getZLevelRenderingList())
                {
                    if (item.isBeingMoved())
                    {
                        packet.writeVarIntToBuffer(item.getId());
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
        buttons.add(new ManagerButton(manager, Names.COPY_COMMAND, 230 - IManagerButton.BUTTON_ICON_SIZE, IManagerButton.BUTTON_ICON_SIZE)
        {
            @Override
            public String getMouseOver()
            {
                return !Settings.isLimitless(manager) && manager.getFlowItems().size() >= 511 ? Names.MAXIMUM_COMPONENT_ERROR : super.getMouseOver();
            }

            @Override
            public boolean readData(ASMPacket packet)
            {
                if (Settings.isLimitless(manager) || manager.components.size() < 511)
                {
                    int id = packet.readVarIntFromBuffer();
                    if (id != -1)
                    {
                        FlowComponent item = manager.getFlowItem(id);
                        Collection<FlowComponent> added = CopyHelper.copyConnectionsWithChildren(manager, manager.getFlowItems(), item, Settings.isLimitless(manager));
                        for (FlowComponent add : added)
                        {
                            manager.addNewComponent(add);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean validClick()
            {
                for (FlowComponent item : manager.getZLevelRenderingList())
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
            public boolean writeData(ASMPacket packet)
            {
                for (FlowComponent item : manager.getZLevelRenderingList())
                {
                    if (item.isBeingMoved())
                    {
                        packet.writeVarIntToBuffer(item.getId());
                        return true;
                    }
                }
                return false;
            }


        });
        return buttons;
    }
}
