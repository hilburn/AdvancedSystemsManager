package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.gui.IManagerButton;
import io.netty.buffer.ByteBuf;

public class ButtonMessage extends SyncMessage
{
    int id;

    public ButtonMessage()
    {
    }

    public ButtonMessage(IManagerButton managerButton, int id)
    {
        super(managerButton);
        this.id = id;
    }

    @Override
    public int getID()
    {
        return 5;
    }

    @Override
    public void writeExtraData(ByteBuf buf)
    {
        buf.writeByte(id);
    }
}
