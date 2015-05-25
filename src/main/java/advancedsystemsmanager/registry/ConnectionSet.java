package advancedsystemsmanager.registry;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.reference.Names;

public enum ConnectionSet
{
    STANDARD(Names.CONNECTION_SET_STANDARD, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    CONTINUOUSLY(Names.CONNECTION_SET_INTERVAL, ConnectionOption.INTERVAL),
    REDSTONE(Names.CONNECTION_SET_REDSTONE, ConnectionOption.REDSTONE_PULSE_HIGH, ConnectionOption.REDSTONE_HIGH, ConnectionOption.REDSTONE_LOW, ConnectionOption.REDSTONE_PULSE_LOW),
    STANDARD_CONDITION(Names.CONNECTION_SET_CONDITION, ConnectionOption.STANDARD_INPUT, ConnectionOption.CONDITION_TRUE, ConnectionOption.CONDITION_FALSE),
    MULTIPLE_INPUT_2(Names.CONNECTION_SET_COLLECTOR_2, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    MULTIPLE_INPUT_5(Names.CONNECTION_SET_COLLECTOR_5, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT),
    MULTIPLE_OUTPUT_2(Names.CONNECTION_SET_SPLIT_2, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT),
    MULTIPLE_OUTPUT_5(Names.CONNECTION_SET_SPLIT_5, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT, ConnectionOption.STANDARD_OUTPUT),
    EMPTY(Names.CONNECTION_SET_DECLARATION),
    FOR_EACH(Names.CONNECTION_SET_FOR_EACH, ConnectionOption.STANDARD_INPUT, ConnectionOption.FOR_EACH, ConnectionOption.STANDARD_OUTPUT),
    BUD(Names.CONNECTION_SET_BUD, ConnectionOption.BUD_PULSE_HIGH, ConnectionOption.BUD_HIGH, ConnectionOption.BUD, ConnectionOption.BUD_LOW, ConnectionOption.BUD_PULSE_LOW),
    OUTPUT_NODE(Names.CONNECTION_SET_OUTPUT_NODE, ConnectionOption.STANDARD_INPUT),
    INPUT_NODE(Names.CONNECTION_SET_INPUT_NODE, ConnectionOption.STANDARD_OUTPUT),
    DYNAMIC(Names.CONNECTION_SET_DYNAMIC, ConnectionOption.DYNAMIC_INPUT, ConnectionOption.DYNAMIC_INPUT, ConnectionOption.DYNAMIC_INPUT, ConnectionOption.DYNAMIC_INPUT, ConnectionOption.DYNAMIC_INPUT, ConnectionOption.DYNAMIC_OUTPUT, ConnectionOption.DYNAMIC_OUTPUT, ConnectionOption.DYNAMIC_OUTPUT, ConnectionOption.DYNAMIC_OUTPUT, ConnectionOption.DYNAMIC_OUTPUT)
            {
                @Override
                public int getInputCount(FlowComponent component)
                {
                    return component.childrenInputNodes.size();
                }

                @Override
                public int getOutputCount(FlowComponent component)
                {
                    return component.childrenOutputNodes.size();
                }
            },
    CHAT(Names.CONNECTION_SET_CHAT, ConnectionOption.STANDARD_OUTPUT),
    DELAYED(Names.CONNECTION_DELAY_OUTPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.STANDARD_OUTPUT);


    public ConnectionOption[] connections;
    public int outputCount;
    public int inputCount;
    public int sideCount;
    public String name;


    ConnectionSet(String name, ConnectionOption... connections)
    {
        this.connections = connections;

        for (ConnectionOption connection : connections)
        {
            if (connection.isInput())
            {
                inputCount++;
            } else if (connection.isOutput())
            {
                outputCount++;
            } else
            {
                sideCount++;
            }
        }

        this.name = name;
    }


    public ConnectionOption[] getConnections()
    {
        return connections;
    }

    public int getOutputCount(FlowComponent component)
    {
        return outputCount;
    }

    public int getInputCount(FlowComponent component)
    {
        return inputCount;
    }

    public int getSideCount()
    {
        return sideCount;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public String getName()
    {
        return name;
    }
}
