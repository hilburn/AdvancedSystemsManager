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
                public int[] getConnectionLocation(int connection, FlowComponent component)
                {
                    boolean input = connection < 5;
                    int id = connection;
                    if (!input) id -=5;
                    if (!connections[connection].isValid(component, id))
                        return null;

                    int size = (input ? component.childrenInputNodes : component.childrenOutputNodes).size() + 1;
                    id ++;
                    int offsetX = -FlowComponent.CONNECTION_SIZE_W/2;
                    return new int[]{component.getX() + Math.round((float)id/size * component.getComponentWidth() + offsetX), component.getY() +  (input ? -FlowComponent.CONNECTION_SIZE_H : component.getComponentHeight())};
                }
            },
    CHAT(Names.CONNECTION_SET_CHAT, ConnectionOption.STANDARD_OUTPUT),
    DELAYED(Names.CONNECTION_DELAY_OUTPUT, ConnectionOption.STANDARD_INPUT, ConnectionOption.DELAY_OUTPUT);

    public ConnectionOption[] connections;
    private ConnectionLocation[] connectionLocations;

    public String name;


    ConnectionSet(String name, ConnectionOption... connections)
    {
        this.connections = connections;

        int totalInput = 1;
        int totalOutput = 1;
        int totalSide = 1;
        for (ConnectionOption connection : connections)
        {
            switch (connection.getType())
            {
                case INPUT:
                    totalInput++;
                    break;
                case OUTPUT:
                    totalOutput++;
                    break;
                default:
                    totalSide++;
            }
        }
        int outputCount = 0;
        int inputCount = 0;
        int sideCount = 0;
        connectionLocations = new ConnectionLocation[connections.length];
        for (int i = 0; i < connections.length; i++)
        {
            ConnectionOption connection = connections[i];
            int offsetX, offsetY;
            float x, y;
            switch (connection.getType())
            {
                case INPUT:
                    inputCount++;
                    x = (float)inputCount/totalInput;
                    y = 0.0f;
                    offsetX = -FlowComponent.CONNECTION_SIZE_W/2;
                    offsetY = -FlowComponent.CONNECTION_SIZE_H;
                    break;
                case OUTPUT:
                    outputCount++;
                    x = (float)outputCount/totalOutput;
                    y = 1.0f;
                    offsetX = -FlowComponent.CONNECTION_SIZE_W/2;
                    offsetY = 0;
                    break;
                default:
                    sideCount++;
                    x = 1.0f;
                    y = (float)sideCount/totalSide;
                    offsetX = 0;
                    offsetY = -FlowComponent.CONNECTION_SIZE_W/2;
            }
            connectionLocations[i] = new ConnectionLocation(x, y, offsetX, offsetY);
        }

        this.name = name;
    }

    public int[] getConnectionLocation(int connection, FlowComponent component)
    {
        return connectionLocations[connection].getLocation(component.getX(), component.getY(), component.getComponentWidth(), component.getComponentHeight());
    }

    public ConnectionOption[] getConnections()
    {
        return connections;
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

    public boolean isInput(int i)
    {
        return connections[i].isInput();
    }

    private static class ConnectionLocation
    {
        float x, y;
        int offsetX, offsetY;

        public ConnectionLocation(float x, float y, int offsetX, int offsetY)
        {
            this.x = x;
            this.y = y;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public int[] getLocation(int x, int y, int w, int h)
        {
            return new int[]{x + Math.round(this.x * w) + offsetX, y + Math.round(this.y * h) + offsetY};
        }
    }
}
