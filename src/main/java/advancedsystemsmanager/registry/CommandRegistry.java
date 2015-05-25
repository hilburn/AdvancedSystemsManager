package advancedsystemsmanager.registry;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.flow.execution.commands.*;
import advancedsystemsmanager.flow.setting.ItemSetting;
import gnu.trove.map.hash.TIntIntHashMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class CommandRegistry
{
    public static ICommand TRIGGER;
    public static CommandBase<ItemStack> INPUT;
    public static ICommand OUTPUT;
    public static ICommand CONDITION;
    public static ICommand FLOW_CONTROL;
    public static CommandBase<Fluid> LIQUID_INPUT;
    public static ICommand LIQUID_OUTPUT;
    public static ICommand LIQUID_CONDITION;
    public static ICommand REDSTONE_EMITTER;
    public static ICommand REDSTONE_CONDITION;
    public static ICommand VARIABLE;
    public static ICommand FOR_EACH;
    public static ICommand AUTO_CRAFTING;
    public static ICommand GROUP;
    public static ICommand NODE;
    public static ICommand CAMOUFLAGE;
    public static ICommand SIGN;

    private static List<ICommand> components = new ArrayList<ICommand>();
    private static TIntIntHashMap componentsMapping = new TIntIntHashMap(20);

    public static ICommand registerCommand(ICommand componentType)
    {
        if (!componentsMapping.containsKey(componentType.getId()))
        {
            componentsMapping.put(componentType.getId(), components.size());
            components.add(componentType);
            return componentType;
        } else
        {
            AdvancedSystemsManager.log.warn("Component ID " + componentType.getId() + " is already registered by " + components.get(componentType.getId()).getName());
        }
        return null;
    }

    public static ICommand getCommand(int id)
    {
        return components.get(componentsMapping.get(id));
    }

    public static List<ICommand> getComponents()
    {
        return components;
    }

    static
    {
//        registerCommand(CONDITION = new Command(3, CommandType.COMMAND_CONTROL, Localization.CONDITION_SHORT, Localization.CONDITION_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
//                MenuInventoryCondition.class, MenuTargetInventory.class, MenuItemCondition.class));
//        registerCommand(FLOW_CONTROL = new Command(4, CommandType.COMMAND_CONTROL, Localization.FLOW_CONTROL_SHORT, Localization.FLOW_CONTROL_LONG,
//                new ConnectionSet[]{ConnectionSet.MULTIPLE_INPUT_2, ConnectionSet.MULTIPLE_INPUT_5, ConnectionSet.MULTIPLE_OUTPUT_2, ConnectionSet.MULTIPLE_OUTPUT_5},
//                MenuSplit.class));
//        registerCommand(LIQUID_CONDITION = new Command(7, CommandType.COMMAND_CONTROL, Localization.LIQUID_CONDITION_SHORT, Localization.LIQUID_CONDITION_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
//                MenuTankCondition.class, MenuTargetTank.class, MenuLiquidCondition.class));
//        registerCommand(REDSTONE_EMITTER = new Command(8, CommandType.OUTPUT, Localization.REDSTONE_EMITTER_SHORT, Localization.REDSTONE_EMITTER_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD},
//                MenuEmitters.class, MenuRedstoneSidesEmitter.class, MenuRedstoneOutput.class, MenuPulse.class));
//        registerCommand(REDSTONE_CONDITION = new Command(9, CommandType.COMMAND_CONTROL, Localization.REDSTONE_CONDITION_SHORT, Localization.REDSTONE_CONDITION_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
//                MenuNodes.class, MenuRedstoneSidesNodes.class, MenuRedstoneStrengthNodes.class));
//        registerCommand(VARIABLE = new Command(10, CommandType.MISC, Localization.CONTAINER_VARIABLE_SHORT, Localization.CONTAINER_VARIABLE_LONG,
//                new ConnectionSet[]{ConnectionSet.EMPTY, ConnectionSet.STANDARD},
//                MenuVariable.class, MenuContainerTypesVariable.class, MenuVariableContainers.class, MenuListOrderVariable.class));
//        registerCommand(FOR_EACH = new Command(11, CommandType.COMMAND_CONTROL, Localization.FOR_EACH_LOOP_SHORT, Localization.FOR_EACH_LOOP_LONG,
//                new ConnectionSet[]{ConnectionSet.FOR_EACH},
//                MenuVariableLoop.class, MenuContainerTypes.class, MenuListOrder.class));
//        registerCommand(AUTO_CRAFTING = new Command(12, CommandType.CRAFTING, Localization.AUTO_CRAFTER_SHORT, Localization.AUTO_CRAFTER_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD},
//                MenuCrafting.class, MenuCraftingPriority.class, MenuContainerScrap.class));
//        registerCommand(GROUP = new Command(13, CommandType.MISC, Localization.GROUP_SHORT, Localization.GROUP_LONG,
//                new ConnectionSet[]{ConnectionSet.DYNAMIC},
//                MenuGroup.class));
//        registerCommand(NODE = new Command(14, CommandType.MISC, Localization.NODE_SHORT, Localization.NODE_LONG,
//                new ConnectionSet[]{ConnectionSet.INPUT_NODE, ConnectionSet.OUTPUT_NODE},
//                MenuResult.class));
//        registerCommand(SIGN = new Command(16, CommandType.MISC, Localization.SIGN_SHORT, Localization.SIGN_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD},
//                MenuSigns.class, MenuSignText.class));
        registerCommand(new CommandTrigger());
        registerCommand(INPUT = new CommandItemInput());
        registerCommand(new CommandItemOutput());
        registerCommand(LIQUID_INPUT = new CommandFluidInput());
        registerCommand(new CommandFluidOutput());

        registerCommand(new CommandCamouflage());
    }
}
