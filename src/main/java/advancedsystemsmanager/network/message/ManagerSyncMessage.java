package advancedsystemsmanager.network.message;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.ContainerManager;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class ManagerSyncMessage implements IMessage, IMessageHandler<ManagerSyncMessage, IMessage>
{
    ContainerManager container;
    boolean setLimitless;
    boolean limitless;
    boolean single;
    boolean add;
    int id;
    FlowComponent component;
    ByteBuf buf;

    public ManagerSyncMessage(ContainerManager container)
    {
        this(container, false, false);
    }

    public ManagerSyncMessage(ContainerManager container, int id, boolean add)
    {
        this(container, true, add);
        this.id = id;
    }

    public ManagerSyncMessage(ContainerManager container, FlowComponent command)
    {
        this(container, true, false);
        this.component = command;
    }

    private ManagerSyncMessage(ContainerManager container, boolean single, boolean add)
    {
        this.container = container;
        this.single = single;
        this.add = add;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        single = buf.readBoolean();
        this.buf = buf;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBoolean(single);
        buf.writeBoolean(setLimitless);
        if (setLimitless)
        {
            buf.writeBoolean(limitless);
            return;
        }
        if (single)
        {
            buf.writeBoolean(add);
            if (add)
            {
                buf.writeByte(id);
            } else
            {
                boolean remove = component == null;
                buf.writeBoolean(remove);
                if (remove)
                {
                    buf.writeInt(id);
                }else
                {
                    NBTTagCompound tagCompound = new NBTTagCompound();
                    component.writeToNBT(tagCompound, false);
                    ByteBufUtils.writeTag(buf, tagCompound);
                }
            }
        } else
        {

        }
        /*
        boolean single data
            true
                boolean add
                    true
                        byte type
                    false
                    boolean remove
                        true
                            int id
                        false
                            new command data
            false
                int commands
                for (Command : manager.commands)
                    write command data
         */
    }

    @Override
    public IMessage onMessage(ManagerSyncMessage message, MessageContext ctx)
    {
        return null;
    }
}
