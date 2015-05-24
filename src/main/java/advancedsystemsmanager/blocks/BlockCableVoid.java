package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityVoid;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCableVoid extends BlockTileBase
{
    public BlockCableVoid()
    {
        super(Names.CABLE_VOID);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityVoid();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide)
    {
        if (!world.isRemote)
        {
            FMLNetworkHandler.openGui(player, AdvancedSystemsManager.INSTANCE, 1, world, x, y, z);
        }
        return true;
    }
}
