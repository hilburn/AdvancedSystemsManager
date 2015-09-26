package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Mods;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;

@Optional.Interface(iface = "cofh.api.block.IDismantleable", modid = Mods.COFHCORE)
public class BlockBase extends Block implements IDismantleable
{
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;

    protected int extraIcons;

    public BlockBase(String name)
    {
        this(Material.iron, soundTypeMetal, name, 1.2F, 0);
    }

    public BlockBase(Material material, SoundType sound, String name, float hardness, int extraIcons)
    {
        super(material);
        setCreativeTab(AdvancedSystemsManager.creativeTab);
        setStepSound(sound);
        setBlockName(name);
        setBlockTextureName(name);
        setHardness(hardness);
        this.extraIcons = extraIcons;
    }

    @Override
    public Block setBlockTextureName(String name)
    {
        return super.setBlockTextureName(Reference.RESOURCE_LOCATION + ":" + name.replace(Names.OLD_PREFIX, ""));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);
        if (extraIcons > 0)
        {
            icons = new IIcon[extraIcons];
            for (int i = 0; i < icons.length; i++)
                icons[i] = register.registerIcon(getTextureName() + (i > 0 ? "_" + i : ""));
        }
    }

    public BlockBase(String name, float hardness)
    {
        this(Material.iron, soundTypeMetal, name, hardness, 0);
    }

    public BlockBase(String name, int extraIcons)
    {
        this(Material.iron, soundTypeMetal, name, 1.2F, extraIcons);
    }

    public BlockBase(String name, float hardness, int extraIcons)
    {
        this(Material.iron, soundTypeMetal, name, hardness, extraIcons);
    }

    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnBlock)
    {
        ArrayList<ItemStack> list = getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        world.setBlockToAir(x, y, z);
        if (!returnBlock)
            for (ItemStack item : list)
                dropBlockAsItem(world, x, y, z, item);
        return list;
    }

    public boolean canDismantle(EntityPlayer entityPlayer, World world, int x, int y, int z)
    {
        return entityPlayer.isSneaking();
    }
}
