package advancedsystemsmanager.api.gui;

import advancedsystemsmanager.tileentities.manager.TileEntityManager;

import java.util.List;

public interface IManagerButtonProvider
{
    List<IManagerButton> getButtons(TileEntityManager manager);
}
