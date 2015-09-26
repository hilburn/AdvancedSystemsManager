package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.blocks.BlockCamouflaged;
import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import advancedsystemsmanager.tileentities.TileEntityEmitter;
import advancedsystemsmanager.tileentities.TileEntityQuantumCable;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

public class WailaCompat extends CompatBase
{
    private static Object labelProvider;
    private static Object registrar;
    private static List<Class> labelClasses = new ArrayList<Class>();

    @Optional.Method(modid = Mods.WAILA)
    public static void callbackRegister(IWailaRegistrar registrar)
    {
        IWailaDataProvider labelProvider = new WailaLabelProvider();
        WailaCompat.labelProvider = labelProvider;
        WailaCompat.registrar = registrar;
        registrar.registerHeadProvider(labelProvider, IInventory.class);
        registrar.registerHeadProvider(labelProvider, IFluidHandler.class);
        registrar.registerHeadProvider(labelProvider, TileEntityClusterElement.class);
        registrar.registerHeadProvider(labelProvider, TileEntityCluster.class);
        for (Class clazz : labelClasses)
        {
            registrar.registerHeadProvider(labelProvider, clazz);
        }

        registrar.registerBodyProvider(new ClusterDataProvider(), TileEntityCluster.class);
        registrar.registerBodyProvider(new RedstoneOutputDataProvider(), TileEntityEmitter.class);
        registrar.registerBodyProvider(new QuantumDataProvider(), TileEntityQuantumCable.class);

        CamouflageDataProvider provider = new CamouflageDataProvider();
        registrar.registerStackProvider(provider, BlockCamouflaged.class);
        registrar.registerHeadProvider(provider, BlockCamouflaged.class);
        registrar.registerBodyProvider(provider, BlockCamouflaged.class);
        registrar.registerTailProvider(provider, BlockCamouflaged.class);

    }

    @Override
    protected void init()
    {
        FMLInterModComms.sendMessage(Mods.WAILA, "register", "advancedsystemsmanager.compatibility.waila.WailaCompat.callbackRegister");
    }

    public void registerLabelProvider(Class clazz)
    {
        if (mod.isLoaded())
        {
            if (registrar != null)
            {
                ((IWailaRegistrar) registrar).registerHeadProvider((IWailaDataProvider)labelProvider, clazz);
            } else
            {
                labelClasses.add(clazz);
            }
        }
    }
}
