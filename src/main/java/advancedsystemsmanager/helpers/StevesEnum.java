package advancedsystemsmanager.helpers;

import advancedsystemsmanager.registry.ComponentType;
import advancedsystemsmanager.registry.ConnectionOption;
import advancedsystemsmanager.registry.ConnectionSet;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.util.ClusterMethodRegistration;
import advancedsystemsmanager.util.ConnectionBlockType;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraftforge.common.util.EnumHelper;

import java.lang.reflect.Field;

public class StevesEnum
{
    private static final Class[][] localizationClasses = new Class[][]{{Localization.class}};
    private static final Class[][] clusterMethodClasses = new Class[][]{{ClusterMethodRegistration.class}};
    private static final Class[][] connectionTypeClasses = new Class[][]{{ConnectionBlockType.class, Localization.class, Class.class, boolean.class}};
    private static final Class[][] componentTypeClasses = new Class[][]{{ComponentType.class, int.class, Localization.class, Localization.class, ConnectionSet[].class, Class[].class}};
    private static final Class[][] connectionSetClasses = new Class[][]{{ConnectionSet.class, Localization.class, ConnectionOption[].class}};
    private static final Class[][] connectionOptionClasses = new Class[][]{{ConnectionOption.class, Localization.class, ConnectionOption.ConnectionType.class}};

    public static final Localization TYPE_RF = addLocalization("TYPE_RF");
    public static final Localization TYPE_RF_INPUT = addLocalization("TYPE_RF_INPUT");
    public static final Localization TYPE_RF_OUTPUT = addLocalization("TYPE_RF_OUTPUT");
    public static final Localization RF_INPUT_SHORT = addLocalization("RF_INPUT_SHORT");
    public static final Localization RF_INPUT_LONG = addLocalization("RF_INPUT_LONG");
    public static final Localization RF_OUTPUT_SHORT = addLocalization("RF_OUTPUT_SHORT");
    public static final Localization RF_OUTPUT_LONG = addLocalization("RF_OUTPUT_LONG");
    public static final Localization RF_CONDITION_SHORT = addLocalization("RF_CONDITION_SHORT");
    public static final Localization RF_CONDITION_LONG = addLocalization("RF_CONDITION_LONG");
    public static final Localization RF_CONDITION_MENU = addLocalization("RF_CONDITION_MENU");
    public static final Localization RF_CONDITION_INFO = addLocalization("RF_CONDITION_INFO");
    public static final Localization RF_CONDITION_ERROR = addLocalization("RF_CONDITION_ERROR");
    public static final Localization NO_RF_ERROR = addLocalization("NO_RF_ERROR");
    public static final Localization COPY_COMMAND = addLocalization("COPY_COMMAND");
    public static final Localization BELOW = addLocalization("BELOW");
    public static final Localization DELAY_TRIGGER = addLocalization("DELAY_TRIGGER");
    public static final Localization DELAY_OUTPUT = addLocalization("DELAY_OUTPUT");
    public static final Localization DELAY_INFO = addLocalization("DELAY_INFO");
    public static final Localization DELAY_ERROR = addLocalization("DELAY_ERROR");
    public static final Localization DELAY_RESTART = addLocalization("DELAY_RESTART");
    public static final Localization DELAY_IGNORE = addLocalization("DELAY_IGNORE");
    public static final ConnectionBlockType RF_PROVIDER = addConnectionBlockType("RF_PROVIDER", TYPE_RF_INPUT, IEnergyProvider.class, false);
    public static final ConnectionBlockType RF_RECEIVER = addConnectionBlockType("RF_RECEIVER", TYPE_RF_OUTPUT, IEnergyReceiver.class, false);
    public static final ConnectionBlockType RF_CONNECTION = addConnectionBlockType("RF_CONNECTION", TYPE_RF, IEnergyConnection.class, false);
    public static final ComponentType RF_INPUT = addComponentType("RF_INPUT", 17, RF_INPUT_SHORT, RF_INPUT_LONG, new ConnectionSet[]{ConnectionSet.STANDARD}, MenuRFInput.class, MenuTargetRF.class, MenuResult.class);
    public static final ComponentType RF_OUTPUT = addComponentType("RF_OUTPUT", 18, RF_OUTPUT_SHORT, RF_OUTPUT_LONG, new ConnectionSet[]{ConnectionSet.STANDARD}, MenuRFOutput.class, MenuTargetRF.class, MenuResult.class);
    public static final ComponentType RF_CONDITION = addComponentType("RF_CONDITION", 19, RF_CONDITION_SHORT, RF_CONDITION_LONG, new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION}, MenuRFStorage.class, MenuTargetRF.class, MenuRFCondition.class, MenuResult.class);
    public static final ClusterMethodRegistration CONNECT_ENERGY = addClusterMethod("CONNECT_ENERGY");
    public static final ClusterMethodRegistration EXTRACT_ENERGY = addClusterMethod("EXTRACT_ENERGY");
    public static final ClusterMethodRegistration ENERGY = addClusterMethod("ENERGY");
    public static final ConnectionOption DELAYED_OUTPUT = addConnectionMethod("DELAYED_OUTPUT", DELAY_OUTPUT, ConnectionOption.ConnectionType.OUTPUT);
    public static final ConnectionSet DELAYED = addConnectionSet("DELAY_TRIGGER", DELAY_TRIGGER, new ConnectionOption[]{ConnectionOption.STANDARD_INPUT, DELAYED_OUTPUT});


    public static Localization addLocalization(String key)
    {
        return EnumHelper.addEnum(localizationClasses, Localization.class, key);
    }

    public static ConnectionBlockType addConnectionBlockType(String key, Localization localization, Class theClass, boolean group)
    {
        return EnumHelper.addEnum(connectionTypeClasses, ConnectionBlockType.class, key, localization, theClass, group);
    }

    public static ComponentType addComponentType(String key, int index, Localization shortName, Localization longName, ConnectionSet[] connections, Class... classes)
    {
        return EnumHelper.addEnum(componentTypeClasses, ComponentType.class, key, index, shortName, longName, connections, classes);
    }

    public static ClusterMethodRegistration addClusterMethod(String key)
    {
        return EnumHelper.addEnum(clusterMethodClasses, ClusterMethodRegistration.class, key);
    }

    public static ConnectionSet addConnectionSet(String key, Localization localization, ConnectionOption[] options)
    {
        return EnumHelper.addEnum(connectionSetClasses, ConnectionSet.class, key, localization, options);
    }

    public static ConnectionOption addConnectionMethod(String key, Localization localization, ConnectionOption.ConnectionType type)
    {
        return EnumHelper.addEnum(connectionOptionClasses, ConnectionOption.class, key, localization, type);
    }

    public static void applyEnumHacks()
    {
        try
        {
            Field classes = ComponentType.class.getDeclaredField("classes");
            Field sets = ComponentType.class.getDeclaredField("sets");
            classes.setAccessible(true);
            sets.setAccessible(true);
            classes.set(ComponentType.TRIGGER, new Class[]{MenuReceivers.class, MenuBUDs.class, MenuInterval.class, MenuRedstoneSidesTrigger.class, MenuRedstoneStrength.class, MenuUpdateBlock.class, MenuDelayed.class, MenuResult.class});
            sets.set(ComponentType.TRIGGER, new ConnectionSet[]{ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE, ConnectionSet.BUD, DELAYED});
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
