package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.*;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

//TODO: WIP command that treats the comparator input of any block as a redstone condition.
public class CommandComparatorCondition extends CommandBase
{
    public CommandComparatorCondition()
    {
        super(24, Names.COMPARATOR_CONDITION, CommandType.COMMAND_CONTROL, ConnectionSet.STANDARD_CONDITION);
    }

    @Override
    public void getMenus(FlowComponent component, List<Menu> menus)
    {
        menus.add(new MenuContainer(component, SystemTypeRegistry.COMPARATOR));
        menus.add(new MenuRedstoneSidesNodes(component));
        menus.add(new MenuRedstoneStrengthNodes(component));
    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        List<Connection> connections = new ArrayList<Connection>();
        int connection = isTriggerPowered(command, true) ? 1 : 2;
        if (command.getConnection(connection) != null) connections.add(command.getConnection(connection));
        return connections;
    }

    private boolean isTriggerPowered(FlowComponent command, boolean high)
    {
        List<SystemCoord> blocks = getContainers(command.getManager(), (MenuContainer)command.getMenus().get(0));
        MenuContainer menuContainer = (MenuContainer)command.getMenus().get(0);
        if (menuContainer.getOption() == 0)
        {
            int[] currentPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
            for (SystemCoord block : blocks)
            {
                for (int i = 0; i < currentPower.length; i++)
                {
                    currentPower[i] = Math.min(15, currentPower[i] + block.getComparatorOutput(i));
                }
            }

            return TileEntityManager.redstoneCondition.isTriggerPowered(command, currentPower, high);
        } else
        {
            boolean requiresAll = (menuContainer.getOption() == 1);
            for (SystemCoord block : blocks)
            {
                int[] currentPower = new int[ForgeDirection.VALID_DIRECTIONS.length];
                for (int i = 0; i < currentPower.length; i++)
                {
                    currentPower[i] = Math.min(15, currentPower[i] + block.getComparatorOutput(i));
                }

                if (TileEntityManager.redstoneCondition.isTriggerPowered(command, currentPower, high))
                {
                    if (!requiresAll)
                    {
                        return true;
                    }
                } else
                {
                    if (requiresAll)
                    {
                        return false;
                    }
                }
            }
            return requiresAll;
        }
    }
}
