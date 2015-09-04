package advancedsystemsmanager.helpers;

import net.minecraft.world.WorldSavedData;

public abstract class SavableData extends WorldSavedData
{
    private boolean needsLoading = true;

    public SavableData(String key)
    {
        super(key);
    }

    public boolean copy(WorldSavedData worldSavedData)
    {
        this.needsLoading = false;
        return copyFrom(worldSavedData == null ? getNew() : worldSavedData);
    }

    public void unload()
    {
        copyFrom(getNew());
        this.needsLoading = true;
    }

    protected abstract SavableData getNew();

    public abstract boolean copyFrom(WorldSavedData worldSavedData);

    public boolean needsLoading()
    {
        return needsLoading;
    }

    public void setNeedsLoading(boolean needsLoading)
    {
        this.needsLoading = needsLoading;
    }
}
