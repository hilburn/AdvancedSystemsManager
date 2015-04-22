package advancedsystemsmanager.compatibility.waila;

import advancedsystemsmanager.blocks.BlockCableOutput;
import advancedsystemsmanager.blocks.BlockCamouflageBase;
import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.helpers.Config;
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
        registrar.registerBodyProvider(labelProvider, IInventory.class);
        registrar.registerBodyProvider(labelProvider, IFluidHandler.class);
        registrar.registerBodyProvider(labelProvider, IEnergyProvider.class);
        registrar.registerBodyProvider(labelProvider, IEnergyReceiver.class);
        registrar.registerBodyProvider(labelProvider, TileEntityClusterElement.class);
        registrar.registerBodyProvider(labelProvider, TileEntityCluster.class);

        registrar.registerBodyProvider(new ClusterDataProvider(), TileEntityCluster.class);
        registrar.registerBodyProvider(new RedstoneOutputDataProvider(), BlockCableOutput.class);

        registrar.registerStackProvider(new CamouflageDataProvider(), BlockCamouflageBase.class);
    }

    @Override
    protected void init()
    {
        if (Config.wailaIntegration)
        {
            FMLInterModComms.sendMessage("Waila", "register", "advancedfactorymanager.compatibility.waila.WailaCompat.callbackRegister");
        }
    }
}
