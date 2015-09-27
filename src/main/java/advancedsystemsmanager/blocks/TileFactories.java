package advancedsystemsmanager.blocks;

import advancedsystemsmanager.api.tileentities.ITileFactory;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.tileentities.*;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.IIcon;

import static advancedsystemsmanager.reference.Names.*;

public class TileFactories
{
    public static final TileFactories INSTANCE = new TileFactories();

    public static ITileFactory MANAGER = registerFactory(new TileFactory(TileEntityManager.class, 0, Names.MANAGER, TOP_SUFFIX, BOTTOM_SUFFIX){
        @Override
        @SideOnly(Side.CLIENT)
        public IIcon getIcon(int side)
        {
            return side == 0 ? super.icons[2] : side == 1 ? super.icons[1] : super.icons[0];
        }
    });
    public static ITileFactory BLOCK_GATE = registerDirectionElement(TileEntityBlockGate.class, CABLE_BLOCK_GATE, SIDE_SUFFIX, FACE_SUFFIX, DIRECTION_SUFFIX);
    public static ITileFactory BUD = registerClusterElement(TileEntityBUD.class, CABLE_BUD);
    public static ITileFactory CAMO = registerClusterElement(TileEntityCamouflage.class, 0, CABLE_CAMO);
    public static ITileFactory CAMO_DOUBLE = registerClusterElement(TileEntityCamouflage.class, 1, CABLE_CAMO_INSIDE);
    public static ITileFactory CAMO_TRANSFORM = registerClusterElement(TileEntityCamouflage.class, 2, CABLE_CAMO_TRANSFORM);
    public static ITileFactory CLUSTER = registerNonCluser(TileEntityCluster.class, 0, CABLE_CLUSTER, SIDE_SUFFIX);
    public static ITileFactory CLUSTER_ADVANCED = registerNonCluser(TileEntityCluster.class, 1, CABLE_CLUSTER + ADVANCED_SUFFIX, SIDE_SUFFIX);
    public static ITileFactory CREATIVE = registerClusterElement(TileEntityCreative.class, CABLE_CREATIVE);
    public static ITileFactory EMITTER = registerClusterElement(TileEntityEmitter.class, CABLE_OUTPUT, WEAK_SUFFIX, IDLE_SUFFIX);
    public static ITileFactory FLUID_GATE = registerDirectionElement(TileEntityFluidGate.class, CABLE_FLUID_GATE, SIDE_SUFFIX);
    public static ITileFactory RECEIVER = registerClusterElement(TileEntityReceiver.class, CABLE_INPUT);
    public static ITileFactory RELAY = registerInterfaceElement(TileEntityRelay.class, CABLE_RELAY, SIDE_SUFFIX);
    public static ITileFactory RELAY_ADVANCED = registerInterfaceElement(TileEntityRelay.class, 1, CABLE_RELAY + ADVANCED_SUFFIX, SIDE_SUFFIX);
    public static ITileFactory SIGN = registerDirectionElement(TileEntitySignUpdater.class, CABLE_SIGN, SIDE_SUFFIX);
    public static ITileFactory VALVE = registerDirectionElement(TileEntityValve.class, CABLE_VALVE, SIDE_SUFFIX);
    public static ITileFactory VALVE_ADVANCED = registerDirectionElement(TileEntityValve.class, 1, CABLE_VALVE + ADVANCED_SUFFIX, SIDE_SUFFIX);
    public static ITileFactory VOID = registerInterfaceElement(TileEntityVoid.class, CABLE_VOID);

    private static ITileFactory registerClusterElement(Class<? extends TileEntityElementBase> clazz, String... iconNames)
    {
        return registerClusterElement(clazz, 0, iconNames);
    }

    private static ITileFactory registerClusterElement(Class<? extends TileEntityElementBase> clazz, int subtype, String... iconNames)
    {
        return registerFactory(new TileFactory.Cluster(clazz, subtype, iconNames));
    }

    private static ITileFactory registerDirectionElement(Class<? extends TileEntityElementBase> clazz, String... iconNames)
    {
        return registerDirectionElement(clazz, 0, iconNames);
    }

    private static ITileFactory registerDirectionElement(Class<? extends TileEntityElementBase> clazz, int subtype, String... iconNames)
    {
        return registerFactory(new TileFactory.Directional(clazz, subtype, iconNames));
    }

    private static ITileFactory registerInterfaceElement(Class<? extends TileEntityElementBase> clazz, String... iconNames)
    {
        return registerInterfaceElement(clazz, 0, iconNames);
    }

    private static ITileFactory registerInterfaceElement(Class<? extends TileEntityElementBase> clazz, int subtype, String... iconNames)
    {
        return registerFactory(new TileFactory.Interface(clazz, subtype, iconNames));
    }

    private static ITileFactory registerNonCluser(Class<? extends TileEntityElementBase> clazz, int subtype, String... iconNames)
    {
        return registerFactory(new TileFactory(clazz, subtype, iconNames));
    }
    
    private static ITileFactory registerFactory(ITileFactory factory)
    {
        ClusterRegistry.register(factory);
        return factory;
    }
}
