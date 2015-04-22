package advancedfactorymanager.compatibility.computercraft;

import advancedfactorymanager.compatibility.CompatBase;

public class ComputerCraftCompat extends CompatBase
{

    @Override
    public void init()
    {
        PeripheralProvider.register();
    }
}
