package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.api.tileentities.IRedstoneEmitter;
import advancedsystemsmanager.flow.menus.MenuPulse;
import advancedsystemsmanager.flow.menus.MenuRedstoneOutput;
import advancedsystemsmanager.flow.menus.MenuRedstoneSidesEmitter;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import advancedsystemsmanager.util.SystemCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class TileEntityEmitter extends TileEntityClusterElement implements IPacketBlock, IRedstoneEmitter
{
    private static final String NBT_SIDES = "Sides";
    private static final String NBT_STRENGTH = "Strength";
    private static final String NBT_STRONG = "Strong";
    private static final String NBT_TICK = "Tick";
    private static final String NBT_PULSES = "Pulses";
    private static final int UPDATE_BUFFER_DISTANCE = 5;
    private int[] strengths;
    private boolean[] strong;
    private int[] updatedStrength;
    private boolean[] updatedStrong;
    private List<PulseTimer>[] pulseTimers;
    private boolean hasUpdatedThisTick;
    private List<SystemCoord> scheduledToUpdate = new ArrayList<SystemCoord>();
    private boolean hasUpdatedData;


    public TileEntityEmitter()
    {

        strengths = new int[ForgeDirection.VALID_DIRECTIONS.length];
        strong = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

        updatedStrength = new int[ForgeDirection.VALID_DIRECTIONS.length];
        updatedStrong = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

        pulseTimers = new List[ForgeDirection.VALID_DIRECTIONS.length];
        for (int i = 0; i < pulseTimers.length; i++)
        {
            pulseTimers[i] = new ArrayList<PulseTimer>();
        }
    }

    public boolean hasStrongSignalAtSide(int side)
    {
        return strong[side];
    }

    public boolean hasStrongSignalAtOppositeSide(int side)
    {
        return strong[getOpposite(side)];
    }

    private int getOpposite(int side)
    {
        return ForgeDirection.getOrientation(side).getOpposite().ordinal();
    }

    public int getStrengthFromOppositeSide(int side)
    {
        return getStrengthFromSide(getOpposite(side));
    }

    public int getStrengthFromSide(int side)
    {
        return strengths[side];
    }

    public void updateState(MenuRedstoneSidesEmitter sides, MenuRedstoneOutput output, MenuPulse pulse)
    {
        boolean updateClient = false;
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
            if (sides.isSideRequired(i))
            {
                int oldStrength = updatedStrength[i];
                boolean oldStrong = updatedStrong[i];

                updateSideState(i, output);
                updatedStrong[i] = sides.useStrongSignal();


                /*if (((updatedStrength[i] > 0) != (oldStrength > 0)) || (oldStrong != updatedStrong[i])) {
                    updateClient = true;
                }*/
                boolean updateBlocks = oldStrength != updatedStrength[i] || oldStrong != updatedStrong[i];
                if (updateBlocks)
                {
                    updateClient = true;
                }


                if (updateBlocks)
                {
                    addBlockScheduledForUpdate(i);
                }

                if (pulse.shouldEmitPulse())
                {
                    PulseTimer timer = new PulseTimer(oldStrength, oldStrong, pulse.getPulseTime() + 1); //add one to counter the first tick (which is the same tick as we add it)
                    List<PulseTimer> timers = pulseTimers[i];

                    if (timers.size() < 200)
                    { //to block a huge amount of pulses at the same time
                        switch (pulse.getSelectedPulseOverride())
                        {
                            case EXTEND_OLD:
                                if (timers.size() > 0)
                                {
                                    if (timers.size() > 1)
                                    {
                                        PulseTimer temp = timers.get(0);
                                        timers.clear();
                                        timers.add(temp);
                                    }

                                    PulseTimer oldTimer = timers.get(0);
                                    oldTimer.ticks = Math.max(oldTimer.ticks, timer.ticks);
                                } else
                                {
                                    timers.add(timer);
                                }
                                break;
                            case KEEP_ALL:
                                timers.add(timer);
                                break;
                            case KEEP_NEW:
                                timers.clear();
                                timers.add(timer);
                                break;
                            case KEEP_OLD:
                                if (timers.isEmpty())
                                {
                                    timers.add(timer);
                                }
                        }
                    }
                }


            }
        }

        if (updateClient)
        {
            sendPacketToClient(CLIENT_SYNC);
        }
    }

    private void updateSideState(int side, MenuRedstoneOutput output)
    {
        int strength = updatedStrength[side];
        int selectedStrength = output.getSelectedStrength();

        switch (output.getSelectedSetting())
        {
            case FIXED:
                strength = selectedStrength;
                break;
            case TOGGLE:
                strength = strength > 0 ? 0 : 15;
                break;
            case MAX:
                strength = Math.max(strength, selectedStrength);
                break;
            case MIN:
                strength = Math.min(strength, selectedStrength);
                break;
            case INCREASE:
                strength = Math.min(15, strength + selectedStrength);
                break;
            case DECREASE:
                strength = Math.max(0, strength - selectedStrength);
                break;
            case FORWARD:
                strength = (strength + selectedStrength) % 16;
                break;
            case BACKWARD:
                strength -= selectedStrength;
                if (strength < 0) strength += 16;
                break;
        }


        updatedStrength[side] = strength;
    }

    private void addBlockScheduledForUpdate(int side)
    {
        hasUpdatedThisTick = true;
        ForgeDirection direction = ForgeDirection.getOrientation(side);
        int x = xCoord + direction.offsetX;
        int y = yCoord + direction.offsetY;
        int z = zCoord + direction.offsetZ;

        SystemCoord coordinate = new SystemCoord(x, y, z, this.worldObj);
        if (!scheduledToUpdate.contains(coordinate))
        {
            scheduledToUpdate.add(coordinate);
        }
    }

    private void notifyUpdate(int x, int y, int z, boolean spread)
    {
        if (worldObj.getBlock(x, y, z) != BlockRegistry.cable && (x != xCoord || y != yCoord || z != zCoord))
        {
            worldObj.notifyBlockOfNeighborChange(x, y, z, getBlockType());

            if (spread)
            {
                notifyUpdate(x - 1, y, z, false);
                notifyUpdate(x + 1, y, z, false);
                notifyUpdate(x, y - 1, z, false);
                notifyUpdate(x, y + 1, z, false);
                notifyUpdate(x, y, z - 1, false);
                notifyUpdate(x, y, z + 1, false);
            }
        }
    }

    @Override
    public void writeToTileNBT(NBTTagCompound nbtTagCompound)
    {
        NBTTagList sidesTag = new NBTTagList();
        for (int i = 0; i < strengths.length; i++)
        {
            NBTTagCompound sideTag = new NBTTagCompound();

            sideTag.setByte(NBT_STRENGTH, (byte)updatedStrength[i]);
            sideTag.setBoolean(NBT_STRONG, updatedStrong[i]);

            NBTTagList pulsesTag = new NBTTagList();
            List<PulseTimer> timers = pulseTimers[i];

            for (PulseTimer timer : timers)
            {
                NBTTagCompound pulseTag = new NBTTagCompound();
                pulseTag.setByte(NBT_STRENGTH, (byte)timer.strength);
                pulseTag.setBoolean(NBT_STRONG, timer.strong);
                pulseTag.setShort(NBT_TICK, (short)timer.ticks);
                pulsesTag.appendTag(pulseTag);
            }
            sideTag.setTag(NBT_PULSES, pulsesTag);

            sidesTag.appendTag(sideTag);
        }


        nbtTagCompound.setTag(NBT_SIDES, sidesTag);
    }

    @Override
    public void readFromTileNBT(NBTTagCompound nbtTagCompound)
    {

        NBTTagList sidesTag = nbtTagCompound.getTagList(NBT_SIDES, 10);
        for (int i = 0; i < sidesTag.tagCount(); i++)
        {

            NBTTagCompound sideTag = sidesTag.getCompoundTagAt(i);

            strengths[i] = updatedStrength[i] = sideTag.getByte(NBT_STRENGTH);
            strong[i] = updatedStrong[i] = sideTag.getBoolean(NBT_STRONG);

            List<PulseTimer> timers = pulseTimers[i];
            timers.clear();
            NBTTagList pulsesTag = sideTag.getTagList(NBT_PULSES, 10);
            for (int j = 0; j < pulsesTag.tagCount(); j++)
            {
                NBTTagCompound pulseTag = pulsesTag.getCompoundTagAt(j);

                timers.add(new PulseTimer(pulseTag.getByte(NBT_STRENGTH), pulseTag.getBoolean(NBT_STRONG), pulseTag.getShort(NBT_TICK)));
            }
        }
    }

    @Override
    public void writeClientSyncData(ASMPacket packet)
    {
        super.writeClientSyncData(packet);
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
            packet.writeByte(updatedStrength[i] | (updatedStrong[i] ? 1 << 5 : 0));
        }
    }

    @Override
    public void readClientSyncData(ASMPacket packet)
    {
        super.readClientSyncData(packet);
        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
            byte val = packet.readByte();
            strengths[i] = val & 0xF;
            strong[i] = (val >> 5) > 0;
        }
    }

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote)
        {
            updatePulses();

            if (hasUpdatedThisTick)
            {
                hasUpdatedThisTick = false;
                List<SystemCoord> coordinates = new ArrayList<SystemCoord>(scheduledToUpdate);
                scheduledToUpdate.clear();
                for (int i = 0; i < strengths.length; i++)
                {
                    strengths[i] = updatedStrength[i];
                    strong[i] = updatedStrong[i];
                }
                for (SystemCoord coordinate : coordinates)
                {
                    notifyUpdate(coordinate.getX(), coordinate.getY(), coordinate.getZ(), true);
                }
            }
        }
    }

    private void updatePulses()
    {
        boolean updateClient = false;

        for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
        {
            Iterator<PulseTimer> iterator = pulseTimers[i].iterator();

            while (iterator.hasNext())
            {
                PulseTimer timer = iterator.next();
                timer.ticks--;
                if (timer.ticks == 0)
                {
                    if (updatedStrength[i] != timer.strength || updatedStrong[i] == timer.strong)
                    {
                        updatedStrength[i] = timer.strength;
                        updatedStrong[i] = timer.strong;
                        addBlockScheduledForUpdate(i);
                        updateClient = true;
                    }
                    iterator.remove();
                }
            }
        }

        if (updateClient)
        {
            sendPacketToClient(CLIENT_SYNC);
        }
    }

    @Override
    public int[] getPower()
    {
        return updatedStrength;
    }

    private class PulseTimer
    {
        private int strength;
        private boolean strong;
        private int ticks;

        private PulseTimer(int strength, boolean strong, int ticks)
        {
            this.strength = strength;
            this.strong = strong;
            this.ticks = ticks;
        }
    }
}
