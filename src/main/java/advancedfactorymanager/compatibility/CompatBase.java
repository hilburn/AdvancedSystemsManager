package advancedfactorymanager.compatibility;

import advancedfactorymanager.AdvancedFactoryManager;

public abstract class CompatBase
{
    private ModCompat mod;

    public boolean load(ModCompat mod)
    {
        this.mod = mod;
        if (mod.isLoaded())
        {
            AdvancedFactoryManager.log.info("Loading compatibility for " + mod.getModName());
            init();
            return true;
        } else
        {
            AdvancedFactoryManager.log.info(mod.getModName() + " not loaded - skipping");
        }
        return false;
    }

    protected abstract void init();
}
