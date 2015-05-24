package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.tileentities.IClusterTile;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.tileentities.TileEntityCluster;
import advancedsystemsmanager.tileentities.TileEntityClusterElement;
import advancedsystemsmanager.tileentities.TileEntityRelay;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableRelay extends BlockCableDirectionAdvanced<TileEntityRelay>
{
    public BlockCableRelay()
    {
        super(Names.CABLE_RELAY);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2)
    {
        return new TileEntityRelay();
    }

    @Override
    protected String getFrontTextureName(boolean isAdvanced)
    {
        return isAdvanced ? "cable_relay_1" : "cable_relay";
    }

    @Override
    protected String getSideTextureName(boolean isAdvanced)
    {
        return "cable_idle";
    }


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item)
    {
        super.onBlockPlacedBy(world, x, y, z, entity, item);

        TileEntityRelay relay = getTileEntity(world, x, y, z);
        if (relay != null && isAdvanced(relay.getBlockMetadata()) && !world.isRemote)
        {
            relay.setOwner(entity);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xSide, float ySide, float zSide)
    {
        TileEntityRelay relay = getTileEntity(world, x, y, z);
        if (relay != null && isAdvanced(relay.getBlockMetadata()))
        {
            if (!world.isRemote)
            {
                FMLNetworkHandler.openGui(player, AdvancedSystemsManager.INSTANCE, 1, world, x, y, z);
            }

            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public boolean isInstance(IClusterTile tile)
    {
        return tile instanceof TileEntityRelay;
    }
}
