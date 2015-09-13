package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class QuantumDataProvider implements IWailaDataProvider
{
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TileEntity te =  accessor.getTileEntity();
        if (te instanceof TileEntityQuantumCable)
        {
            currenttip.add(LocalizationHelper.translateFormatted(Names.QUANTUM_RANGE, ((TileEntityQuantumCable) te).getQuantumRange()));
            TileEntityQuantumCable pair = TileEntityQuantumCable.getPairedCable((TileEntityQuantumCable) te);
            if (pair != null)
            {
                currenttip.add(LocalizationHelper.translate(Names.QUANTUM_PAIRED));
            } else
            {
                currenttip.add(LocalizationHelper.translateFormatted(Names.QUANTUM_PAIRING, ((TileEntityQuantumCable) te).getSpinString()));
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z)
    {
        return null;
    }
}
