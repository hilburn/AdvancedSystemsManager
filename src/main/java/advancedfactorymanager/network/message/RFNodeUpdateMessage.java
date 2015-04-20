package advancedfactorymanager.network.message;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import advancedfactorymanager.network.MessageHelper;
import advancedfactorymanager.tileentities.TileEntityRFNode;

public class RFNodeUpdateMessage implements IMessage, IMessageHandler<RFNodeUpdateMessage, IMessage>
{
    public int posX, posY, posZ;
    public byte input, output;

    public RFNodeUpdateMessage()
    {
    }

    public RFNodeUpdateMessage(TileEntityRFNode rfNode)
    {
        this.posX = rfNode.xCoord;
        this.posY = rfNode.yCoord;
        this.posZ = rfNode.zCoord;
        this.output = MessageHelper.booleanToByte(rfNode.getOutputs());
        this.input = MessageHelper.booleanToByte(rfNode.getInputs());
    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.posX = buf.readInt();
        this.posY = buf.readInt();
        this.posZ = buf.readInt();
        this.input = buf.readByte();
        this.output = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.posX);
        buf.writeInt(this.posY);
        buf.writeInt(this.posZ);
        buf.writeByte(this.input);
        buf.writeByte(this.output);

    }

    @Override
    public IMessage onMessage(RFNodeUpdateMessage message, MessageContext ctx)
    {
        TileEntity tileEntity = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.posX, message.posY, message.posZ);
        if (tileEntity instanceof TileEntityRFNode)
        {
            ((TileEntityRFNode)tileEntity).setInputSides(MessageHelper.byteToBooleanArray(message.input));
            ((TileEntityRFNode)tileEntity).setOutputSides(MessageHelper.byteToBooleanArray(message.output));
            FMLClientHandler.instance().getClient().theWorld.markBlockRangeForRenderUpdate(message.posX, message.posY, message.posZ, message.posX, message.posY, message.posZ);
        }
        return null;

    }
}
