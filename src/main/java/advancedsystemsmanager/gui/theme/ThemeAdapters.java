package advancedsystemsmanager.gui.theme;

import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.ThemeHandler;
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
            out.value(value.getHexValue());
        }

        @Override
        public HexValue read(JsonReader in) throws IOException
        {
            return new HexValue(in.nextString());
        }
    }.nullSafe();
    public static final TypeAdapter<ThemeCommand.CommandSet> COMMAND_ADAPTER = new TypeAdapter<ThemeCommand.CommandSet>()
    {
        @Override
        public void write(JsonWriter out, ThemeCommand.CommandSet value) throws IOException
        {
            out.beginObject();
            for (ICommand command : CommandRegistry.getComponents())
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
            for (ICommand command : CommandRegistry.getComponents())
            {
                String name = LocalizationHelper.translate(command.getName());
                if (input.containsKey(name))
                {
                    command.setColour(input.get(name).getColour());
                } else
                {
                    command.setColour(ThemeHandler.theme.commands.baseColor.getColour());
                }
            }
            return new ThemeCommand.CommandSet();
        }
    };
}
