package advancedsystemsmanager.flow.execution.commands;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.api.gui.IManagerButton;
import advancedsystemsmanager.flow.Connection;
import advancedsystemsmanager.flow.FlowComponent;
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
import java.util.List;

public abstract class CommandBase<Type> implements ICommand
{
    protected static final int BUTTON_SHEET_SIZE = 20;
    protected static final int TRIGGER = 0;
    protected static final int ITEM_INPUT = 1;
    protected static final int ITEM_OUTPUT = 2;
    protected static final int ITEM_CONDITION = 3;
    protected static final int FLOW_CONTROL = 4;
    protected static final int FLUID_INPUT = 5;
    protected static final int FLUID_OUTPUT = 6;
    protected static final int FLUID_CONDITION = 7;

    protected int id;
    protected String name;
    protected String longName;
    protected ConnectionSet[] connectionSets;
    protected CommandType type;
    protected EnumSet<ConnectionOption> outputs = EnumSet.allOf(ConnectionOption.class);

    public CommandBase(int id, String name, CommandType type, ConnectionSet... connectionSets)
    {
        this.id = id;
        this.name = name;
        this.longName = name + "Long";
        this.connectionSets = connectionSets;
        this.type = type;
    }

    public List<SystemCoord> getContainers(TileEntityManager manager, MenuContainer container)
    {
        List<SystemCoord> result = new ArrayList<SystemCoord>();
        for (long selected : container.getSelectedInventories())
        {
            SystemCoord coord = manager.getInventory(selected);
            if (coord != null) result.add(coord);
        }
        return result;
    }

    @Override
    public int getId()
    {
        return id;
    }

    public List<Setting<Type>> getValidSettings(List<Setting<Type>> oldSettings)
    {
        List<Setting<Type>> result = new ArrayList<Setting<Type>>();
        for (Setting<Type> setting : oldSettings) if (setting.isValid()) result.add(setting);
        return result;
    }

    @Override
    public ConnectionSet[] getSets()
    {
        return connectionSets;
    }

    public Setting<Type> isValid(List<Setting<Type>> settings, Type check)
    {
        for (Setting<Type> setting : settings)
            if (setting.isContentEqual(check)) return setting;
        return null;
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
        return 230 - (getId() / BUTTON_SHEET_SIZE) * IManagerButton.BUTTON_ICON_SIZE;
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
    public List<Connection> getActiveChildren(FlowComponent command)
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


}
