package advancedsystemsmanager.api.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public interface ITileElement
{
    void setSubtype(int subtype);

    boolean isPartOfCluster();

    void setPartOfCluster(boolean partOfCluster);

    void writeToItemNBT(NBTTagCompound tag);

    void readFromItemNBT(NBTTagCompound tag);

    void onAddedToCluster(ICluster cluster);

    @SideOnly(Side.CLIENT)
    IIcon getIcon(int side);
}
