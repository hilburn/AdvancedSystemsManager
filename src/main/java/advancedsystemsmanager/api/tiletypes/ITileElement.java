package advancedsystemsmanager.api.tiletypes;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public interface ITileElement
{
    void setSubtype(int subtype);

    boolean isPartOfCluster();

    void setPartOfCluster(boolean partOfCluster);

    void writeContentToNBT(NBTTagCompound tag);

    void readContentFromNBT(NBTTagCompound tag);

    @SideOnly(Side.CLIENT)
    IIcon getIcon(int side);
}
