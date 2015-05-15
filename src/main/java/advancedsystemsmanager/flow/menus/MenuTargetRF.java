package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.gui.ContainerManager;
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
        return new Button(-300)
        {
            @Override
            public String getLabel()
            {
                return "";
            }

            @Override
            public String getMouseOverText()
            {
                return "";
            }

            @Override
            public void onClicked()
            {
            }
        };
    }

    @Override
    public void copyAdvancedSetting(Menu menu, int i)
    {

    }

    @Override
    public void refreshAdvancedComponentData(ContainerManager containerManager, Menu menu, int i)
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

    @Override
    public void drawAdvancedComponent(GuiManager guiManager, int i, int i1)
    {

    }
}
