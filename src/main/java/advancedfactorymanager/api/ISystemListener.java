package advancedfactorymanager.api;


import advancedfactorymanager.tileentities.TileEntityManager;

public interface ISystemListener {

    void added(TileEntityManager owner);
    void removed(TileEntityManager owner);

}
