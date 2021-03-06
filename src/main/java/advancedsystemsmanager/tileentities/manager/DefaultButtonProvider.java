package advancedsystemsmanager.tileentities.manager;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.api.gui.IManagerButtonProvider;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.helpers.CopyHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.CommandRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultButtonProvider implements IManagerButtonProvider
{
    private static final int DELETE_POSITION_X = 18;
    private static final int DELETE_POSITION_Y = 126;
    private static final int COPY_POSITION_Y = 144;

    @Override
    public List<IManagerButton> getButtons(TileEntityManager manager)
    {
        List<IManagerButton> buttons = new ArrayList<IManagerButton>();
        for (ICommand command : CommandRegistry.getCommands())
        {
            if (command != null)
            {
                buttons.add(new ManagerButtonCreate(manager, command));
            }
        }
        buttons.add(new ManagerButton(manager, Names.DELETE_COMMAND, DELETE_POSITION_X, DELETE_POSITION_Y)
        {
            int moved;

            @Override
            public boolean readData(ASMPacket packet)
            {
                int idToRemove = packet.readVarIntFromBuffer();
                if (idToRemove != -1)
                {
                    manager.removeFlowComponent(idToRemove, packet.readBoolean());
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
                        moved = item.getId();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean writeData(ASMPacket packet)
            {
                manager.serverPacket = true;
                packet.writeVarIntToBuffer(moved);
                packet.writeBoolean(clicked == 2 && Settings.getSetting(Settings.MIDDLE_CLICK));
                return true;
            }

            @Override
            public boolean activateOnRelease()
            {
                return true;
            }
        });
        buttons.add(new ManagerButton(manager, Names.COPY_COMMAND, DELETE_POSITION_X, COPY_POSITION_Y)
        {
            int moved;

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
                        Collection<FlowComponent> added = CopyHelper.copyCommandsWithChildren(manager, item, Settings.isLimitless(manager), packet.readBoolean());
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
                        moved = item.getId();
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

                packet.writeVarIntToBuffer(moved);
                packet.writeBoolean(clicked == 2 && Settings.getSetting(Settings.MIDDLE_CLICK));
                return true;
            }


        });
        return buttons;
    }
}
