package advancedsystemsmanager.network;

import advancedsystemsmanager.containers.ContainerBase;
import advancedsystemsmanager.reference.Reference;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static advancedsystemsmanager.AdvancedSystemsManager.packetHandler;

public class ASMPacket extends PacketBuffer
{
    List<EntityPlayerMP> players;
    Side side;

    public ASMPacket()
    {
        super(Unpooled.buffer(10));
    }

    public ASMPacket(int size)
    {
        super(Unpooled.buffer(size));
    }

    public ASMPacket(ByteBuf buffer, Side side)
    {
        super(buffer);
        this.side = side;
    }

    public Side getSide()
    {
        return side;
    }

    public void setPlayer(EntityPlayerMP player)
    {
        if (players == null) players = new ArrayList<EntityPlayerMP>();
        players.add(player);
    }

    public List<EntityPlayerMP> getPlayers()
    {
        return this.players;
    }

    public void setPlayers(List<EntityPlayerMP> players)
    {
        this.players = players;
    }

    public void sendResponse()
    {
        FMLProxyPacket packet = getPacket();
        for (EntityPlayerMP player : players)
        {
            packetHandler.sendTo(packet, player);
        }
    }

    public FMLProxyPacket getPacket()
    {
        return new FMLProxyPacket(this, Reference.NETWORK_ID);
    }

    public void sendPlayerPackets(double x, double y, double z, double r, int dimension)
    {
        packetHandler.sendToAllAround(getPacket(), new TargetPoint(dimension, x, y, z, r));
    }

    public void sendPlayerPacket(EntityPlayerMP player)
    {
        packetHandler.sendTo(getPacket(), player);
    }

    public void sendServerPacket()
    {
        packetHandler.sendToServer(getPacket());
    }

    public void sendPlayerPackets(boolean reply, ContainerBase container)
    {
        FMLProxyPacket packet = getPacket();
        for (Object crafting : container.getCrafters())
        {
            if (crafting instanceof EntityPlayerMP && (players == null || players.contains(crafting) == reply))
            {
                packetHandler.sendTo(packet, (EntityPlayerMP)crafting);
            }
        }
    }

    public void writeNBTTagCompoundToBuffer(NBTTagCompound tag)
    {
        try
        {
            super.writeNBTTagCompoundToBuffer(tag);
        } catch (IOException ignored)
        {
        }
    }

    public NBTTagCompound readNBTTagCompoundFromBuffer()
    {
        try
        {
            return super.readNBTTagCompoundFromBuffer();
        } catch (IOException e)
        {
            return null;
        }
    }

    @Override
    public void writeItemStackToBuffer(ItemStack stack)
    {
        try
        {
            super.writeItemStackToBuffer(stack);
        } catch (IOException ignored)
        {
        }
    }

    @Override
    public ItemStack readItemStackFromBuffer()
    {
        try
        {
            return super.readItemStackFromBuffer();
        } catch (IOException e)
        {
            return null;
        }
    }

    @Override
    public String readStringFromBuffer(int maxLength)
    {
        try
        {
            return super.readStringFromBuffer(maxLength);
        } catch (IOException e)
        {
            return null;
        }
    }

    @Override
    public void writeStringToBuffer(String string)
    {
        try
        {
            super.writeStringToBuffer(string);
        } catch (IOException ignored)
        {
        }
    }

    public ASMPacket copy()
    {
        return new ASMPacket(super.copy(), this.side);
    }

    public void writeBooleanArray(boolean... val)
    {
        int b = 0;
        for (boolean aVal : val)
        {
            b <<= 1;
            if (aVal) b |= 1;
        }
        writeVarIntToBuffer(b);
    }

    public boolean[] readBooleanArray(int size)
    {
        boolean[] result = new boolean[size];
        int b = readVarIntFromBuffer();
        for (int i = 1; i <= size; i++)
        {
            if ((b & 1) != 0) result[size - i] = true;
            b >>= 1;
        }
        return result;
    }

    public String readStringFromBuffer()
    {
        return readStringFromBuffer(32);
    }

    public void writeUUID(UUID id)
    {
        this.writeLong(id.getMostSignificantBits());
        this.writeLong(id.getLeastSignificantBits());
    }

    public UUID readUUID()
    {
        return new UUID(this.readLong(), this.readLong());
    }
}
