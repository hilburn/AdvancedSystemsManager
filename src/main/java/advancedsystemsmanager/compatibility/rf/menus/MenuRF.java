package advancedsystemsmanager.compatibility.rf.menus;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.flow.menus.MenuContainer;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityRFNode;
import advancedsystemsmanager.util.SystemCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.List;

public class MenuRF extends MenuContainer
{
    public MenuRF(FlowComponent parent, ISystemType validType)
    {
        super(parent, validType);
    }

    public boolean isSelected(TileEntityRFNode node)
    {
        for (SystemCoord block : getParent().getManager().getConnectedInventories())
        {
            if (block.getTileEntity() == node) return getSelectedInventories().contains(block.getId());
        }
        return false;
    }

    public void updateConnectedNodes()
    {
        if (!getParent().getManager().getWorldObj().isRemote)
        {
            for (SystemCoord connection : getParent().getManager().getConnectedInventories())
            {
                if (connection.getTileEntity() instanceof TileEntityRFNode)
                    ((TileEntityRFNode)connection.getTileEntity()).update(getParent());
            }
        }
    }
}
