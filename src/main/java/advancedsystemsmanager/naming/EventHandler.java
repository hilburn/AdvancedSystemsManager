package advancedsystemsmanager.naming;

import advancedsystemsmanager.api.items.ILeftClickItem;
import advancedsystemsmanager.items.ItemDuplicator;
import advancedsystemsmanager.items.ItemLabeler;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ItemRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class EventHandler
{
    @SubscribeEvent
    public void playerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            NameRegistry.syncNameData((EntityPlayerMP)event.player);
            PacketHandler.sendLogonMessage((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event)
    {
        NameRegistry.removeName(event.world, event.x, event.y, event.z);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event)
    {
        ItemStack stack = event.entityPlayer.getCurrentEquippedItem();
        if (event.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK && stack != null)
        {
            if (stack.getItem() instanceof ILeftClickItem)
            {
                if (((ILeftClickItem) stack.getItem()).leftClick(event.entityPlayer, stack, event.world, event.x, event.y, event.z, event.face))
                {
                    event.setCanceled(true);
                }
            }
        }
    }
}
