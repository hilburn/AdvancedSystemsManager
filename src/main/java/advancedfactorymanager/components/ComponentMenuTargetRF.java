package advancedfactorymanager.components;

import advancedfactorymanager.interfaces.ContainerManager;
import advancedfactorymanager.interfaces.GuiManager;
import advancedfactorymanager.network.DataReader;
import advancedfactorymanager.network.DataWriter;
import net.minecraft.nbt.NBTTagCompound;

public class ComponentMenuTargetRF extends ComponentMenuTarget
{

    public ComponentMenuTargetRF(FlowComponent parent)
    {
        super(parent);
    }

    @Override
    protected Button getSecondButton()
    {
        return new Button(-300)
        {
            @Override
            protected String getLabel()
            {
                return "";
            }

            @Override
            protected String getMouseOverText()
            {
                return "";
            }

            @Override
            protected void onClicked()
            {
            }
        };
    }

    @Override
    protected void drawAdvancedComponent(GuiManager guiManager, int i, int i1)
    {

    }

    @Override
    protected void refreshAdvancedComponent()
    {

    }

    @Override
    protected void writeAdvancedSetting(DataWriter dataWriter, int i)
    {

    }

    @Override
    protected void readAdvancedSetting(DataReader dataReader, int i)
    {

    }

    @Override
    protected void copyAdvancedSetting(ComponentMenu componentMenu, int i)
    {

    }

    @Override
    protected void onAdvancedClick(int i, int i1, int i2)
    {

    }

    @Override
    protected void loadAdvancedComponent(NBTTagCompound nbtTagCompound, int i)
    {

    }

    @Override
    protected void saveAdvancedComponent(NBTTagCompound nbtTagCompound, int i)
    {

    }

    @Override
    protected void resetAdvancedSetting(int i)
    {

    }

    @Override
    protected void refreshAdvancedComponentData(ContainerManager containerManager, ComponentMenu componentMenu, int i)
    {

    }

    @Override
    protected void readAdvancedNetworkComponent(DataReader dataReader, DataTypeHeader dataTypeHeader, int i)
    {

    }

    @Override
    public void readNetworkComponent(DataReader dr)
    {
        super.readNetworkComponent(dr);
        if (!getParent().getManager().getWorldObj().isRemote)
        {
            ComponentMenu menu = getParent().getMenus().get(0);
            if (menu instanceof ComponentMenuRF)
            {
                ((ComponentMenuRF)menu).updateConnectedNodes();
            }
        }
    }
}
