package advancedsystemsmanager.gui.theme;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.registry.CommandRegistry;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThemeAdapters
{
    public static final TypeAdapter<HexValue> HEX_ADAPTER = new TypeAdapter<HexValue>()
    {
        @Override
        public void write(JsonWriter out, HexValue value) throws IOException
        {
            out.value(value == null? "#00000000" : value.getHexValue());
        }

        @Override
        public HexValue read(JsonReader in) throws IOException
        {
            if (in.peek() == JsonToken.NULL) return new HexValue();
            return new HexValue(in.nextString());
        }
    };
    public static final TypeAdapter<ThemeCommand.CommandSet> COMMAND_ADAPTER = new TypeAdapter<ThemeCommand.CommandSet>()
    {
        @Override
        public void write(JsonWriter out, ThemeCommand.CommandSet value) throws IOException
        {
            out.beginObject();
            for (ICommand command : CommandRegistry.getCommands())
            {
                out.name(LocalizationHelper.translate(command.getName())).value(HexValue.getHexString(command.getColour()));
            }
            out.endObject();
        }

        @Override
        public ThemeCommand.CommandSet read(JsonReader in) throws IOException
        {
            Map<String, HexValue> input = new HashMap<String, HexValue>();
            in.beginObject();
            while (in.peek() != JsonToken.END_OBJECT)
            {
                String name = in.nextName();
                String hex = in.nextString();
                input.put(name, new HexValue(hex));
            }
            in.endObject();
            for (ICommand command : CommandRegistry.getCommands())
            {
                String name = LocalizationHelper.translate(command.getName());
                if (input.containsKey(name))
                {
                    command.setColour(input.get(name).getColour());
                }
            }
            return new ThemeCommand.CommandSet();
        }
    };
}
