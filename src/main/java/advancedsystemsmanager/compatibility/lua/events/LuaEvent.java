package advancedsystemsmanager.compatibility.lua.events;

import advancedsystemsmanager.compatibility.ModCompat;
import advancedsystemsmanager.reference.Mods;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.peripheral.IComputerAccess;

import java.util.List;

public abstract class LuaEvent
{
    private String name;

    public LuaEvent(String name)
    {
        this.name = name;
    }

    public void announce(List<IComputerAccess> computers, Object... message)
    {
        if (ModCompat.COMPUTERCRAFT.isLoaded())
            computerCraftAnnounce(computers, message);
//        if (ModList.opencomputers.isLoaded())
//            openComputersAnnounce(cTE, message);
    }

    @Optional.Method(modid = Mods.COMPUTERCRAFT)
    public void computerCraftAnnounce(List<IComputerAccess> computerList, Object... message)
    {
        for (IComputerAccess computer : computerList)
        {
            computer.queueEvent(name, message);
        }
    }

//    @Optional.Method(modid = Mods.OPENCOMPUTERS)
//    public void openComputersAnnounce(TilePeripheralBase te, Object... message)
//    {
//        for (Object context : te.getContext())
//        {
//            ((Context)context).signal(name, message);
//        }
//    }
}
