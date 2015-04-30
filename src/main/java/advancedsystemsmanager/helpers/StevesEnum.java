package advancedsystemsmanager.helpers;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.registry.ConnectionOption;

public class StevesEnum
{
//    private static final Class[][] localizationClasses = new Class[][]{{Localization.class}};
//    private static final Class[][] clusterMethodClasses = new Class[][]{{ClusterMethodRegistration.class}};
//    private static final Class[][] connectionTypeClasses = new Class[][]{{ISystemType.class, Localization.class, Class.class, boolean.class}};
//    private static final Class[][] connectionSetClasses = new Class[][]{{ConnectionSet.class, Localization.class, ConnectionOption[].class}};
//    private static final Class[][] connectionOptionClasses = new Class[][]{{ConnectionOption.class, Localization.class, ConnectionOption.ConnectionType.class}};

//    public static final Localization TYPE_RF = addLocalization("TYPE_RF");
//    public static final Localization TYPE_RF_INPUT = addLocalization("TYPE_RF_INPUT");
//    public static final Localization TYPE_RF_OUTPUT = addLocalization("TYPE_RF_OUTPUT");
//    public static final Localization RF_INPUT_SHORT = addLocalization("RF_INPUT_SHORT");
//    public static final Localization RF_INPUT_LONG = addLocalization("RF_INPUT_LONG");
//    public static final Localization RF_OUTPUT_SHORT = addLocalization("RF_OUTPUT_SHORT");
//    public static final Localization RF_OUTPUT_LONG = addLocalization("RF_OUTPUT_LONG");
//    public static final Localization RF_CONDITION_SHORT = addLocalization("RF_CONDITION_SHORT");
//    public static final Localization RF_CONDITION_LONG = addLocalization("RF_CONDITION_LONG");
//    public static final Localization RF_CONDITION_MENU = addLocalization("RF_CONDITION_MENU");
//    public static final Localization RF_CONDITION_INFO = addLocalization("RF_CONDITION_INFO");
//    public static final Localization RF_CONDITION_ERROR = addLocalization("RF_CONDITION_ERROR");
//    public static final Localization NO_RF_ERROR = addLocalization("NO_RF_ERROR");
//    public static final Localization COPY_COMMAND = addLocalization("COPY_COMMAND");
//    public static final Localization BELOW = addLocalization("BELOW");
//    public static final Localization DELAY_TRIGGER = addLocalization("DELAY_TRIGGER");
//    public static final Localization DELAY_OUTPUT = addLocalization("DELAY_OUTPUT");
//    public static final Localization DELAY_INFO = addLocalization("DELAY_INFO");
//    public static final Localization DELAY_ERROR = addLocalization("DELAY_ERROR");
//    public static final Localization DELAY_RESTART = addLocalization("DELAY_RESTART");
//    public static final Localization DELAY_IGNORE = addLocalization("DELAY_IGNORE");
    public static  ISystemType RF_PROVIDER;// = addConnectionBlockType("RF_PROVIDER", TYPE_RF_INPUT, IEnergyProvider.class, false);
    public static  ISystemType RF_RECEIVER; //= addConnectionBlockType("RF_RECEIVER", TYPE_RF_OUTPUT, IEnergyReceiver.class, false);
    public static  ISystemType RF_CONNECTION ;//= addConnectionBlockType("RF_CONNECTION", TYPE_RF, IEnergyConnection.class, false);
//    public static Command RF_INPUT;
//    public static Command RF_OUTPUT;
//    public static Command RF_CONDITION;
    //public static ConnectionOption DELAYED_OUTPUT;// = addConnectionMethod("DELAYED_OUTPUT", DELAY_OUTPUT, ConnectionOption.ConnectionType.OUTPUT);
//    public static final ConnectionSet DELAYED = addConnectionSet("DELAY_TRIGGER", DELAY_TRIGGER, new ConnectionOption[]{ConnectionOption.STANDARD_INPUT, DELAYED_OUTPUT});
//
//
//    public static Localization addLocalization(String key)
//    {
//        return EnumHelper.addEnum(localizationClasses, Localization.class, key);
//    }
//
//    public static ISystemType addConnectionBlockType(String key, Localization localization, Class theClass, boolean group)
//    {
//        return null;//EnumHelper.addEnum(connectionTypeClasses, IConnectionBlockType.class, key, localization, theClass, group);
//    }
//
//    public static Command addComponentType(int index, ICommand.CommandType commandType, Localization shortName, Localization longName, ConnectionSet[] connections, Class... classes)
//    {
//        return (Command)CommandRegistry.registerCommand(new Command(index, commandType, shortName, longName, connections, classes));
//    }
//
//    public static ClusterMethodRegistration addClusterMethod(String key)
//    {
//        return EnumHelper.addEnum(clusterMethodClasses, ClusterMethodRegistration.class, key);
//    }
//
//    public static ConnectionSet addConnectionSet(String key, Localization localization, ConnectionOption[] options)
//    {
//        return EnumHelper.addEnum(connectionSetClasses, ConnectionSet.class, key, localization, options);
//    }
//
//    public static ConnectionOption addConnectionMethod(String key, Localization localization, ConnectionOption.ConnectionType type)
//    {
//        return EnumHelper.addEnum(connectionOptionClasses, ConnectionOption.class, key, localization, type);
//    }
}
