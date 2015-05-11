package advancedsystemsmanager.network.message;

import advancedsystemsmanager.api.gui.IManagerButton;
import io.netty.buffer.ByteBuf;

public class ButtonMessage extends ContainerMessage
{
    int id;
    public ButtonMessage(IManagerButton managerButton, int id)
    {
        super(managerButton);
        this.id = id;
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeByte(id);
        super.toBytes(buf);
    }
}
