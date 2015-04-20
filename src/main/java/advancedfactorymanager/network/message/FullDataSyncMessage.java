package advancedfactorymanager.network.message;

import advancedfactorymanager.naming.NameData;
import advancedfactorymanager.naming.NameRegistry;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FullDataSyncMessage implements IMessage, IMessageHandler<FullDataSyncMessage, IMessage>
{
    Map<Integer, NameData> nameMapping;

    public FullDataSyncMessage()
    {
        this.nameMapping = new HashMap<Integer, NameData>();
    }

    public FullDataSyncMessage(Map<Integer, NameData> nameMapping)
    {
        this.nameMapping = nameMapping;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);
        int length = pb.readInt();
        try
        {
            for (int i = 0; i < length; i++)
            {
                int dim = pb.readInt();
                NBTTagCompound tagCompound = pb.readNBTTagCompoundFromBuffer();
                NameData nameData = new NameData();
                nameData.readFromNBT(tagCompound);
                nameMapping.put(dim, nameData);
            }
        } catch (IOException e)
        {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        PacketBuffer pb = new PacketBuffer(buf);
        pb.writeInt(nameMapping.entrySet().size());
        try
        {
            for (Map.Entry<Integer, NameData> entry : nameMapping.entrySet())
            {
                pb.writeInt(entry.getKey());
                NBTTagCompound tagCompound = new NBTTagCompound();
                entry.getValue().writeToNBT(tagCompound);
                pb.writeNBTTagCompoundToBuffer(tagCompound);
            }
        } catch (IOException e)
        {
            throw Throwables.propagate(e);
        }

    }

    @Override
    public IMessage onMessage(FullDataSyncMessage message, MessageContext ctx)
    {
        NameRegistry.setNameData(message.nameMapping);
        return null;
    }
}
