package advancedfactorymanager.blocks;

import advancedfactorymanager.reference.Names;
import advancedfactorymanager.reference.Reference;
import advancedfactorymanager.tileentities.TileEntityAENode;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableAE extends BlockClusterElement
{
    public BlockCableAE()
    {
        super(Names.CABLE_AE);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityAENode();
    }

    @Override
    public void registerBlockIcons(IIconRegister ir)
    {
        this.blockIcon = ir.registerIcon(Reference.ID + ":" + Names.CABLE_AE);
    }
}
