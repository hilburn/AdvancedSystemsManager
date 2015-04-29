package advancedsystemsmanager.api;

import net.minecraft.tileentity.TileEntity;

public interface ISystemType<Type>
{
    public boolean isInstance(TileEntity tileEntity);

    public Type getType(TileEntity tileEntity);

    public boolean isGroup();

    public String getName();
}
