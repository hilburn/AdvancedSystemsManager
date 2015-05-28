package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.blocks.BlockCableOutput;
import advancedsystemsmanager.blocks.BlockCamouflageBase;
import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.fluids.IFluidHandler;

public class WailaCompat extends CompatBase
{
    public static void callbackRegister(IWailaRegistrar registrar)
    {
        WailaLabelProvider labelProvider = new WailaLabelProvider();
        registrar.registerHeadProvider(labelProvider, IInventory.class);
        registrar.registerHeadProvider(labelProvider, IFluidHandler.class);
        registrar.registerHeadProvider(labelProvider, IEnergyProvider.class);
        registrar.registerHeadProvider(labelProvider, IEnergyReceiver.class);
        registrar.registerHeadProvider(labelProvider, TileEntityClusterElement.class);
        registrar.registerHeadProvider(labelProvider, TileEntityCluster.class);

        registrar.registerBodyProvider(new ClusterDataProvider(), TileEntityCluster.class);
        registrar.registerBodyProvider(new RedstoneOutputDataProvider(), BlockCableOutput.class);

        registrar.registerStackProvider(new CamouflageDataProvider(), BlockCamouflageBase.class);
    }

    @Override
    protected void init()
    {
        FMLInterModComms.sendMessage(Mods.WAILA, "register", "advancedsystemsmanager.compatibility.waila.WailaCompat.callbackRegister");
    }
}
