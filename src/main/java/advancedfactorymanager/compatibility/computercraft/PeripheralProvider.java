package advancedfactorymanager.compatibility.computercraft;

import advancedfactorymanager.reference.Mods;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.world.World;

@Optional.Interface(iface = "dan200.computercraft.api.peripheral.IPeripheralProvider", modid = Mods.COMPUTERCRAFT)
public class PeripheralProvider implements IPeripheralProvider
{
    public static void register()
    {
        ComputerCraftAPI.registerPeripheralProvider(new PeripheralProvider());
    }

    @Override
    @Optional.Method(modid = Mods.COMPUTERCRAFT)
    public IPeripheral getPeripheral(World world, int x, int y, int z, int side)
    {

        return null;
    }
}
