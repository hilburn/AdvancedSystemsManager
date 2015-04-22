package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.interfaces.ContainerManager;
import advancedsystemsmanager.interfaces.GuiManager;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.network.DataReader;
import advancedsystemsmanager.network.DataWriter;
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
    public void drawAdvancedComponent(GuiManager guiManager, int i, int i1)
    {

    }

    @Override
    public void refreshAdvancedComponent()
    {

    }

    @Override
    public void writeAdvancedSetting(DataWriter dataWriter, int i)
    {

    }

    @Override
    public void readAdvancedSetting(DataReader dataReader, int i)
    {

    }

    @Override
    public void copyAdvancedSetting(Menu menu, int i)
    {

    }

    @Override
    public void onAdvancedClick(int i, int i1, int i2)
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
    public void resetAdvancedSetting(int i)
    {

    }

    @Override
    public void refreshAdvancedComponentData(ContainerManager containerManager, Menu menu, int i)
    {

    }

    @Override
    public void readAdvancedNetworkComponent(DataReader dataReader, DataTypeHeader dataTypeHeader, int i)
    {

    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        super.readNetworkComponent(dr);
        if (!getParent().getManager().getWorldObj().isRemote)
        {
            Menu menu = getParent().getMenus().get(0);
            if (menu instanceof MenuRF)
            {
                ((MenuRF)menu).updateConnectedNodes();
            }
        }
    }
}
