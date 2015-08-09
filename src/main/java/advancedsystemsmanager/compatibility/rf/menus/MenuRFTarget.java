package advancedsystemsmanager.compatibility.rf.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.client.gui.GuiManager;
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
    protected void setDirection(int dir, boolean val)
    {
        super.setDirection(dir, val);
        if (!getParent().getManager().getWorldObj().isRemote)
        {
            Menu menu = getParent().getMenu(0);
            if (menu instanceof MenuRF)
            {
                ((MenuRF) menu).updateConnectedNodes();
            }
        }
    }

    @Override
    public Button getSecondButton()
    {
        return null;
    }

    @Override
    public void drawAdvancedComponent(GuiManager guiManager, int i, int i1)
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
