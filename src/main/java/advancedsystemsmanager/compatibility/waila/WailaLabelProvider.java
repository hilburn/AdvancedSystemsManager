package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.naming.BlockCoord;
import advancedsystemsmanager.naming.NameRegistry;
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

public class WailaLabelProvider implements IWailaDataProvider
{
    public static final String LABELLED = "asm.waila.labelled";

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return list;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<String> getWailaBody(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        ITaggedList tagged = (ITaggedList)list;
        if (iWailaDataAccessor.getBlock() != null && tagged.getEntries(LABELLED).isEmpty())
        {
            BlockCoord coord = new BlockCoord(iWailaDataAccessor.getPosition().blockX, iWailaDataAccessor.getPosition().blockY, iWailaDataAccessor.getPosition().blockZ);
            String label = NameRegistry.getSavedName(iWailaDataAccessor.getWorld().provider.dimensionId, coord);
            if (label != null)
            {
                tagged.add(StatCollector.translateToLocalFormatted(LABELLED, label), LABELLED);
//                int size = tagged.size();
//                if (size > 1)
//                {
//                    for (Iterator<String> itr = tagged.iterator(); itr.hasNext() && size > 1; size--)
//                    {
//                        String val = itr.next();
//                        Set<String> tags = tagged.getTags(val);
//                        itr.remove();
//                        tagged.add(val, tags);
//                    }
//                }
            }
        }
        return list;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> list, IWailaDataAccessor iWailaDataAccessor, IWailaConfigHandler iWailaConfigHandler)
    {
        return list;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP entityPlayerMP, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2)
    {
        return null;
    }
}
