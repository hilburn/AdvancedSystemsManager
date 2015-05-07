package advancedsystemsmanager.api.tileentities;


import advancedsystemsmanager.tileentities.manager.TileEntityManager;

public interface ISystemListener
{

    void added(TileEntityManager owner);

    void removed(TileEntityManager owner);

}
