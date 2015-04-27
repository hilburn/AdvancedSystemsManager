package advancedsystemsmanager.network.message;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;

public class LabelSyncMessage implements IMessage, IMessageHandler<LabelSyncMessage, IMessage>
{
    ItemStack stack;
    int id;

    public LabelSyncMessage()
    {
    }

    public LabelSyncMessage(ItemStack stack, EntityPlayer player)
    {
        this.stack = stack;
        this.id = player.getEntityId();
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.stack = ByteBufUtils.readItemStack(buf);
        id = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(id);
    }

    @Override
    public IMessage onMessage(LabelSyncMessage message, MessageContext ctx)
    {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player != null)
        {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, message.stack);
        }
        return null;
    }
}
