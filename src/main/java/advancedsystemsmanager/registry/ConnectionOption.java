package advancedsystemsmanager.registry;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.client.gui.theme.ThemeMouseover;
import advancedsystemsmanager.reference.Names;

public enum ConnectionOption
{
    STANDARD_INPUT(Names.CONNECTION_INPUT, ConnectionType.INPUT),
    STANDARD_OUTPUT(Names.CONNECTION_OUTPUT, ConnectionType.OUTPUT),
    INTERVAL(Names.CONNECTION_INTERVAL, ConnectionType.OUTPUT),
    REDSTONE_PULSE_HIGH(Names.CONNECTION_ON_HIGH_REDSTONE_PULSE, ConnectionType.OUTPUT),
    REDSTONE_PULSE_LOW(Names.CONNECTION_ON_LOW_REDSTONE_PULSE, ConnectionType.OUTPUT),
    REDSTONE_HIGH(Names.CONNECTION_WHILE_HIGH_REDSTONE, ConnectionType.OUTPUT),
    REDSTONE_LOW(Names.CONNECTION_WHILE_LOW_REDSTONE, ConnectionType.OUTPUT),
    CONDITION_TRUE(Names.CONNECTION_TRUE, ConnectionType.OUTPUT),
    CONDITION_FALSE(Names.CONNECTION_FALSE, ConnectionType.OUTPUT),
    FOR_EACH(Names.CONNECTION_FOR_EACH, ConnectionType.SIDE),
    BUD(Names.CONNECTION_ON_BLOCK_UPDATE, ConnectionType.OUTPUT),
    BUD_PULSE_HIGH(Names.CONNECTION_ON_HIGH_BLOCK_PULSE, ConnectionType.OUTPUT),
    BUD_HIGH(Names.CONNECTION_WHILE_HIGH_BLOCK, ConnectionType.OUTPUT),
    BUD_PULSE_LOW(Names.CONNECTION_ON_LOW_BLOCK_PULSE, ConnectionType.OUTPUT),
    BUD_LOW(Names.CONNECTION_WHILE_LOW_BLOCK, ConnectionType.OUTPUT),
    DELAY_OUTPUT(Names.CONNECTION_DELAY_OUTPUT, ConnectionType.OUTPUT),
    DYNAMIC_INPUT(null, ConnectionType.INPUT)
            {
                @Override
                public String getName(FlowComponent component, int id)
                {
                    return id < component.getChildrenInputNodes().size() ? component.getChildrenInputNodes().get(id).getName() : "";
                }

                @Override
                public boolean isValid(FlowComponent component, int id)
                {
                    return id < component.getChildrenInputNodes().size();
                }
            },
    DYNAMIC_OUTPUT(null, ConnectionType.OUTPUT)
            {
                @Override
                public String getName(FlowComponent component, int id)
                {
                    return id < component.getChildrenOutputNodes().size() ? component.getChildrenOutputNodes().get(id).getName() : "";
                }

                @Override
                public boolean isValid(FlowComponent component, int id)
                {
                    return id < component.getChildrenOutputNodes().size();
                }
            };
    public String name;
    public ConnectionType type;

    ConnectionOption(String name, ConnectionType type)
    {
        this.name = name;
        this.type = type;
    }

    public boolean isInput()
    {
        return type == ConnectionType.INPUT;
    }

    public boolean isOutput()
    {
        return type == ConnectionType.OUTPUT;
    }

    public ConnectionType getType()
    {
        return type;
    }


    public String getName(FlowComponent component, int id)
    {
        return name;
    }

    public boolean isValid(FlowComponent component, int id)
    {
        return name != null;
    }

    public int[] getColour(boolean selected)
    {
        return type.getColour(selected);
    }

    public enum ConnectionType
    {
        INPUT(165, 15)
                {
                    @Override
                    public ThemeMouseover getTheme()
                    {
                        return ThemeHandler.theme.commands.connections.input;
                    }
                },
        OUTPUT(169, 15)
                {
                    @Override
                    public ThemeMouseover getTheme()
                    {
                        return ThemeHandler.theme.commands.connections.output;
                    }
                },
        SIDE(165, 20)
                {
                    @Override
                    public ThemeMouseover getTheme()
                    {
                        return ThemeHandler.theme.commands.connections.side;
                    }
                };

        final int x, y;

        ConnectionType(int x, int y)
        {
            this.x = x;
            this.y = y;
        }

        public int[] getColour(boolean selected)
        {
            return (selected ? getTheme().mouseover : getTheme().colour).getColour();
        }

        public abstract ThemeMouseover getTheme();

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }
    }
}
