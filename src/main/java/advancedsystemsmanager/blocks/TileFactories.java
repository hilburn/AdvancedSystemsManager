package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.*;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

import java.util.Arrays;

import static advancedsystemsmanager.reference.Names.*;

public class TileFactories
{
    public static final TileFactories INSTANCE = new TileFactories();

    public static TileFactory MANAGER = registerFactory(new TileFactory(TileEntityManager.class, new String[]{Names.MANAGER}, TOP_SUFFIX, BOTTOM_SUFFIX)
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getIcon(int side, int subtype)
        {
            return super.icons[subtype][side == 0 ? 2 : side == 1 ? 1 : 0];
        }
    });
    public static TileFactory BLOCK_GATE = registerDirectionElement(TileEntityBlockGate.class, new String[]{CABLE_BLOCK_GATE}, SIDE_SUFFIX, FACE_SUFFIX, DIRECTION_SUFFIX);
    public static TileFactory BUD = registerClusterElement(TileEntityBUD.class, new String[]{CABLE_BUD});
    public static TileFactory CAMO = registerClusterElement(TileEntityCamouflage.class, new String[]{CABLE_CAMO, CABLE_CAMO_INSIDE, CABLE_CAMO_TRANSFORM});
    public static TileFactory CLUSTER = registerNonCluser(TileEntityCluster.class, new String[]{CABLE_CLUSTER, CABLE_CLUSTER + ADVANCED_SUFFIX}, SIDE_SUFFIX);
    public static TileFactory CREATIVE = registerClusterElement(TileEntityCreative.class, new String[]{CABLE_CREATIVE});
    public static TileFactory EMITTER = registerClusterElement(TileEntityEmitter.class, new String[]{CABLE_OUTPUT}, WEAK_SUFFIX, IDLE_SUFFIX);
    public static TileFactory FLUID_GATE = registerDirectionElement(TileEntityFluidGate.class, new String[]{CABLE_FLUID_GATE}, SIDE_SUFFIX);
    public static TileFactory RECEIVER = registerClusterElement(TileEntityReceiver.class, new String[]{CABLE_INPUT});
    public static TileFactory RELAY = registerInterfaceElement(TileEntityRelay.class, new String[]{CABLE_RELAY, CABLE_RELAY + ADVANCED_SUFFIX}, SIDE_SUFFIX);
    public static TileFactory SIGN = registerDirectionElement(TileEntitySignUpdater.class, new String[]{CABLE_SIGN}, SIDE_SUFFIX);
    public static TileFactory VALVE = registerDirectionElement(TileEntityValve.class, new String[]{CABLE_VALVE, CABLE_VALVE + ADVANCED_SUFFIX}, SIDE_SUFFIX);
    public static TileFactory VOID = registerInterfaceElement(TileEntityVoid.class, new String[]{CABLE_VOID});

    private static TileFactory registerClusterElement(Class<? extends TileEntityElementBase> clazz, String[] subtypes, String... iconNames)
    {
        return registerFactory(new TileFactory.Cluster(clazz, subtypes, iconNames));
    }

    private static TileFactory registerDirectionElement(Class<? extends TileEntityElementBase> clazz, String[] subtypes, String... iconNames)
    {
        return registerFactory(new TileFactory.Directional(clazz, subtypes, iconNames));
    }
    private static TileFactory registerInterfaceElement(Class<? extends TileEntityElementBase> clazz, String[] subtype, String... iconNames)
    {
        return registerFactory(new TileFactory.Interface(clazz, subtype, iconNames));
    }

    private static TileFactory registerNonCluser(Class<? extends TileEntityElementBase> clazz, String[] subtypes, String... iconNames)
    {
        return registerFactory(new TileFactory(clazz, subtypes, iconNames));
    }
    
    private static TileFactory registerFactory(TileFactory factory)
    {
        ClusterRegistry.register(factory);
        return factory;
    }
}
