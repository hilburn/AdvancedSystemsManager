package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.blocks.BlockCamouflageBase;
import advancedsystemsmanager.flow.menus.MenuCamouflageInside;
import advancedsystemsmanager.flow.menus.MenuCamouflageShape;
import advancedsystemsmanager.network.*;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.EnumSet;
import java.util.Random;


public class TileEntityCamouflage extends TileEntityClusterElement implements IPacketBlock
{

    private static final Random rand = new Random();
    private static final int UPDATE_BUFFER_DISTANCE = 5;
    private static final String NBT_SIDES = "Sides";
    private static final String NBT_ID = "Id";
    private static final String NBT_META = "Meta";
    private static final String NBT_COLLISION = "Collision";
    private static final String NBT_FULL = "Full";
    private static final String NBT_BOUNDS = "Bounds";
    private boolean useCollision = true;
    private boolean fullCollision = false;
    private byte[] bounds = {0, 32, 0, 32, 0, 32};
    private int[] ids = new int[ForgeDirection.VALID_DIRECTIONS.length * 2];
    private int[] metas = new int[ForgeDirection.VALID_DIRECTIONS.length * 2];
    private boolean hasClientUpdatedData;
    private boolean isServerDirty;

    public boolean isNormalBlock()
    {
        if (getCamouflageType().useSpecialShape())
        {
            if (!useCollision)
            {
                return false;
            } else
            {
                for (int i = 0; i < bounds.length; i++)
                {
                    if (bounds[i] != (i % 2 == 0 ? 0 : 32))
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public CamouflageType getCamouflageType()
    {
        return CamouflageType.getByID(getBlockMetadata());
    }

    @SideOnly(Side.CLIENT)
    public boolean addBlockEffect(Block camoBlock, int sideHit, EffectRenderer effectRenderer)
    {
        try
        {
            if (ids[sideHit] != 0)
            {
                Block block = Block.getBlockById(ids[sideHit]);
                if (block != null)
                {
                    float f = 0.1F;
                    double x = (double)xCoord + rand.nextDouble() * (camoBlock.getBlockBoundsMaxX() - camoBlock.getBlockBoundsMinX() - (double)(f * 2.0F)) + (double)f + camoBlock.getBlockBoundsMinX();
                    double y = (double)yCoord + rand.nextDouble() * (camoBlock.getBlockBoundsMaxY() - camoBlock.getBlockBoundsMinY() - (double)(f * 2.0F)) + (double)f + camoBlock.getBlockBoundsMinY();
                    double z = (double)zCoord + rand.nextDouble() * (camoBlock.getBlockBoundsMaxZ() - camoBlock.getBlockBoundsMinZ() - (double)(f * 2.0F)) + (double)f + camoBlock.getBlockBoundsMinZ();

                    switch (sideHit)
                    {
                        case 0:
                            y = (double)yCoord + camoBlock.getBlockBoundsMinY() - (double)f;
                            break;
                        case 1:
                            y = (double)yCoord + camoBlock.getBlockBoundsMaxY() + (double)f;
                            break;
                        case 2:
                            z = (double)zCoord + camoBlock.getBlockBoundsMinZ() - (double)f;
                            break;
                        case 3:
                            z = (double)zCoord + camoBlock.getBlockBoundsMaxZ() + (double)f;
                            break;
                        case 4:
                            x = (double)xCoord + camoBlock.getBlockBoundsMinX() - (double)f;
                            break;
                        case 5:
                            x = (double)xCoord + camoBlock.getBlockBoundsMaxX() + (double)f;
                            break;
                    }


                    effectRenderer.addEffect((new EntityDiggingFX(this.worldObj, x, y, z, 0.0D, 0.0D, 0.0D, block, metas[sideHit])).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
                    return true;
                }
            }
        } catch (Exception ignored)
        {
        }

        return false;
    }

    public void setBlockBounds(BlockCamouflageBase blockCamouflageBase)
    {
        blockCamouflageBase.setBlockBounds(bounds[0] / 32F, bounds[2] / 32F, bounds[4] / 32F, bounds[1] / 32F, bounds[3] / 32F, bounds[5] / 32F);
    }

    public boolean isUseCollision()
    {
        return useCollision;
    }

    public boolean isFullCollision()
    {
        return fullCollision;
    }

    public void setBounds(MenuCamouflageShape menu)
    {
        if (getCamouflageType().useSpecialShape() && menu.shouldUpdate())
        {
            if (menu.isUseCollision() != useCollision)
            {
                useCollision = menu.isUseCollision();
                isServerDirty = true;
            }

            if (menu.isFullCollision() != fullCollision)
            {
                fullCollision = menu.isFullCollision();
                isServerDirty = true;
            }

            for (int i = 0; i < bounds.length; i++)
            {
                if (bounds[i] != menu.getBounds(i))
                {
                    bounds[i] = (byte)menu.getBounds(i);
                    isServerDirty = true;
                }
            }

            for (int i = 0; i < bounds.length; i += 2)
            {
                if (bounds[i] > bounds[i + 1])
                {
                    int tmp = bounds[i + 1];
                    bounds[i + 1] = bounds[i];
                    bounds[i] = (byte)tmp;
                }
            }
        }
    }

    public void setItem(ItemStack item, int side, MenuCamouflageInside.InsideSetType type)
    {
        switch (type)
        {
            case ONLY_OUTSIDE:
                setItem(item, side);
                break;
            case ONLY_INSIDE:
                setItemForInside(item, side + ForgeDirection.VALID_DIRECTIONS.length);
                break;
            case SAME:
                setItem(item, side);
                setItemForInside(item, side + ForgeDirection.VALID_DIRECTIONS.length);
                break;
            case OPPOSITE:
                setItem(item, side);
                int sidePairInternalId = side % 2;
                int insideSide = side + (sidePairInternalId == 0 ? 1 : -1);
                setItemForInside(item, insideSide + ForgeDirection.VALID_DIRECTIONS.length);
                break;
            default:
        }
    }

    private void setItemForInside(ItemStack item, int side)
    {
        if (getCamouflageType().useDoubleRendering())
        {
            setItem(item, side);
        }
    }

    private void setItem(ItemStack item, int side)
    {
        int oldId = ids[side];
        int oldMeta = metas[side];

        if (item == null)
        {
            ids[side] = 0;
            metas[side] = 0;
        } else if (item.getItem() != null && item.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock)item.getItem()).field_150939_a;
            if (block != null)
            {
                ids[side] = Block.getIdFromBlock(block);
                metas[side] = item.getItem().getMetadata(item.getItemDamage());
                validateSide(side);
            } else
            {
                ids[side] = 0;
                metas[side] = 0;
            }
        }

        if (ids[side] != oldId || metas[side] != oldMeta)
        {
            isServerDirty = true;
        }
    }

    private void validateSide(int i)
    {
        if (ids[i] < 0 || ids[i] >= Block.blockRegistry.getKeys().size())
        {
            ids[i] = 0;
        }
    }

    public int getId(int side)
    {
        return ids[side];
    }

    public int getMeta(int side)
    {
        return metas[side];
    }

    @Override
    public void writeData(ASMPacket dw, int id)
    {
        for (int i = 0; i < getSideCount(); i++)
        {
            if (ids[i] == 0)
            {
                dw.writeBoolean(false);
            } else
            {
                dw.writeBoolean(true);
                dw.writeShort(ids[i]);
                dw.writeByte(metas[i]);
            }
        }
        if (getCamouflageType().useSpecialShape())
        {
            dw.writeBoolean(useCollision);
            if (useCollision)
            {
                dw.writeBoolean(fullCollision);
            }
            for (int bound : bounds)
            {
                //This is done since 0 and 32 are the most common values and the final bit would only be used by 32 anyways
                //0 -> 01
                //32 -> 11
                //1 to 31 ->  bin(bound) << 1

                if (bound == 0)
                {
                    dw.writeBoolean(true);
                    dw.writeBoolean(false);
                } else if (bound == 32)
                {
                    dw.writeBoolean(true);
                    dw.writeBoolean(true);
                } else
                {
                    dw.writeByte(bound << 1);
                }
            }
        }
    }

    private int getSideCount()
    {
        return getCamouflageType().useDoubleRendering() ? ids.length : ids.length / 2;
    }

    @Override
    public void readData(ASMPacket dr, int id)
    {
        for (int i = 0; i < getSideCount(); i++)
        {
            if (!dr.readBoolean())
            {
                ids[i] = 0;
                metas[i] = 0;
            } else
            {
                ids[i] = dr.readShort();
                metas[i] = dr.readByte();
                validateSide(i);
            }
        }
        if (getCamouflageType().useSpecialShape())
        {
            useCollision = dr.readBoolean();
            if (useCollision)
            {
                fullCollision = dr.readBoolean();
            } else
            {
                fullCollision = false;
            }

            for (int i = 0; i < bounds.length; i++)
            {
                //This is done since 0 and 32 are the most common values and the final bit would only be used by 32 anyways
                //0 -> 01
                //32 -> 11
                //1 to 31 ->  bin(bound) << 1

                if (dr.readBoolean())
                {
                    bounds[i] = (byte)(dr.readBoolean() ? 32 : 0);
                } else
                {
                    bounds[i] = (byte)(dr.readByte() - 1);
                }
            }
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity()
    {
        if (worldObj.isRemote)
        {
            keepClientDataUpdated();
        } else
        {
            if (isServerDirty)
            {
                isServerDirty = false;
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void keepClientDataUpdated()
    {
        double distance = Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

        if (distance > PacketHandler.BLOCK_UPDATE_SQ)
        {
            hasClientUpdatedData = false;
        } else if (!hasClientUpdatedData && distance < Math.pow(PacketHandler.BLOCK_UPDATE_RANGE - UPDATE_BUFFER_DISTANCE, 2))
        {
            hasClientUpdatedData = true;
            PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    @Override
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList list = tagCompound.getTagList(NBT_SIDES, 10);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound element = list.getCompoundTagAt(i);

            ids[i] = element.getShort(NBT_ID);
            metas[i] = element.getByte(NBT_META);
            validateSide(i);
        }

        if (tagCompound.hasKey(NBT_COLLISION))
        {
            useCollision = tagCompound.getBoolean(NBT_COLLISION);
            fullCollision = tagCompound.getBoolean(NBT_FULL);

            bounds = tagCompound.getByteArray(NBT_BOUNDS);
        }
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSideCount(); i++)
        {
            NBTTagCompound element = new NBTTagCompound();

            element.setShort(NBT_ID, (short)ids[i]);
            element.setByte(NBT_META, (byte)metas[i]);

            list.appendTag(element);
        }
        tagCompound.setTag(NBT_SIDES, list);

        if (getCamouflageType().useSpecialShape())
        {
            tagCompound.setBoolean(NBT_COLLISION, useCollision);
            tagCompound.setBoolean(NBT_FULL, fullCollision);

            tagCompound.setByteArray(NBT_BOUNDS, bounds);
        }


    }

    @Override
    public EnumSet<ClusterMethodRegistration> getRegistrations()
    {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconWithDefault(IBlockAccess world, int x, int y, int z, BlockCamouflageBase block, int side, boolean inside)
    {
        IIcon icon = getIcon(side, inside);
        if (icon == null)
        {
            icon = block.getDefaultIcon(side, world.getBlockMetadata(x, y, z), getBlockMetadata()); //here we actually want to fetch the meta data of the block, rather then getting the tile entity version
        }

        return icon;
    }

    @SideOnly(Side.CLIENT)
    private IIcon getIcon(int side, boolean inside)
    {
        if (inside)
        {
            side += ForgeDirection.VALID_DIRECTIONS.length;
        }

        Block block = Block.getBlockById(ids[side]);
        if (block != null)
        {
            try
            {
                IIcon icon = block.getIcon(side, metas[side]);
                if (icon != null)
                {
                    return icon;
                }
            } catch (Exception ignored)
            {
            }
        }
        return null;
    }

    public enum CamouflageType
    {
        NORMAL("", false, false),
        INSIDE("_inside", true, false),
        SHAPE("_shape", true, true);

        private String unlocalized;
        private String icon;
        private boolean useDouble;
        private boolean useShape;

        private CamouflageType(String tag, boolean useDouble, boolean useShape)
        {
            this.unlocalized = tag;
            this.icon = Names.CABLE_CAMO + tag;
            this.useDouble = useDouble;
            this.useShape = useShape;
        }

        public static CamouflageType getByID(int id)
        {
            return values()[id % values().length];
        }

        public String getUnlocalized()
        {
            return unlocalized;
        }

        public String getIcon()
        {
            return icon;
        }

        public boolean useDoubleRendering()
        {
            return useDouble;
        }

        public boolean useSpecialShape()
        {
            return useShape;
        }
    }
}
