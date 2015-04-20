package advancedfactorymanager.network.message;

import advancedfactorymanager.AdvancedFactoryManager;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import advancedfactorymanager.threading.SearchItems;

public class SearchRegistryGenerateMessage implements IMessage, IMessageHandler<SearchRegistryGenerateMessage, IMessage>
{
    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    @Override
    public IMessage onMessage(SearchRegistryGenerateMessage message, MessageContext ctx)
    {
        if (ctx.side == Side.CLIENT && SearchItems.searchEntries.isEmpty())
        {
            long time = System.currentTimeMillis();
            SearchItems.setItems();
            AdvancedFactoryManager.log.info("Search database generated in " + (System.currentTimeMillis() - time) + "ms");
        }
        return null;
    }
}
