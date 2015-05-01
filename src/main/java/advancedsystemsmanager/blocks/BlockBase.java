package advancedsystemsmanager.blocks;

import advancedsystemsmanager.AdvancedSystemsManager;
import advancedsystemsmanager.reference.Names;
import advancedsystemsmanager.reference.Reference;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockBase extends Block
{
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;

    protected int extraIcons;

    public BlockBase(String name)
    {
        this(Material.iron, soundTypeMetal, name, 1.2F, 0);
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
        return super.setBlockTextureName(Reference.RESOURCE_LOCATION + ":" + name.replace(Names.PREFIX, ""));
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
}
