package advancedsystemsmanager.compatibility;

import advancedsystemsmanager.AdvancedSystemsManager;

public abstract class CompatBase
{
    protected ModCompat mod;

    public boolean load(ModCompat mod)
    {
        this.mod = mod;
        if (mod.isLoaded())
        {
            AdvancedSystemsManager.log.info("Loading compatibility for " + mod.getModName());
            init();
            return true;
        } else
        {
            AdvancedSystemsManager.log.info(mod.getModName() + " not loaded - skipping");
        }
        return false;
    }

    protected abstract void init();
}
