package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.gui.IManagerButton;
import io.netty.buffer.ByteBuf;

public class ButtonMessage extends SyncMessage
{
    int id;

    public ButtonMessage(){}

    public ButtonMessage(IManagerButton managerButton, int id)
    {
        super(managerButton);
        this.id = id;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        if (this.buf == null)
        {
            buf.writeBoolean(false); //Not Sync All Data
            buf.writeBoolean(false); //Not handle settings
            buf.writeBoolean(false); //Not new component message
            buf.writeBoolean(false); //Not specific component
            buf.writeBoolean(false); //Not client only shit
            buf.writeByte(id);
        }
        super.toBytes(buf);
    }
}
