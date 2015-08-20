package advancedsystemsmanager.commands;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.api.network.IPacketSync;
import advancedsystemsmanager.helpers.LocalizationHelper;
import advancedsystemsmanager.network.ASMPacket;
import advancedsystemsmanager.network.PacketHandler;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;

public class CommandTheme implements ISubCommand, IPacketSync
{
    private static List<String> commands = Arrays.asList("list", "load", "save");
    int id;

    @Override
    public int getPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "theme";
    }

    @Override
    public void handleCommand(ICommandSender sender, String[] arguments)
    {
        if (sender instanceof EntityPlayerMP)
        {
            ASMPacket packet = PacketHandler.getCommandPacket();
            packet.writeByte(id);
            packet.writeByte(commands.indexOf(arguments[1]));
            packet.writeByte(Math.max(arguments.length - 2, 0));
            for (int i = 2; i < arguments.length; i++)
            {
                packet.writeStringToBuffer(arguments[i]);
            }
            packet.sendPlayerPacket((EntityPlayerMP)sender);
        } else
        {
            throw new CommandException(Names.COMMAND_PLAYER_ONLY);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return commands;
    }

    @Override
    public boolean isVisible(ICommandSender sender)
    {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean readData(ASMPacket packet)
    {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        int command = packet.readByte();
        String[] args = new String[packet.readByte()];
        for (int i = 0; i < args.length; i++)
        {
            args[i] = packet.readStringFromBuffer();
        }
        switch (command)
        {
            case 0:
                List<String> themes = AdvancedSystemsManager.themeHandler.getThemes();
                String list = "";
                for (int i = 0; i < themes.size(); i++)
                {
                    list += themes.get(i);
                    if (i < themes.size() - 1) list += ", ";
                }
                LocalizationHelper.addChatMessageFormatted(player, Names.COMMAND_THEME_LIST + Names.COMMAND_OUTPUT, list);
                break;
            case 1:
                if (args.length > 1) throw new CommandException(Names.COMMAND_THEME_LOAD + Names.COMMAND_SYNTAX);
                if (!AdvancedSystemsManager.themeHandler.setTheme(args.length == 1 ? args[0] : "default"))
                {
                    throw new CommandException(Names.COMMAND_THEME_LOAD + Names.COMMAND_FAILED, args[0]);
                }
                LocalizationHelper.addChatMessage(player, Names.COMMAND_THEME_LOAD + Names.COMMAND_OUTPUT);
                break;
            case 2:
                if (args.length != 1) throw new CommandException(Names.COMMAND_THEME_SAVE + Names.COMMAND_SYNTAX);
                AdvancedSystemsManager.themeHandler.saveTheme(args[0]);
                LocalizationHelper.addChatMessage(player, Names.COMMAND_THEME_SAVE + Names.COMMAND_OUTPUT);
                break;
        }
        return false;
    }
}
