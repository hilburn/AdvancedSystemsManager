package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.tileentities.ITileInterfaceProvider;
import advancedsystemsmanager.api.tileentities.IActivateListener;
import advancedsystemsmanager.containers.ContainerRelay;
import advancedsystemsmanager.client.gui.GuiRelay;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.util.UserPermission;
import advancedsystemsmanager.util.wrappers.InventoryWrapper;
import advancedsystemsmanager.util.wrappers.InventoryWrapperHorse;
import advancedsystemsmanager.util.wrappers.InventoryWrapperPlayer;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class TileEntityRelay extends TileEntityElementRotation implements IInventory, ISidedInventory, IFluidHandler, ITileInterfaceProvider, IActivateListener
{

    private static final int MAX_CHAIN_LENGTH = 512;
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_UUID = "UUID";
    private static final String NBT_CREATIVE = "Creative";
    private static final String NBT_LIST = "ShowList";
    private static final String NBT_PERMISSIONS = "Permissions";
    private static final String NBT_NAME = "Name";
    private static final String NBT_ACTIVE = "Active";
    private static final String NBT_EDITOR = "Editor";
    public static int PERMISSION_MAX_LENGTH = 255;
    private int[] cachedAllSlots;
    private boolean blockingUsage;
    private int chainLength;
    private Entity[] cachedEntities = new Entity[2];
    private InventoryWrapper cachedInventoryWrapper;
    //used by the advanced version
    private List<UserPermission> permissions = new ArrayList<UserPermission>();
    private boolean doesListRequireOp = false;
    private GameProfile owner;
    private boolean creativeMode;

    public String getOwnerName()
    {
        return owner == null ? "Unknown" : owner.getName();
    }

    public GameProfile getOwner()
    {
        return owner;
    }

    public void setOwner(EntityLivingBase entity)
    {
        if (entity != null && entity instanceof EntityPlayer)
        {
            owner = ((EntityPlayer)entity).getGameProfile();
        }
    }

    public void setListRequireOp(boolean val)
    {
        doesListRequireOp = val;
    }

    public List<UserPermission> getPermissions()
    {
        return permissions;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int var1)
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                if (inventory instanceof ISidedInventory)
                {
                    return ((ISidedInventory)inventory).getAccessibleSlotsFromSide(var1);
                } else
                {
                    int size = inventory.getSizeInventory();
                    if (cachedAllSlots == null || cachedAllSlots.length != size)
                    {
                        cachedAllSlots = new int[size];
                        for (int i = 0; i < size; i++)
                        {
                            cachedAllSlots[i] = i;
                        }
                    }
                    return cachedAllSlots;
                }
            }

            return new int[0];
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean canInsertItem(int i, ItemStack itemstack, int j)
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                if (inventory instanceof ISidedInventory)
                {
                    return ((ISidedInventory)inventory).canInsertItem(i, itemstack, j);
                } else
                {
                    return inventory.isItemValidForSlot(i, itemstack);
                }
            }

            return false;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean canExtractItem(int i, ItemStack itemstack, int j)
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                if (inventory instanceof ISidedInventory)
                {
                    return ((ISidedInventory)inventory).canExtractItem(i, itemstack, j);
                } else
                {
                    return inventory.isItemValidForSlot(i, itemstack);
                }
            }

            return false;
        } finally
        {
            unBlockUsage();
        }
    }

    private IInventory getInventory()
    {
        return getContainer(IInventory.class, 0);
    }

    private void unBlockUsage()
    {
        blockingUsage = false;
        chainLength = 0;
    }

    private <T> T getContainer(Class<T> type, int id)
    {
        if (isBlockingUsage())
        {
            return null;
        }

        blockUsage();

        if (cachedEntities[id] != null)
        {
            if (cachedEntities[id].isDead)
            {
                cachedEntities[id] = null;
                if (id == 0)
                {
                    cachedInventoryWrapper = null;
                }
            } else
            {
                return getEntityContainer(id);
            }
        }

        ForgeDirection direction = getFacing();

        int x = xCoord + direction.offsetX;
        int y = yCoord + direction.offsetY;
        int z = zCoord + direction.offsetZ;

        World world = getWorldObj();
        if (world != null)
        {
            TileEntity te = world.getTileEntity(x, y, z);

            if (te != null && type.isInstance(te))
            {
                if (te instanceof TileEntityRelay)
                {
                    TileEntityRelay relay = (TileEntityRelay)te;
                    relay.chainLength = chainLength + 1;
                }
                return (T)te;
            }


            List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
            if (entities != null)
            {
                double closest = Double.MAX_VALUE;
                for (Entity entity : entities)
                {
                    double distance = entity.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    if (isEntityValid(entity, type, id) && distance < closest)
                    {
                        closest = distance;
                        cachedEntities[id] = entity;
                    }
                }
                if (id == 0 && cachedEntities[id] != null)
                {
                    cachedInventoryWrapper = getInventoryWrapper(cachedEntities[id]);
                }

                return getEntityContainer(id);
            }
        }


        return null;
    }

    public boolean isBlockingUsage()
    {
        return blockingUsage || chainLength >= MAX_CHAIN_LENGTH;
    }

    private void blockUsage()
    {
        blockingUsage = true;
    }

    private <T> T getEntityContainer(int id)
    {
        if (id == 0 && cachedInventoryWrapper != null)
        {
            return (T)cachedInventoryWrapper;
        }

        return (T)cachedEntities[id];
    }

    private boolean isEntityValid(Entity entity, Class type, int id)
    {
        return type.isInstance(entity) || (id == 0 && ((entity instanceof EntityPlayer && allowPlayerInteraction((EntityPlayer)entity)) || entity instanceof EntityHorse));
    }

    private InventoryWrapper getInventoryWrapper(Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            return new InventoryWrapperPlayer((EntityPlayer)entity);
        } else if (entity instanceof EntityHorse)
        {
            return new InventoryWrapperHorse((EntityHorse)entity);
        } else
        {
            return null;
        }
    }

    public boolean allowPlayerInteraction(EntityPlayer player)
    {
        return isAdvanced() && (creativeMode != isPlayerActive(player));
    }

    private boolean isAdvanced()
    {
        return subtype != 0;
    }

    private boolean isPlayerActive(EntityPlayer player)
    {
        if (player != null)
        {
            for (UserPermission permission : permissions)
            {
                if (permission.getUser().equals(player.getGameProfile()))
                {
                    return permission.isActive();
                }
            }
        }
        return true;
    }

    @Override
    public int getSizeInventory()
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                return inventory.getSizeInventory();
            }

            return 0;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                return inventory.getStackInSlot(i);
            }

            return null;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                return inventory.decrStackSize(i, j);
            }

            return null;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        //don't drop the things twice
        return null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                inventory.setInventorySlotContents(i, itemstack);
            }
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public String getInventoryName()
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                return inventory.getInventoryName();
            }

            return "Unknown";
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        try
        {
            IInventory inventory = getInventory();

            return inventory != null && inventory.hasCustomInventoryName();

        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                return inventory.getInventoryStackLimit();
            }

            return 0;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        try
        {
            IInventory inventory = getInventory();

            return inventory != null && inventory.isUseableByPlayer(entityplayer);

        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public void openInventory()
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                inventory.openInventory();
            }
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public void closeInventory()
    {
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                inventory.closeInventory();
            }
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        try
        {
            IInventory inventory = getInventory();

            return inventory != null && inventory.isItemValidForSlot(i, itemstack);

        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        try
        {
            IFluidHandler tank = getTank();

            if (tank != null)
            {
                return tank.fill(from, resource, doFill);
            }

            return 0;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        try
        {
            IFluidHandler tank = getTank();

            if (tank != null)
            {
                return tank.drain(from, resource, doDrain);
            }

            return null;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        try
        {
            IFluidHandler tank = getTank();

            if (tank != null)
            {
                return tank.drain(from, maxDrain, doDrain);
            }

            return null;
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        try
        {
            IFluidHandler tank = getTank();

            return tank != null && tank.canFill(from, fluid);

        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        try
        {
            IFluidHandler tank = getTank();

            return tank != null && tank.canDrain(from, fluid);

        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        try
        {
            IFluidHandler tank = getTank();

            if (tank != null)
            {
                return tank.getTankInfo(from);
            }

            return new FluidTankInfo[0];
        } finally
        {
            unBlockUsage();
        }
    }

    private IFluidHandler getTank()
    {
        return getContainer(IFluidHandler.class, 1);
    }

    @Override
    public void updateEntity()
    {
        cachedEntities[0] = null;
        cachedEntities[1] = null;
        cachedInventoryWrapper = null;
    }

    @Override
    public void markDirty()
    {
        //super.onInventoryChanged();
        try
        {
            IInventory inventory = getInventory();

            if (inventory != null)
            {
                inventory.markDirty();
            }
        } finally
        {
            unBlockUsage();
        }
    }

    @Override
    public Container getContainer(EntityPlayer player)
    {
        return new ContainerRelay(this, player.inventory);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player)
    {
        return new GuiRelay(this, player.inventory);
    }

    @Override
    public boolean readData(ASMPacket buf, EntityPlayer player)
    {
        readUpdatedData(buf, player);
        return false;
    }

    public void readUpdatedData(ASMPacket dr, EntityPlayer player)
    {
        if (!worldObj.isRemote)
        {
            boolean action = dr.readBoolean();
            if (action)
            {
                return;
            }
        }

        GameProfile user = player.getGameProfile();
        boolean isOp = false;
        if (worldObj.isRemote || user.equals(owner))
        {
            isOp = true;
        } else
        {
            for (UserPermission permission : permissions)
            {
                if (user.equals(permission.getUser()))
                {
                    isOp = permission.isOp();
                    break;
                }
            }
        }

        boolean userData = dr.readBoolean();
        if (userData)
        {
            boolean added = dr.readBoolean();
            if (added)
            {
                UserPermission permission = new UserPermission(dr.readStringFromBuffer(), dr.readUUID());

                for (UserPermission userPermission : permissions)
                {
                    if (userPermission.getUser().equals(permission.getUser()))
                    {
                        return;
                    }
                }

                if (worldObj.isRemote)
                {
                    permission.setActive(dr.readBoolean());
                    permission.setOp(dr.readBoolean());
                }

                if (permissions.size() < TileEntityRelay.PERMISSION_MAX_LENGTH && (worldObj.isRemote || permission.getUser().equals(user)))
                {
                    permissions.add(permission);
                }
            } else
            {
                int id = dr.readByte();

                if (id >= 0 && id < permissions.size())
                {
                    boolean deleted = dr.readBoolean();
                    if (deleted)
                    {
                        UserPermission permission = permissions.get(id);
                        if (isOp || permission.getUser().equals(user))
                        {
                            permissions.remove(id);
                        }
                    } else if (isOp)
                    {

                        UserPermission permission = permissions.get(id);
                        permission.setActive(dr.readBoolean());
                        permission.setOp(dr.readBoolean());

                    }
                }
            }


        } else if (isOp)
        {
            creativeMode = dr.readBoolean();
            doesListRequireOp = dr.readBoolean();
        }
    }

    public void updateData(ContainerRelay container)
    {
        if (container.oldCreativeMode != isCreativeMode() || container.oldOpList != doesListRequireOp())
        {
            container.oldOpList = doesListRequireOp();
            container.oldCreativeMode = isCreativeMode();

            ASMPacket dw = PacketHandler.getWriterForUpdate(container);
            dw.writeBoolean(false); //no user data
            dw.writeBoolean(creativeMode);
            dw.writeBoolean(doesListRequireOp);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        //added
        if (permissions.size() > container.oldPermissions.size())
        {
            int id = container.oldPermissions.size();
            UserPermission permission = permissions.get(id);

            ASMPacket dw = PacketHandler.getWriterForUpdate(container);
            dw.writeBoolean(true); //user data
            dw.writeBoolean(true); //added
            dw.writeStringToBuffer(permission.getUser().getName());
            dw.writeUUID(permission.getUser().getId());
            dw.writeBoolean(permission.isActive());
            dw.writeBoolean(permission.isOp());
            PacketHandler.sendDataToListeningClients(container, dw);

            container.oldPermissions.add(permission.copy());
            //removed
        } else if (permissions.size() < container.oldPermissions.size())
        {
            for (int i = 0; i < container.oldPermissions.size(); i++)
            {
                if (i >= permissions.size() || !permissions.get(i).getUser().equals(container.oldPermissions.get(i).getUser()))
                {
                    ASMPacket dw = PacketHandler.getWriterForUpdate(container);
                    dw.writeBoolean(true); //user data
                    dw.writeBoolean(false); //existing
                    dw.writeInt(i);
                    dw.writeBoolean(true); //deleted
                    PacketHandler.sendDataToListeningClients(container, dw);
                    container.oldPermissions.remove(i);
                    break;
                }
            }
            //updated
        } else
        {
            for (int i = 0; i < permissions.size(); i++)
            {
                UserPermission permission = permissions.get(i);
                UserPermission oldPermission = container.oldPermissions.get(i);

                if (permission.isOp() != oldPermission.isOp() || permission.isActive() != oldPermission.isActive())
                {
                    ASMPacket dw = PacketHandler.getWriterForUpdate(container);
                    dw.writeBoolean(true); //user data
                    dw.writeBoolean(false); //existing
                    dw.writeInt(i);
                    dw.writeBoolean(false); //update
                    dw.writeBoolean(permission.isActive());
                    dw.writeBoolean(permission.isOp());
                    PacketHandler.sendDataToListeningClients(container, dw);

                    oldPermission.setActive(permission.isActive());
                    oldPermission.setOp(permission.isOp());
                }
            }
        }
    }

    public boolean doesListRequireOp()
    {
        return doesListRequireOp;
    }

    public boolean isCreativeMode()
    {
        return creativeMode;
    }

    public void setCreativeMode(boolean creativeMode)
    {
        this.creativeMode = creativeMode;
    }

    @Override
    public boolean writeData(ASMPacket packet)
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToTileNBT(tagCompound);
        ByteBufUtils.writeTag(packet, tagCompound);
        return true;
    }

    @Override
    public void writeToTileNBT(NBTTagCompound nbtTagCompound)
    {
        if (isAdvanced())
        {
            if (owner != null)
            {
                nbtTagCompound.setString(NBT_OWNER, owner.getName());
                nbtTagCompound.setString(NBT_UUID, owner.getId().toString());
            }
            nbtTagCompound.setBoolean(NBT_CREATIVE, creativeMode);
            nbtTagCompound.setBoolean(NBT_LIST, doesListRequireOp);

            NBTTagList permissionTags = new NBTTagList();
            for (UserPermission permission : permissions)
            {
                NBTTagCompound permissionTag = new NBTTagCompound();
                nbtTagCompound.setString(NBT_NAME, permission.getUser().getName());
                nbtTagCompound.setString(NBT_UUID, permission.getUser().getId().toString());
                permissionTag.setBoolean(NBT_ACTIVE, permission.isActive());
                permissionTag.setBoolean(NBT_EDITOR, permission.isOp());
                permissionTags.appendTag(permissionTag);
            }
            nbtTagCompound.setTag(NBT_PERMISSIONS, permissionTags);
        }
    }

    @Override
    public void readFromTileNBT(NBTTagCompound nbtTagCompound)
    {
        if (nbtTagCompound.hasKey(NBT_OWNER))
        {
            String name = nbtTagCompound.getString(NBT_OWNER);
            UUID id = UUID.fromString(nbtTagCompound.getString(NBT_UUID));
            owner = new GameProfile(id, name);
            creativeMode = nbtTagCompound.getBoolean(NBT_CREATIVE);
            doesListRequireOp = nbtTagCompound.getBoolean(NBT_LIST);
            permissions.clear();

            NBTTagList permissionTags = nbtTagCompound.getTagList(NBT_PERMISSIONS, 10);
            for (int i = 0; i < permissionTags.tagCount(); i++)
            {
                NBTTagCompound permissionTag = permissionTags.getCompoundTagAt(i);
                UserPermission permission = new UserPermission(permissionTag.getString(NBT_NAME), UUID.fromString(nbtTagCompound.getString(NBT_UUID)));
                permission.setActive(permissionTag.getBoolean(NBT_ACTIVE));
                permission.setOp(permissionTag.getBoolean(NBT_EDITOR));
                permissions.add(permission);
            }
        }
    }
}
