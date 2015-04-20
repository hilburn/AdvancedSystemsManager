package advancedfactorymanager.helpers;

import advancedfactorymanager.commands.CommandDuplicator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Threaded
{

    private static ClipboardOwner clippy = new ClipboardOwner()
    {
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {

        }
    };

    private Threaded()
    {

    }

    public static class Set implements Runnable
    {
        private EntityPlayerMP sender;
        private ItemStack duplicator;
        private String name;

        public Set(ItemStack duplicator, EntityPlayerMP sender, String[] arguments) throws UnsupportedEncodingException
        {
            name = arguments[2];
            this.sender = sender;
            this.duplicator = duplicator;
        }

        @Override
        public void run()
        {
            name = name.replaceAll("http://pastebin.com/(.*)", "$1");
            name = name.replaceAll("pastebin.com/(.*)", "$1");
            HttpPost httpPost;
            try
            {
                httpPost = new HttpPost("http://pastebin.com/raw.php?i=" + URLEncoder.encode(name, "UTF-8"));
            } catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
                return;
            }
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<String> post = service.submit(httpPost);
            NBTBase nbtBase;
            try
            {
                nbtBase = JsonToNBT.func_150315_a(post.get());
            } catch (NBTException e)
            {
                e.printStackTrace();
                return;
            } catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            } catch (ExecutionException e)
            {
                e.printStackTrace();
                return;
            }
            if (nbtBase instanceof NBTTagCompound)
            {
                NBTTagCompound tagCompound = (NBTTagCompound)nbtBase;
                tagCompound = CommandDuplicator.unstripBaseNBT(tagCompound);
                duplicator.setTagCompound(tagCompound);
                sender.addChatComponentMessage(new ChatComponentText(LocalizationHelper.translateFormatted("stevesaddons.command.loadSuccess", "http://pastebin.com/" + name)));
            }
        }
    }


    public static class Put implements Runnable
    {
        private EntityPlayerMP sender;
        private ItemStack duplicator;
        private String[] arguments;

        public Put(ItemStack duplicator, EntityPlayerMP sender, String[] arguments) throws UnsupportedEncodingException
        {
            this.duplicator = duplicator;
            this.sender = sender;
            this.arguments = arguments;
        }

        @Override
        public void run()
        {
            HttpPost httpPost = new HttpPost("http://pastebin.com/api/api_post.php");
            httpPost.put("api_option", "paste");
            httpPost.put("api_paste_private", "1");
            String apiKey = "367773fafa3565615286cf270e73f3de";
            httpPost.put("api_dev_key", apiKey);
            if (arguments.length > 2) httpPost.put("api_paste_name", arguments[2]);
            NBTTagCompound tagCompound = (NBTTagCompound)duplicator.getTagCompound().copy();
            tagCompound.removeTag("x");
            tagCompound.removeTag("y");
            tagCompound.removeTag("z");
            CommandDuplicator.stripBaseNBT(tagCompound);
            tagCompound.setString("Author", sender.getCommandSenderName());
            httpPost.put("api_paste_code", tagCompound.toString());
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<String> line = service.submit(httpPost);
            String inputLine;
            try
            {
                inputLine = line.get();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
                return;
            } catch (ExecutionException e)
            {
                e.printStackTrace();
                return;
            }
            sender.addChatComponentMessage(new ChatComponentText(LocalizationHelper.translateFormatted("stevesaddons.command.savedTo", inputLine)));
            if (!sender.mcServer.isDedicatedServer())
            {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(inputLine), clippy);
                sender.addChatComponentMessage(new ChatComponentText(LocalizationHelper.translate("stevesaddons.command.copiedToClip")));
            }
        }
    }
}
