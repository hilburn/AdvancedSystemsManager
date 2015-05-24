package advancedsystemsmanager.network;


import advancedsystemsmanager.gui.ContainerBase;
import advancedsystemsmanager.reference.Reference;
import advancedsystemsmanager.settings.Settings;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static advancedsystemsmanager.AdvancedSystemsManager.packetHandler;
import static advancedsystemsmanager.reference.Reference.ID;

public class ASMPacket extends PacketBuffer
{
    List<EntityPlayerMP> players;

    public ASMPacket()
    {
        super(Unpooled.buffer(10));
    }

    public ASMPacket(int size)
    {
        super(Unpooled.buffer(size));
    }

    public ASMPacket(ByteBuf buffer)
    {
        super(buffer);
    }

    public ASMPacket copy()
    {
        return new ASMPacket(super.copy());
    }

    public void setPlayer(EntityPlayerMP player)
    {
        if (players == null) players = new ArrayList<EntityPlayerMP>();
        players.add(player);
    }

    public void setPlayers(List<EntityPlayerMP> players)
    {
        this.players = players;
    }

    public List<EntityPlayerMP> getPlayers()
    {
        return this.players;
    }

    public void sendResponse()
    {
        FMLProxyPacket packet = getPacket();
        for (EntityPlayerMP player : players)
        {
            packetHandler.sendTo(packet, player);
        }
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

    public FMLProxyPacket getPacket()
    {
        return new FMLProxyPacket(this, Reference.ID);
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
    public void writeItemStackToBuffer(ItemStack stack)
    {
        try
        {
            super.writeItemStackToBuffer(stack);
        } catch (IOException ignored)
        {
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
    public void writeStringToBuffer(String string)
    {
        try
        {
            super.writeStringToBuffer(string);
        } catch (IOException ignored)
        {
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

    public void writeBooleanArray(boolean[] val)
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
        for (int i = 1; i<= size; i++)
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
}
