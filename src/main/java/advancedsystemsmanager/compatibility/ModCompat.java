package advancedsystemsmanager.compatibility;

import advancedsystemsmanager.compatibility.computercraft.ComputerCraftCompat;
import advancedsystemsmanager.compatibility.jabba.JabbaCompat;
import advancedsystemsmanager.compatibility.waila.WailaCompat;
import advancedsystemsmanager.reference.Mods;
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
        if (compatClass != null)
            compatClass.load(this);
    }

    @SuppressWarnings(value = "unchecked")
    public <T extends CompatBase> T getCompatClass()
    {
        return (T)compatClass;
    }

    public static void loadAll()
    {
        for (ModCompat compat : values())
            compat.load();
    }
}
