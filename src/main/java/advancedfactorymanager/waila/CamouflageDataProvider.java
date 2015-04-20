package advancedfactorymanager.waila;

import advancedfactorymanager.tileentities.TileEntityCamouflage;
import advancedfactorymanager.tileentities.TileEntityCluster;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class CamouflageDataProvider implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler iWailaConfigHandler)
    {
        TileEntity te = accessor.getTileEntity();
        if (te != null && !isShiftDown())
        {
            TileEntityCamouflage camouflage = TileEntityCluster.getTileEntity(TileEntityCamouflage.class, te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
            if (camouflage != null)
            {
                int id = camouflage.getId(accessor.getPosition().sideHit);
                int meta = camouflage.getMeta(accessor.getPosition().sideHit);

                if (id != 0)
                {
                    Block block = Block.getBlockById(id);
                    if (block != null)
                    {
                        return new ItemStack(block, 1, block.damageDropped(meta));
                    }
                }
            }
        }
        return new ItemStack(accessor.getBlock(), 1, accessor.getBlock().damageDropped(accessor.getMetadata()));
    }

    @SideOnly(Side.CLIENT)
    private boolean isShiftDown()
    {
        return GuiScreen.isShiftKeyDown();
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2)
    {
        return null;
    }
}
