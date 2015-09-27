package advancedsystemsmanager.compatibility.rf;

import advancedsystemsmanager.api.ISystemType;
import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.blocks.TileFactory;
import advancedsystemsmanager.compatibility.CompatBase;
import advancedsystemsmanager.compatibility.ModCompat;
import advancedsystemsmanager.compatibility.rf.commands.CommandRFInput;
import advancedsystemsmanager.compatibility.rf.commands.CommandRFOutput;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.registry.ClusterRegistry;
import advancedsystemsmanager.registry.CommandRegistry;
import advancedsystemsmanager.registry.SystemTypeRegistry;
import advancedsystemsmanager.tileentities.manager.TileEntityManager;
import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;

import java.util.Set;

public class RFCompat extends CompatBase
{
    public static ISystemType RF_PROVIDER;
    public static ISystemType RF_RECEIVER;
    public static ISystemType RF_STORAGE;
    public static ICommand RF_INPUT_COMMAND;
    public static ICommand RF_OUTPUT_COMMAND;
    public static ICommand RF_CONDITION_COMMAND;
    public static TileFactory RF;

    @Override
    protected void init()
    {
        RF_PROVIDER = SystemTypeRegistry.register(new SystemTypeRegistry.SystemType<IEnergyProvider>(Names.RF_INPUT)
        {
            @Override
            public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
            {
                return tileEntity instanceof IEnergyProvider;
            }

            @Override
            protected String getErrorName()
            {
                return Names.NO_RF_ERROR;
            }
        });
        RF_RECEIVER = SystemTypeRegistry.register(new SystemTypeRegistry.SystemType<IEnergyReceiver>(Names.RF_OUTPUT)
        {
            @Override
            public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
            {
                return tileEntity instanceof IEnergyReceiver;
            }

            @Override
            protected String getErrorName()
            {
                return Names.NO_RF_ERROR;
            }
        });
        RF_STORAGE = SystemTypeRegistry.register(new SystemTypeRegistry.SystemType<IEnergyConnection>(Names.RF_STORAGE)
        {
            @Override
            public boolean isInstance(TileEntityManager manager, TileEntity tileEntity)
            {
                return tileEntity instanceof IEnergyProvider || tileEntity instanceof IEnergyReceiver;
            }

            @Override
            public boolean containsGroup(Set<ISystemType> types)
            {
                return types.contains(RF_RECEIVER) || types.contains(RF_PROVIDER);
            }

            @Override
            protected String getErrorName()
            {
                return Names.NO_RF_ERROR;
            }
        });
        RF_INPUT_COMMAND = CommandRegistry.registerCommand(new CommandRFInput());
        RF_OUTPUT_COMMAND = CommandRegistry.registerCommand(new CommandRFOutput());
//        StevesEnum.RF_CONDITION = StevesEnum.addComponentType(19, ICommand.CommandType.COMMAND_CONTROL, StevesEnum.RF_CONDITION_SHORT, StevesEnum.RF_CONDITION_LONG, new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION}, MenuRFStorage.class, MenuRFTarget.class, MenuRFCondition.class, MenuResult.class);
        ClusterRegistry.register(new TileFactory.Cluster(TileEntityRFNode.class, new String[]{Names.CABLE_RF}));
    }

    @Override
    protected void postInit()
    {
        ModCompat.registerLabel(IEnergyProvider.class);
        ModCompat.registerLabel(IEnergyReceiver.class);
    }
}
