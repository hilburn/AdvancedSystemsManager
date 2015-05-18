package advancedsystemsmanager.network.message;

import advancedsystemsmanager.gui.ContainerBase;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.Iterator;

public class SecretMessage implements IMessage, IMessageHandler<SecretMessage, IMessage>
{
    int x, y, z;

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
    }

    @Override
    public IMessage onMessage(SecretMessage message, MessageContext ctx)
    {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        Container container = player.openContainer;
        if (container instanceof ContainerBase)
        {
            TileEntity te = ((ContainerBase)container).getTileEntity();
            World world = player.getEntityWorld();
            EntityCreeper creeper = new EntityCreeper(world);
            for (int attempts = 0; attempts < 4; attempts++)
            {
                setCoords(te, player, world);
                creeper.setLocationAndAngles(x + world.rand.nextDouble(), y, z + world.rand.nextDouble(), world.rand.nextFloat(), world.rand.nextFloat());
                if (!creeper.isEntityInsideOpaqueBlock())
                {
                    creeper.setAlwaysRenderNameTag(true);
                    creeper.setCustomNameTag("hilburn");

                    Iterator itr = creeper.tasks.taskEntries.iterator();
                    while (itr.hasNext())
                    {
                        Object ai = itr.next();
                        if (ai instanceof EntityAICreeperSwell || ai instanceof EntityAIAttackOnCollide) itr.remove();
                    }
                    world.spawnEntityInWorld(creeper);
                    if (world.rand.nextInt(10) == 0) creeper.getDataWatcher().updateObject(17, 1);
                    break;
                }
            }

        }
        return null;
    }

    private void setCoords(TileEntity te, EntityPlayer player, World world)
    {
        if (world == te.getWorldObj())
        {
            x = te.xCoord + (int)(4D - world.rand.nextDouble() * 8);
            y = te.yCoord + 1;
            z = te.zCoord + (int)(4D - world.rand.nextDouble() * 8);
        }else
        {
            x = (int)(player.posX + 4D - world.rand.nextDouble() * 8);
            z = (int)(player.posX + 4D - world.rand.nextDouble() * 8);
            y = (int)player.posY;
        }
        while (world.isAirBlock(x, y, z)) y--;
        while (!world.isAirBlock(x, y, z)) y++;
    }
}
