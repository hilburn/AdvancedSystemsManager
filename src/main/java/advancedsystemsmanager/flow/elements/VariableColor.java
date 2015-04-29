package advancedsystemsmanager.flow.elements;

import advancedsystemsmanager.gui.Color;
import advancedsystemsmanager.reference.Names;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public enum VariableColor
{
    WHITE(Names.VARIABLE_WHITE, Color.WHITE, 1.0F, 1.0F, 1.0F),
    ORANGE(Names.VARIABLE_ORANGE, Color.ORANGE, 0.85F, 0.5F, 0.2F),
    MAGENTA(Names.VARIABLE_MAGENTA, Color.MAGENTA, 0.7F, 0.3F, 0.85F),
    LIGHT_BLUE(Names.VARIABLE_LIGHT_BLUE, Color.LIGHT_BLUE, 0.4F, 0.6F, 0.85F),
    YELLOW(Names.VARIABLE_YELLOW, Color.YELLOW, 0.9F, 0.9F, 0.2F),
    LIME(Names.VARiABLE_LIME, Color.LIME, 0.5F, 0.8F, 0.1F),
    PINK(Names.VARIABLE_PINK, Color.PINK, 0.95F, 0.5F, 0.65F),
    GRAY(Names.VARIABLE_GRAY, Color.GRAY, 0.3F, 0.3F, 0.3F),
    LIGHT_GRAY(Names.VARIABLE_LIGHT_GRAY, Color.LIGHT_GRAY, 0.6F, 0.6F, 0.6F),
    CYAN(Names.VARIABLE_CYAN, Color.CYAN, 0.3F, 0.5F, 0.6F),
    PURPLE(Names.VARIABLE_PURPLE, Color.PURPLE, 0.5F, 0.25F, 0.7F),
    BLUE(Names.VARIABLE_BLUE, Color.BLUE, 0.2F, 0.3F, 0.7F),
    BROWN(Names.VARIABLE_BROWN, Color.WHITE, 0.4F, 0.3F, 0.2F),
    GREEN(Names.VARIABLE_GREEN, Color.GREEN, 0.4F, 0.5F, 0.2F),
    RED(Names.VARIABLE_RED, Color.RED, 0.6F, 0.2F, 0.2F),
    BLACK(Names.VARIABLE_BLACK, Color.BLACK, 0.1F, 0.1F, 0.1F);

    public String name;
    public Color textColor;
    public float red;
    public float green;
    public float blue;

    VariableColor(String name, Color textColor, float red, float green, float blue)
    {
        this.name = name;
        this.textColor = textColor;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @SideOnly(Side.CLIENT)
    public void applyColor()
    {
        GL11.glColor4f(red, green, blue, 1F);
    }


    @Override
    public String toString()
    {
        return name;
    }

    public Color getTextColor()
    {
        return textColor;
    }
}
