package advancedsystemsmanager.registry;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.execution.ICommand;
import advancedsystemsmanager.flow.execution.commands.*;
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
    public static CommandItemCondition CONDITION;
    public static ICommand FLOW_CONTROL;
    public static CommandBase<Fluid> LIQUID_INPUT;
    public static ICommand LIQUID_OUTPUT;
    public static CommandFluidCondition LIQUID_CONDITION;
    public static ICommand REDSTONE_EMITTER;
    public static ICommand REDSTONE_CONDITION;
    public static ICommand VARIABLE;
    public static ICommand FOR_EACH;
    public static ICommand AUTO_CRAFTING;
    public static ICommand GROUP;
    public static ICommand NODE;
    public static ICommand CAMOUFLAGE;
    public static ICommand SIGN;

    private static ICommand[] commands = new ICommand[20];

    public static ICommand registerCommand(ICommand command)
    {
        if (command.getId() >= commands.length)
        {
            ICommand[] newCommands = new ICommand[command.getId()];
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            commands = newCommands;
        }
        if (commands[command.getId()] == null)
        {
            commands[command.getId()] = command;
            return command;
        } else
        {
            AdvancedSystemsManager.log.warn("Component ID " + command.getId() + " is already registered by " + commands[command.getId()].getName());
        }
        return null;
    }

    public static ICommand getCommand(int id)
    {
        return commands[id];
    }

    public static ICommand[] getCommands()
    {
        return commands;
    }

    static
    {
//        registerCommand(AUTO_CRAFTING = new Command(12, CommandType.CRAFTING, Localization.AUTO_CRAFTER_SHORT, Localization.AUTO_CRAFTER_LONG,
//                new ConnectionSet[]{ConnectionSet.STANDARD},
//                MenuCrafting.class, MenuCraftingPriority.class, MenuContainerScrap.class));
        registerCommand(new CommandTrigger());
        registerCommand(INPUT = new CommandItemInput());
        registerCommand(new CommandItemOutput());
        registerCommand(CONDITION = new CommandItemCondition());
        registerCommand(new CommandSplit());
        registerCommand(LIQUID_INPUT = new CommandFluidInput());
        registerCommand(new CommandFluidOutput());
        registerCommand(LIQUID_CONDITION = new CommandFluidCondition());
        registerCommand(new CommandRedstoneOutput());
        registerCommand(new CommandRedstoneCondition());
        registerCommand(VARIABLE = new CommandVariable());
        registerCommand(new CommandLoop());

        registerCommand(new CommandGroup());
        registerCommand(NODE = new CommandGroupNode());
        registerCommand(new CommandCamouflage());
        registerCommand(new CommandSign());
    }
}
