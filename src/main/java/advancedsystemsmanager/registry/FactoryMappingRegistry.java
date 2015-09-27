package advancedsystemsmanager.registry;

import advancedsystemsmanager.api.network.IPacketReader;
import advancedsystemsmanager.api.network.IPacketWriter;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.reference.Files;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class FactoryMappingRegistry implements IPacketReader, IPacketWriter
{
    public static FactoryMappingRegistry INSTANCE = new FactoryMappingRegistry();
    public static final TypeAdapter<FactoryMappingRegistry> TYPE_ADAPTER = new TypeAdapter<FactoryMappingRegistry>()
    {

        @Override
        public void write(JsonWriter out, FactoryMappingRegistry value) throws IOException
        {
            out.beginObject();
            TreeSet<Integer> values = new TreeSet<Integer>(value.factoryMapping.values());
            for (Integer v : values)
            {
                out.name(value.factoryMapping.inverse().get(v)).value(v);
            }
            out.endObject();
        }

        @Override
        public FactoryMappingRegistry read(JsonReader in) throws IOException
        {
            FactoryMappingRegistry result = new FactoryMappingRegistry();

            in.beginObject();
            while (in.peek() != JsonToken.END_OBJECT)
            {
                result.register(in.nextName(), in.nextInt());
            }
            in.endObject();
            return result;
        }
    };
    private static Gson GSON = new GsonBuilder().registerTypeAdapter(FactoryMappingRegistry.class, TYPE_ADAPTER).setPrettyPrinting()
                                                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE_WITH_SPACES).create();
    private BiMap<String, Integer> factoryMapping = HashBiMap.create();
    private List<String> toBeAssigned = new ArrayList<String>();

    public FactoryMappingRegistry()
    {
        toBeAssigned.addAll(ClusterRegistry.getKeys());
        register(ClusterRegistry.CABLE.getKey(), 0);
        register(ClusterRegistry.MANAGER.getKey(), 1);
    }

    private void register(String name, int val)
    {
        if (!factoryMapping.containsKey(name) && !factoryMapping.containsValue(val))
        {
            toBeAssigned.remove(name);
            factoryMapping.put(name, val);
        }
    }

    public static void load()
    {
        try
        {
            File file = new File(Files.WORLD_SAVE_DIR, "block_mapping.json");
            if (!file.exists()) file.createNewFile();
            JsonReader reader = new JsonReader(new FileReader(file));
            INSTANCE = GSON.fromJson(reader, FactoryMappingRegistry.class);
            reader.close();
            if (INSTANCE == null)
            {
                INSTANCE = new FactoryMappingRegistry();
            }
        } catch (IOException ignored)
        {
        }
        if (!INSTANCE.toBeAssigned.isEmpty())
        {
            INSTANCE.assignNewEntries();
            save();
        }
        BlockRegistry.registerTiles();
    }

    private void assignNewEntries()
    {
        List<String> assign = new ArrayList<String>(toBeAssigned);
        for (String key : assign)
        {
            Set<Integer> values = factoryMapping.values();
            int i = 0;
            while (values.contains(i)) i++;
            register(key, i);
        }
    }

    private static void save()
    {
        File file = new File(Files.WORLD_SAVE_DIR, "block_mapping.json");
        try
        {
            if (!file.exists())
                file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            GSON.toJson(FactoryMappingRegistry.INSTANCE, FactoryMappingRegistry.class, fileWriter);
            fileWriter.close();
        } catch (IOException ignored)
        {
        }
    }

    public int getId(String key)
    {
        return factoryMapping.get(key);
    }

    @Override
    public boolean readData(ASMPacket packet)
    {
        factoryMapping.clear();
        int length = packet.readUnsignedByte();
        for (int i = 0; i < length; i++)
        {
            register(packet.readStringFromBuffer(), packet.readUnsignedByte());
        }
        BlockRegistry.registerTiles();
        return false;
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        BlockRegistry.registerTiles();
        packet.writeByte(factoryMapping.size());
        for (Map.Entry<String, Integer> entry : factoryMapping.entrySet())
        {
            packet.writeStringToBuffer(entry.getKey());
            packet.writeByte(entry.getValue());
        }
        return false;
    }
}
