package advancedsystemsmanager.compatibility;

import advancedsystemsmanager.AdvancedSystemsManager;

public abstract class CompatBase
{
    protected ModCompat mod;

    public boolean preInit(ModCompat mod)
    {
        this.mod = mod;
        if (mod.isLoaded())
        {
            AdvancedSystemsManager.log.info("Loading compatibility for " + mod.getModName());
            return true;
        } else
        {
            AdvancedSystemsManager.log.info(mod.getModName() + " not loaded - skipping");
        }
        return false;
    }

    public boolean isLoaded()
    {
        return mod != null && mod.isLoaded();
    }

    protected void init()
    {
    }

    protected void postInit()
    {
    }
}
