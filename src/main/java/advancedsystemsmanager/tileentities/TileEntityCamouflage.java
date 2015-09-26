package advancedsystemsmanager.tileentities;

import advancedsystemsmanager.api.network.IPacketBlock;
import advancedsystemsmanager.blocks.BlockCamouflaged;
import advancedsystemsmanager.flow.menus.MenuCamouflageInside;
import advancedsystemsmanager.flow.menus.MenuCamouflageShape;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;


public class TileEntityCamouflage extends TileEntityClusterElement implements IPacketBlock
{
    private static final String NBT_SIDES = "Sides";
    private static final String NBT_ID = "Id";
    private static final String NBT_META = "Meta";
    private static final String NBT_COLLISION = "Collision";
    private static final String NBT_FULL = "Full";
    private static final String NBT_BOUNDS = "Bounds";
    private boolean useCollision = true;
    private boolean fullCollision = false;
    private byte[] bounds = {0, 32, 0, 32, 0, 32};
    private Block[] ids = new Block[2];
    private int[] metas = new int[2];
    private boolean isServerDirty;
    private CamouflageType type;

    @Override
    public void setSubtype(int subtype)
    {
        this.type = CamouflageType.getByID(subtype);
    }

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
        return type;
    }

    @SideOnly(Side.CLIENT)
    public boolean addBlockEffect(Block camoBlock, int sideHit, EffectRenderer effectRenderer)
    {
        try
        {
            if (ids[0] != null)
            {
                Block block = ids[0];
                float f = 0.1F;
                double x = (double)xCoord + worldObj.rand.nextDouble() * (camoBlock.getBlockBoundsMaxX() - camoBlock.getBlockBoundsMinX() - (double)(f * 2.0F)) + (double)f + camoBlock.getBlockBoundsMinX();
                double y = (double)yCoord + worldObj.rand.nextDouble() * (camoBlock.getBlockBoundsMaxY() - camoBlock.getBlockBoundsMinY() - (double)(f * 2.0F)) + (double)f + camoBlock.getBlockBoundsMinY();
                double z = (double)zCoord + worldObj.rand.nextDouble() * (camoBlock.getBlockBoundsMaxZ() - camoBlock.getBlockBoundsMinZ() - (double)(f * 2.0F)) + (double)f + camoBlock.getBlockBoundsMinZ();

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
                effectRenderer.addEffect((new EntityDiggingFX(this.worldObj, x, y, z, 0.0D, 0.0D, 0.0D, block, metas[0])).multiplyVelocity(0.2F).multipleParticleScaleBy(0.6F));
                return true;
            }
        } catch (Exception ignored)
        {
        }

        return false;
    }

    public void setBlockBounds(BlockCamouflaged blockCamouflageBase)
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

    public void setItem(ItemStack item, MenuCamouflageInside.InsideSetType type)
    {
        switch (type)
        {
            case ONLY_OUTSIDE:
                setItem(item, 0);
                break;
            case ONLY_INSIDE:
                setItemForInside(item, 1);
                break;
            case SAME:
                setItem(item, 0);
                setItemForInside(item, 1);
                break;
            default:
        }
    }

    private void setItem(ItemStack item, int side)
    {
        Block oldBlock = ids[side];
        int oldMeta = metas[side];

        if (item == null)
        {
            ids[side] = null;
            metas[side] = 0;
        } else if (item.getItem() != null && item.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock)item.getItem()).field_150939_a;
            if (block != null)
            {
                ids[side] = block;
                metas[side] = item.getItem().getMetadata(item.getItemDamage());
            } else
            {
                ids[side] = null;
                metas[side] = 0;
            }
        }

        if (ids[side] != oldBlock || metas[side] != oldMeta)
        {
            isServerDirty = true;
        }
    }

    private void setItemForInside(ItemStack item, int side)
    {
        if (getCamouflageType().useDoubleRendering())
        {
            setItem(item, side);
        }
    }

    @Override
    public void writeData(ASMPacket dw, int id)
    {
        for (int i = 0; i < getSideCount(); i++)
        {
            if (ids[i] == null)
            {
                dw.writeBoolean(false);
            } else
            {
                dw.writeBoolean(true);
                dw.writeShort(Block.getIdFromBlock(ids[i]));
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
                dw.writeByte(bound);
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
                ids[i] = null;
                metas[i] = 0;
            } else
            {
                ids[i] = Block.getBlockById(dr.readShort());
                metas[i] = dr.readByte();
            }
        }
        if (getCamouflageType().useSpecialShape())
        {
            useCollision = dr.readBoolean();
            fullCollision = useCollision && dr.readBoolean();

            for (int i = 0; i < bounds.length; i++)
            {
                bounds[i] = dr.readByte();
            }
        }
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity()
    {
        if (!worldObj.isRemote)
        {
            if (isServerDirty)
            {
                isServerDirty = false;
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        PacketHandler.sendBlockPacket(this, null, 0);
        return null;
    }

    @Override
    public void writeContentToNBT(NBTTagCompound tagCompound)
    {
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < getSideCount(); i++)
        {
            NBTTagCompound element = new NBTTagCompound();

            element.setShort(NBT_ID, (short)Block.getIdFromBlock(ids[i]));
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
    public void readContentFromNBT(NBTTagCompound tagCompound)
    {
        NBTTagList list = tagCompound.getTagList(NBT_SIDES, 10);
        for (int i = 0; i < Math.min(list.tagCount(),2); i++)
        {
            NBTTagCompound element = list.getCompoundTagAt(i);

            ids[i] = Block.getBlockById(element.getShort(NBT_ID));
            metas[i] = element.getByte(NBT_META);
        }

        if (tagCompound.hasKey(NBT_COLLISION))
        {
            useCollision = tagCompound.getBoolean(NBT_COLLISION);
            fullCollision = tagCompound.getBoolean(NBT_FULL);

            bounds = tagCompound.getByteArray(NBT_BOUNDS);
        }
    }

    public Block getSideBlock(int side)
    {
        return ids[side];
    }

    public int getSideMetadata(int side)
    {
        return metas[side];
    }

    public boolean hasSideBlock(int side)
    {
        return ids[side] != null;
    }

    public enum CamouflageType
    {
        NORMAL(false, false),
        INSIDE(true, false),
        SHAPE(true, true);

        private boolean useDouble;
        private boolean useShape;

        CamouflageType(boolean useDouble, boolean useShape)
        {
            this.useDouble = useDouble;
            this.useShape = useShape;
        }

        public static CamouflageType getByID(int id)
        {
            return values()[id % values().length];
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
