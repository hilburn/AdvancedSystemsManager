package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.registry.BlockRegistry;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class TileEntityBlockGate extends TileEntityClusterElement implements IInventory, IPacketBlock
{

    protected static final int[] ROTATION_SIDE_MAPPING = {0, 0, 0, 2, 3, 1};
    protected static final String NBT_DIRECTION = "Direction";
    private static final String FAKE_PLAYER_NAME = "[ASM_PLAYER]";
    private static final UUID FAKE_PLAYER_ID = null;
    protected boolean missingPlaceDirection;
    protected boolean blocked;
    protected int placeDirection;
    private List<ItemStack> inventory;
    private List<ItemStack> inventoryCache;
    private boolean broken;

    public int getPlaceDirection()
    {
        return placeDirection;
    }

    public void setPlaceDirection(int placeDirection)
    {
        if (this.placeDirection != placeDirection)
        {
            this.placeDirection = placeDirection;

            if (!isPartOfCluster() && worldObj != null && !worldObj.isRemote)
            {
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }

    @Override
    public void writeData(ASMPacket packet, int id)
    {
        packet.writeByte(placeDirection);
    }

    @Override
    public void readData(ASMPacket packet, int id)
    {
        placeDirection = packet.readByte();
        markBlockForUpdate();
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setByte(NBT_DIRECTION, (byte)placeDirection);
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        if (tagCompound.hasKey(NBT_DIRECTION))
        {
            setPlaceDirection(tagCompound.getByte(NBT_DIRECTION));
        } else
        {
            if (worldObj != null)
            {
                setPlaceDirection(getMetadata());
            } else
            {
                missingPlaceDirection = true;
            }
        }
    }

    @Override
    public EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY, ClusterMethodRegistration.ON_BLOCK_ACTIVATED);
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        if (missingPlaceDirection)
        {
            setPlaceDirection(getMetadata());
            missingPlaceDirection = false;
        }
        if (inventory != null)
        {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getMetadata() % ForgeDirection.VALID_DIRECTIONS.length];


            for (ItemStack itemStack : getInventoryForDrop())
            {
                List<ItemStack> items = placeItem(itemStack);
                if (items != null && !items.isEmpty())
                {
                    for (ItemStack item : items)
                    {
                        double x = xCoord + 0.5 + direction.offsetX * 0.75;
                        double y = yCoord + 0.5 + direction.offsetY * 0.75;
                        double z = zCoord + 0.5 + direction.offsetZ * 0.75;


                        if (direction.offsetY == 0)
                        {
                            y -= 0.1;
                        }

                        EntityItem entityitem = new EntityItem(worldObj, x, y, z, item);

                        entityitem.motionX = direction.offsetX * 0.1;
                        entityitem.motionY = direction.offsetY * 0.1;
                        entityitem.motionZ = direction.offsetZ * 0.1;

                        entityitem.delayBeforeCanPickup = 40;
                        worldObj.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }
        inventory = null;
        inventoryCache = null;
        broken = false;
    }

    private List<ItemStack> getInventoryForDrop()
    {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        for (ItemStack itemStack : inventory)
        {
            if (itemStack != null)
            {
                ItemStack newStack = itemStack.copy();


                if (!broken)
                {
                    for (int i = 0; i < inventoryCache.size(); i++)
                    {
                        ItemStack copyStack = inventoryCache.get(i);

                        if (copyStack != null && newStack.isItemEqual(copyStack) && ItemStack.areItemStackTagsEqual(newStack, copyStack))
                        {
                            int max = Math.min(copyStack.stackSize, newStack.stackSize);

                            copyStack.stackSize -= max;
                            if (copyStack.stackSize == 0)
                            {
                                inventoryCache.set(0, null);
                            }

                            newStack.stackSize -= max;
                            if (newStack.stackSize == 0)
                            {
                                newStack = null;
                                break;
                            }
                        }
                    }
                }


                if (newStack != null)
                {
                    ret.add(newStack);
                }
            }
        }
        return ret;
    }

    private List<ItemStack> placeItem(ItemStack itemstack)
    {
        List<ItemStack> items = new ArrayList<ItemStack>();

        if (itemstack != null && itemstack.getItem() != null && itemstack.stackSize > 0)
        {
            ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[getMetadata() % ForgeDirection.VALID_DIRECTIONS.length];
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[placeDirection];
            if (side == direction)
            {
                side = ForgeDirection.UNKNOWN;
            }
            direction = direction.getOpposite();

            float hitX = 0.5F + direction.offsetX * 0.5F;
            float hitY = 0.5F + direction.offsetY * 0.5F;
            float hitZ = 0.5F + direction.offsetZ * 0.5F;

            EntityPlayerMP player = FakePlayerFactory.get((WorldServer)worldObj, new GameProfile(FAKE_PLAYER_ID, FAKE_PLAYER_NAME));
            int rotationSide = ROTATION_SIDE_MAPPING[direction.ordinal()];

            player.prevRotationPitch = player.rotationYaw = rotationSide * 90;
            player.prevRotationYaw = player.rotationPitch = direction == ForgeDirection.UP ? 90 : direction == ForgeDirection.DOWN ? -90 : 0;
            player.prevPosX = player.posX = xCoord + side.offsetX + 0.5 + direction.offsetX * 0.4;
            player.prevPosY = player.posY = yCoord + side.offsetY + 0.5 + direction.offsetY * 0.4;
            player.prevPosZ = player.posZ = zCoord + side.offsetZ + 0.5 + direction.offsetZ * 0.4;
            player.eyeHeight = 0;
            player.yOffset = 1.82F;
            player.theItemInWorldManager.setBlockReachDistance(1);

            blocked = true;
            try
            {
                player.inventory.clearInventory(null, -1);
                player.inventory.currentItem = 0;
                player.inventory.setInventorySlotContents(0, itemstack);
                ItemStack result = itemstack.useItemRightClick(worldObj, player);
                if (ItemStack.areItemStacksEqual(result, itemstack))
                {
                    if (side == ForgeDirection.UNKNOWN)
                    {
                        side = direction.getOpposite();
                    }
                    int x = xCoord + side.offsetX - direction.offsetX;
                    int y = yCoord + side.offsetY - direction.offsetY;
                    int z = zCoord + side.offsetZ - direction.offsetZ;

                    player.theItemInWorldManager.activateBlockOrUseItem(player, worldObj, itemstack, x, y, z, direction.ordinal(), hitX, hitY, hitZ);

                } else
                {
                    player.inventory.setInventorySlotContents(0, result);
                }
            } catch (Exception ignored)
            {

            } finally
            {
                for (ItemStack itemStack : player.inventory.mainInventory)
                {
                    if (itemStack != null && itemStack.stackSize > 0)
                    {
                        items.add(itemStack);
                    }
                }
                blocked = false;
            }

        }

        return items;
    }

    @Override
    public void markDirty()
    {
        super.markDirty();

        if (inventory != null && !broken)
        {
            boolean match = true;
            for (int i = 0; i < inventory.size(); i++)
            {
                ItemStack itemStack = inventory.get(i);
                ItemStack itemStackCopy = inventoryCache.get(i);

                if (!ItemStack.areItemStacksEqual(itemStack, itemStackCopy) || itemStack.stackSize < itemStackCopy.stackSize)
                {
                    match = false;
                    break;
                }
            }

            if (!match)
            {
                ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

                int x = xCoord + direction.offsetX;
                int y = yCoord + direction.offsetY;
                int z = zCoord + direction.offsetZ;

                Block block = worldObj.getBlock(x, y, z);


                if (canBreakBlock(block, x, y, z))
                {
                    broken = true;
                    int meta = worldObj.getBlockMetadata(x, y, z);
                    block.breakBlock(worldObj, x, y, z, block, meta);
                    worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (meta << 12));
                    worldObj.setBlockToAir(x, y, z);
                }

            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        PacketHandler.sendBlockPacket(this, null, 0);
        return null;
    }

    private boolean canBreakBlock(Block block, int x, int y, int z)
    {
        return block != null && block != Blocks.bedrock && block.getBlockHardness(worldObj, x, y, z) >= 0;
    }

    @Override
    public int getSizeInventory()
    {
        return getInventory().size() + 1;
    }

    private List<ItemStack> getInventory()
    {
        if (inventory == null)
        {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            int x = xCoord + direction.offsetX;
            int y = yCoord + direction.offsetY;
            int z = zCoord + direction.offsetZ;
            Block block = worldObj.getBlock(x, y, z);
            if (canBreakBlock(block, x, y, z))
            {
                inventory = block.getDrops(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 0);
            }
            if (inventory == null)
            {
                inventory = new ArrayList<ItemStack>();
            }
            inventoryCache = new ArrayList<ItemStack>();
            for (ItemStack itemStack : inventory)
            {
                inventoryCache.add(itemStack.copy());
            }
        }

        return inventory;
    }

    @Override
    public ItemStack getStackInSlot(int id)
    {
        if (id < getInventory().size())
        {
            return getInventory().get(id);
        } else
        {
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int id, int count)
    {

        ItemStack item = getStackInSlot(id);
        if (item != null)
        {
            if (item.stackSize <= count)
            {
                getInventory().set(id, null);
                return item;
            }

            ItemStack ret = item.splitStack(count);

            if (item.stackSize == 0)
            {
                getInventory().set(id, null);
            }

            return ret;
        } else
        {
            return null;
        }

    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int id, ItemStack itemstack)
    {
        if (id < getInventory().size())
        {
            getInventory().set(id, itemstack);
        } else
        {
            getInventory().add(itemstack);
            inventoryCache.add(null);
        }
    }

    @Override
    public String getInventoryName()
    {
        return BlockRegistry.cableBlockGate.getLocalizedName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return false;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return true;
    }

    public boolean isBlocked()
    {
        return blocked;
    }
}
