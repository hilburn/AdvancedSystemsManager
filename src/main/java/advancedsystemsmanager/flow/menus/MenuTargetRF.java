package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.GuiManager;
import net.minecraft.nbt.NBTTagCompound;

public class MenuTargetRF extends MenuTarget
{

    public MenuTargetRF(FlowComponent parent)
    {
        super(parent);
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
