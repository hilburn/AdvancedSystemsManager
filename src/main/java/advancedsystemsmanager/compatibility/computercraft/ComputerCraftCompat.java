package advancedsystemsmanager.compatibility.computercraft;

import advancedsystemsmanager.compatibility.CompatBase;

public class ComputerCraftCompat extends CompatBase
{

    @Override
    public void init()
    {
        PeripheralProvider.register();
    }
}
