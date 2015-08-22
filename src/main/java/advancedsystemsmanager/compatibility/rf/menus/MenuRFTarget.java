package advancedsystemsmanager.compatibility.rf.menus;

import advancedsystemsmanager.client.gui.GuiBase;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.Menu;
import advancedsystemsmanager.flow.menus.MenuTarget;
import net.minecraft.nbt.NBTTagCompound;

public class MenuRFTarget extends MenuTarget
{

    public MenuRFTarget(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    public Button getSecondButton()
    {
        return null;
    }

    @Override
    public void drawAdvancedComponent(GuiBase guiManager, int i, int i1)
    {

    }

    @Override
    public void copyAdvancedSetting(Menu menu, int i)
    {

    }

    @Override
    public void loadAdvancedComponent(NBTTagCompound nbtTagCompound, int i)
    {

    }

    @Override
    public void saveAdvancedComponent(NBTTagCompound nbtTagCompound, int i)
    {

    }

    @Override
    public void refreshAdvancedComponent()
    {

    }

    @Override
    public void onAdvancedClick(int i, int i1, int i2)
    {

    }
}
