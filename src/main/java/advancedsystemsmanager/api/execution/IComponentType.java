package advancedsystemsmanager.api.execution;

import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.registry.ConnectionSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;

public interface IComponentType
{
    int getId();

    public ConnectionSet[] getSets();

    Class<? extends Menu>[] getClasses();

    public String getName();

    public String getLongName();

    int getX();

    int getY();

    @SideOnly(Side.CLIENT)
    ResourceLocation getTexture();
}
