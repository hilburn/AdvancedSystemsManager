package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityEmitter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class RedstoneOutputDataProvider implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        TileEntityEmitter emitter = (TileEntityEmitter)iWailaDataAccessor.getTileEntity();

        if (isShiftDown())
        {
            for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++)
            {
                list.add(getEmitterSide(emitter, i, true));
            }
        } else
        {
            list.add(getEmitterSide(emitter, iWailaDataAccessor.getPosition().sideHit, false));
        }
        return list;
    }

    @SideOnly(Side.CLIENT)
    private boolean isShiftDown()
    {
        return GuiScreen.isShiftKeyDown();
    }

    private String getEmitterSide(TileEntityEmitter emitter, int side, boolean full)
    {
        String str = StatCollector.translateToLocal(emitter.hasStrongSignalAtSide(side) ? Names.STRONG_POWER : Names.WEAK_POWER) + ": " + emitter.getStrengthFromSide(side) + " ";

        if (full)
        {
            str = StatCollector.translateToLocal(LocalizationHelper.getDirectionString(side)) + " " + str;
        }

        return str;
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
