package advancedsystemsmanager.registry;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.flow.ComponentType;
import advancedsystemsmanager.api.execution.IComponentType;
import advancedsystemsmanager.flow.menus.*;
import advancedsystemsmanager.helpers.Localization;
import advancedsystemsmanager.helpers.StevesEnum;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ComponentRegistry
{
    public static IComponentType TRIGGER;
    public static IComponentType INPUT;
    public static IComponentType OUTPUT;
    public static IComponentType CONDITION;
    public static IComponentType FLOW_CONTROL;
    public static IComponentType LIQUID_INPUT;
    public static IComponentType LIQUID_OUTPUT;
    public static IComponentType LIQUID_CONDITION;
    public static IComponentType REDSTONE_EMITTER;
    public static IComponentType REDSTONE_CONDITION;
    public static IComponentType VARIABLE;
    public static IComponentType FOR_EACH;
    public static IComponentType AUTO_CRAFTING;
    public static IComponentType GROUP;
    public static IComponentType NODE;
    public static IComponentType CAMOUFLAGE;
    public static IComponentType SIGN;

    private static List<IComponentType> components = new ArrayList<IComponentType>();
    private static TIntIntHashMap componentsMapping = new TIntIntHashMap(20);

    public static IComponentType registerComponent(IComponentType componentType)
    {
        if (!componentsMapping.containsKey(componentType.getId()))
        {
            componentsMapping.put(componentType.getId(), components.size());
            components.add(componentType);
            return componentType;
        }else
        {
            AdvancedSystemsManager.log.warn("Component ID "+ componentType.getId() + " is already registered by " + components.get(componentType.getId()).getName());
        }
        return null;
    }

    public static IComponentType getComponent(int id)
    {
        return components.get(componentsMapping.get(id));
    }

    public static List<IComponentType> getComponents()
    {
        return components;
    }

    static
    {
        registerComponent(TRIGGER = new ComponentType(0, Localization.TRIGGER_SHORT, Localization.TRIGGER_LONG,
                new ConnectionSet[]{ConnectionSet.CONTINUOUSLY, ConnectionSet.REDSTONE, ConnectionSet.BUD, StevesEnum.DELAYED},
                MenuReceivers.class, MenuBUDs.class, MenuInterval.class, MenuRedstoneSidesTrigger.class, MenuRedstoneStrength.class, MenuUpdateBlock.class, MenuDelayed.class, MenuResult.class));
        registerComponent(INPUT = new ComponentType(1, Localization.INPUT_SHORT, Localization.INPUT_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuInventory.class, MenuTargetInventory.class, MenuItem.class, MenuResult.class));
        registerComponent(OUTPUT = new ComponentType(2, Localization.OUTPUT_SHORT, Localization.OUTPUT_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuInventory.class, MenuTargetInventory.class, MenuItemOutput.class, MenuResult.class));
        registerComponent(CONDITION = new ComponentType(3, Localization.CONDITION_SHORT, Localization.CONDITION_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
                MenuInventoryCondition.class, MenuTargetInventory.class, MenuItemCondition.class, MenuResult.class));
        registerComponent(FLOW_CONTROL = new ComponentType(4, Localization.FLOW_CONTROL_SHORT, Localization.FLOW_CONTROL_LONG,
                new ConnectionSet[]{ConnectionSet.MULTIPLE_INPUT_2, ConnectionSet.MULTIPLE_INPUT_5, ConnectionSet.MULTIPLE_OUTPUT_2, ConnectionSet.MULTIPLE_OUTPUT_5},
                MenuSplit.class, MenuResult.class));
        registerComponent(LIQUID_INPUT = new ComponentType(5, Localization.LIQUID_INPUT_SHORT, Localization.LIQUID_INPUT_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuTank.class, MenuTargetTank.class, MenuLiquid.class, MenuResult.class));
        registerComponent(LIQUID_OUTPUT = new ComponentType(6, Localization.LIQUID_OUTPUT_SHORT, Localization.LIQUID_OUTPUT_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuTank.class, MenuTargetTank.class, MenuLiquidOutput.class, MenuResult.class));
        registerComponent(LIQUID_CONDITION = new ComponentType(7, Localization.LIQUID_CONDITION_SHORT, Localization.LIQUID_CONDITION_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
                MenuTankCondition.class, MenuTargetTank.class, MenuLiquidCondition.class, MenuResult.class));
        registerComponent(REDSTONE_EMITTER = new ComponentType(8, Localization.REDSTONE_EMITTER_SHORT, Localization.REDSTONE_EMITTER_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuEmitters.class, MenuRedstoneSidesEmitter.class, MenuRedstoneOutput.class, MenuPulse.class, MenuResult.class));
        registerComponent(REDSTONE_CONDITION = new ComponentType(9, Localization.REDSTONE_CONDITION_SHORT, Localization.REDSTONE_CONDITION_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD_CONDITION},
                MenuNodes.class, MenuRedstoneSidesNodes.class, MenuRedstoneStrengthNodes.class, MenuResult.class));
        registerComponent(VARIABLE = new ComponentType(10, Localization.CONTAINER_VARIABLE_SHORT, Localization.CONTAINER_VARIABLE_LONG,
                new ConnectionSet[]{ConnectionSet.EMPTY, ConnectionSet.STANDARD},
                MenuVariable.class, MenuContainerTypesVariable.class, MenuVariableContainers.class, MenuListOrderVariable.class, MenuResult.class));
        registerComponent(FOR_EACH = new ComponentType(11, Localization.FOR_EACH_LOOP_SHORT, Localization.FOR_EACH_LOOP_LONG,
                new ConnectionSet[]{ConnectionSet.FOR_EACH},
                MenuVariableLoop.class, MenuContainerTypes.class, MenuListOrder.class, MenuResult.class));
        registerComponent(AUTO_CRAFTING = new ComponentType(12, Localization.AUTO_CRAFTER_SHORT, Localization.AUTO_CRAFTER_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuCrafting.class, MenuCraftingPriority.class, MenuContainerScrap.class, MenuResult.class));
        registerComponent(GROUP = new ComponentType(13, Localization.GROUP_SHORT, Localization.GROUP_LONG,
                new ConnectionSet[]{ConnectionSet.DYNAMIC},
                MenuGroup.class, MenuResult.class));
        registerComponent(NODE = new ComponentType(14, Localization.NODE_SHORT, Localization.NODE_LONG,
                new ConnectionSet[]{ConnectionSet.INPUT_NODE, ConnectionSet.OUTPUT_NODE},
                MenuResult.class));
        registerComponent(CAMOUFLAGE = new ComponentType(15, Localization.CAMOUFLAGE_SHORT, Localization.CAMOUFLAGE_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuCamouflage.class, MenuCamouflageShape.class, MenuCamouflageInside.class, MenuCamouflageSides.class, MenuCamouflageItems.class, MenuResult.class));
        registerComponent(SIGN = new ComponentType(16, Localization.SIGN_SHORT, Localization.SIGN_LONG,
                new ConnectionSet[]{ConnectionSet.STANDARD},
                MenuSigns.class, MenuSignText.class, MenuResult.class));
    }
}
