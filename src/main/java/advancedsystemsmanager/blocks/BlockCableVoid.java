package advancedsystemsmanager.blocks;

import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityVoid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableVoid extends BlockTileBase
{
    protected BlockCableVoid()
    {
        super(Names.CABLE_VOID);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityVoid();
    }
}
