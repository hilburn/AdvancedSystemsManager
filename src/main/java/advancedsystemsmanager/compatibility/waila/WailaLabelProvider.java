package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.naming.NameRegistry;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.common.Optional;
import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = Mods.WAILA)
public class WailaLabelProvider implements IWailaDataProvider
{

    @Override
    @Optional.Method(modid = Mods.WAILA)
    public ItemStack getWailaStack(IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    @Optional.Method(modid = Mods.WAILA)
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        ITaggedList tagged = (ITaggedList)list;
        if (iWailaDataAccessor.getBlock() != null && tagged.getEntries(Names.LABELLED).isEmpty())
        {
            String label = NameRegistry.getSavedName(iWailaDataAccessor.getWorld().provider.dimensionId, iWailaDataAccessor.getPosition().blockX, iWailaDataAccessor.getPosition().blockY, iWailaDataAccessor.getPosition().blockZ);
            if (label != null)
            {
                tagged.add(StatCollector.translateToLocalFormatted(Names.LABELLED, label), Names.LABELLED);
            }
        }
        return list;
    }

    @Override
    @Optional.Method(modid = Mods.WAILA)
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return list;
    }

    @Override
    @Optional.Method(modid = Mods.WAILA)
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return list;
    }

    @Override
    @Optional.Method(modid = Mods.WAILA)
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2)
    {
        return null;
    }
}
