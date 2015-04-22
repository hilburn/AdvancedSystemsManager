package advancedsystemsmanager.flow.menus;

import advancedsystemsmanager.flow.FlowComponent;
import advancedsystemsmanager.tileentities.TileEntityRFNode;
import advancedsystemsmanager.util.ConnectionBlock;
import advancedsystemsmanager.util.ConnectionBlockType;

public abstract class MenuRF extends MenuContainer
{
    public MenuRF(FlowComponent parent, ConnectionBlockType validType)
    {
        super(parent, validType);
    }

    public boolean isSelected(TileEntityRFNode node)
    {
        for (ConnectionBlock block : getParent().getManager().getConnectedInventories())
        {
            if (block.getTileEntity() == node) return getSelectedInventories().contains(block.getId());
        }
        return false;
    }

    public void updateConnectedNodes()
    {
        if (!getParent().getManager().getWorldObj().isRemote)
        {
            for (ConnectionBlock connection : getParent().getManager().getConnectedInventories())
            {
                if (connection.getTileEntity() instanceof TileEntityRFNode)
                    ((TileEntityRFNode)connection.getTileEntity()).update(getParent());
            }
        }
    }
}
