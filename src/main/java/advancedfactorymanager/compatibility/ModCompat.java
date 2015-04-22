package advancedfactorymanager.compatibility;

import advancedfactorymanager.compatibility.computercraft.ComputerCraftCompat;
import advancedfactorymanager.compatibility.jabba.JabbaCompat;
import advancedfactorymanager.compatibility.waila.WailaCompat;
import advancedfactorymanager.reference.Mods;
import cpw.mods.fml.common.Loader;

public enum ModCompat
{
    COMPUTERCRAFT(Mods.COMPUTERCRAFT, "ComputerCraft", new ComputerCraftCompat()),
    OPENCOMPUTERS(Mods.OPENCOMPUTERS, "OpenComputers"),
    JABBA(Mods.JABBA, "JABBA", new JabbaCompat()),
    WAILA(Mods.WAILA, "Waila", new WailaCompat());

    private final String modId;
    private final String modName;
    private final CompatBase compatClass;
    private final boolean loaded;

    ModCompat(CompatBase compatClass)
    {
        this("minecraft", "Minecraft", compatClass, true);
    }

    ModCompat(String modId, CompatBase compatClass)
    {
        this(modId, modId, compatClass);
    }

    ModCompat(String modId)
    {
        this(modId, modId, null, Loader.isModLoaded(modId));
    }

    ModCompat(String modId, String modName)
    {
        this(modId, modName, null, Loader.isModLoaded(modId));
    }

    ModCompat(String modId, String modName, CompatBase compatClass)
    {
        this(modId, modName, compatClass, Loader.isModLoaded(modId));
    }

    private ModCompat(String modId, String modName, CompatBase compatClass, boolean loaded)
    {
        this.modId = modId;
        this.modName = modName;
        this.compatClass = compatClass;
        this.loaded = loaded;
    }

    public String getModId()
    {
        return modId;
    }

    public String getModName()
    {
        return modName;
    }

    public boolean isLoaded()
    {
        return loaded;
    }

    private void load()
    {
        compatClass.load(this);
    }

    public <T extends CompatBase> T getCompatClass()
    {
        return (T)compatClass;
    }
}
