package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.elements.Variable;
import advancedsystemsmanager.flow.execution.Executor;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.flow.setting.Setting;
import advancedsystemsmanager.reference.Textures;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import advancedsystemsmanager.util.SystemCoord;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public abstract class CommandBase<Type> implements ICommand
{
    protected static final int BUTTON_SHEET_SIZE = 13;
    protected static final int TRIGGER = 0;
    protected static final int ITEM_INPUT = 1;
    protected static final int ITEM_OUTPUT = 2;
    protected static final int ITEM_CONDITION = 3;
    protected static final int FLOW_CONTROL = 4;
    protected static final int FLUID_INPUT = 5;
    protected static final int FLUID_OUTPUT = 6;
    protected static final int FLUID_CONDITION = 7;
    protected static final int REDSTONE_OUTPUT = 8;
    protected static final int REDSTONE_CONDITION = 9;
    protected static final int VARIABLE = 10;
    protected static final int FOR_EACH = 11;
    protected static final int AUTOCRAFTING = 12;
    protected static final int GROUP = 13;
    protected static final int GROUP_NODE = 14;
    protected static final int CAMOUFLAGE = 15;
    protected static final int SIGN = 16;
    protected static final int RF_INPUT = 17;
    protected static final int RF_OUTPUT = 18;
    protected static final int RF_CONDITION = 19;
    protected static final int ASPECT_INPUT = 22;
    protected static final int ASPECT_OUTPUT = 23;
    protected static final int ASPECT_CONDITION = 24;

    protected int id;
    protected String name;
    protected String longName;
    protected ConnectionSet[] connectionSets;
    protected CommandType type;
    protected EnumSet<ConnectionOption> outputs = EnumSet.allOf(ConnectionOption.class);
    protected int energyCost;
    protected int[] colour = new int[]{0xc6, 0xc6, 0xc6, 0xff};

    public CommandBase(int id, String name, CommandType type, ConnectionSet... connectionSets)
    {
        this.id = id;
        this.name = name;
        this.longName = name + "Long";
        this.connectionSets = connectionSets;
        this.type = type;
    }

    public static List<SystemCoord> getContainers(TileEntityManager manager, MenuContainer container)
    {
        return getContainers(manager, container.selectedInventories);
    }

    private static List<SystemCoord> getContainers(TileEntityManager manager, List<Long> selectedInventories)
    {
        List<SystemCoord> result = new ArrayList<SystemCoord>();
        for (Iterator<Long> itr = selectedInventories.iterator(); itr.hasNext(); )
        {
            long selected = itr.next();
            if (selected >= 0)
            {
                SystemCoord coord = manager.getInventory(selected);
                if (coord != null && coord.isValid())
                {
                    result.add(coord);
                } else
                {
//                    itr.remove();
                }
            } else
            {
                Variable variable = manager.getVariable((int)selected);
                if (variable != null)
                {
                    result.addAll(getContainers(manager, variable.getContainers()));
                } else
                {
//                    itr.remove();
                }
            }
        }
        return result;
    }

    public boolean isValidSetting(boolean whitelist, Setting<Type> setting)
    {
        return ((setting != null) == whitelist) || (setting != null && setting.isLimitedByAmount());
    }

    public List<Setting<Type>> getValidSettings(List<Setting<Type>> oldSettings)
    {
        List<Setting<Type>> result = new ArrayList<Setting<Type>>();
        for (Setting<Type> setting : oldSettings)
        {
            if (setting.isValid())
            {
                setting.resetCount();
                result.add(setting);
            }
        }
        return result;
    }

    @Override
    public int getId()
    {
        return id;
    }

    public Setting<Type> isValid(List<Setting<Type>> settings, Type check)
    {
        for (Iterator<Setting<Type>> itr = settings.iterator(); itr.hasNext(); )
        {
            Setting<Type> setting = itr.next();
            if (setting.isContentEqual(check))
            {
                if (!(setting.isLimitedByAmount() && setting.getAmountLeft() < 1))
                    return setting;
                else
                {
                    itr.remove();
                }
            }
        }
        return null;
    }

    @Override
    public ConnectionSet[] getSets()
    {
        return connectionSets;
    }



    @Override
    public void execute(FlowComponent command, int connectionId, Executor executor)
    {
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getLongName()
    {
        return longName;
    }

    @Override
    public CommandType getCommandType()
    {
        return type;
    }

    @Override
    public int getX()
    {
        return (getId() / BUTTON_SHEET_SIZE) * IManagerButton.BUTTON_ICON_SIZE;
    }

    @Override
    public int getY()
    {
        return (getId() % BUTTON_SHEET_SIZE) * IManagerButton.BUTTON_ICON_SIZE;
    }

    @Override
    public ResourceLocation getTexture()
    {
        return Textures.BUTTONS;
    }

    @Override
    public List<Connection> getActiveChildren(FlowComponent command, int connectionId)
    {
        List<Connection> connections = new ArrayList<Connection>();
        for (int i = 0; i < command.getConnectionSet().getConnections().length; ++i)
        {
            Connection connection = command.getConnection(i);
            ConnectionOption option = command.getConnectionSet().getConnections()[i];
            if (connection != null && !option.isInput() && outputs.contains(option))
            {
                connections.add(connection);
            }
        }
        return connections;
    }

    @Override
    public void moveComponent(FlowComponent component, FlowComponent oldParent, FlowComponent newParent)
    {
    }

    @Override
    public int getEnergyCost()
    {
        return energyCost;
    }

    @Override
    public void setEnergyCost(int energyCost)
    {
        this.energyCost = energyCost;
    }

    @Override
    public int[] getColour()
    {
        return colour;
    }

    @Override
    public void setColour(int[] colour)
    {
        this.colour = colour;
    }
}