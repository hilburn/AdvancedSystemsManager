package advancedsystemsmanager.gui.fonts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

/**
 * Represents a Font object for OpenGL.
 *
 * Shamelessly adapted from Fontbox by AfterLifeLochie: https://github.com/AfterLifeLochie/fontbox
 */
public class GLFont
{

    private static final char MIN_CH = '\u0000';
    private static final char MAX_CH = '\u00ff';

    private static final int RASTER_DIM = 512;
    private String name;
    private float scale;
    private int textureId;
    private GLFontMetrics metric;

    private GLFont(String name, int textureId, float scale, GLFontMetrics metric)
    {
        this.name = name;
        this.textureId = textureId;
        this.scale = scale;
        this.metric = metric;
    }

    /**
     * Get the name of the font.
     *
     * @return The name of the font
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the OpenGL texture ID for this font.
     *
     * @return The 2D texture ID for the font
     */
    public int getTextureId()
    {
        return textureId;
    }

    /**
     * Get the OpenGL font scale for this font.
     *
     * @return The 2D font scaling ratio
     */
    public float getScale()
    {
        return scale;
    }

    public float getTextureSize()
    {
        return RASTER_DIM;
    }

    /**
     * Get the font metric map associated with this font.
     *
     * @return The metric map
     */
    public GLFontMetrics getMetric()
    {
        return metric;
    }

    /**
     * Delete the font. This releases all the resources associated with the font
     * immediately.
     */
    public void delete()
    {
//		Fontbox.deleteFont(this);
        GL11.glDeleteTextures(textureId);
        textureId = -1;
        name = null;
        metric = null;
    }

    @Override
    public String toString()
    {
        return "GLFont { hash: " + System.identityHashCode(this) + ", texture: " + textureId + ", metric: "
                + System.identityHashCode(metric) + " }";
    }

    /**
     * Create a GLFont from a TTF file
     *
     * @param px  The font pixel size
     * @param ttf The TTF file
     * @return The GLFont result
     */
    public static GLFont fromTTF(float px, ResourceLocation ttf)
    {
        if (ttf == null)
            throw new IllegalArgumentException("ttf may not be null");
        try
        {
            IResource metricResource = Minecraft.getMinecraft().getResourceManager().getResource(ttf);
            InputStream stream = metricResource.getInputStream();
            if (stream == null)
                throw new IOException("Could not open TTF file.");
            Font sysfont = Font.createFont(Font.TRUETYPE_FONT, stream);
            return fromFont(sysfont.deriveFont(px));
        } catch (FontFormatException e)
        {
        } catch (IOException e)
        {
        }
        return null;
    }

    /**
     * Create a GLFont from a Java Font object
     *
     * @param font The font object
     * @return The GLFont result
     */
    public static GLFont fromFont(Font font)
    {
        if (font == null)
            throw new IllegalArgumentException("font may not be null");
        BufferedImage buffer = new BufferedImage(RASTER_DIM, RASTER_DIM, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D)buffer.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int off = 0;
        int charsPerRow = 12;
        for (char k = MIN_CH; k <= MAX_CH; k++, off++)
        {
            TextLayout layout = new TextLayout(String.valueOf(k), font, graphics.getFontRenderContext());
            Rectangle2D rect = layout.getBounds();
            int x = (off % charsPerRow) * (RASTER_DIM / charsPerRow);
            int y = (off / charsPerRow) * (RASTER_DIM / charsPerRow);
            float cy = (float)rect.getHeight();
            graphics.setColor(Color.WHITE);
            layout.draw(graphics, x, y - cy);
        }

        GLFontMetrics metric = GLFontMetrics.fromFontMetrics(font, graphics.getFontRenderContext(), RASTER_DIM,
                RASTER_DIM, charsPerRow, MIN_CH, MAX_CH);
        GLFont f0 = fromBuffer(font.getFontName(), buffer, RASTER_DIM, RASTER_DIM, metric);
        return f0;
    }

    /**
     * Create a GLFont from an image buffer of a specified size with a specified
     * metric map.
     *
     * @param name   The name of the font
     * @param image  The buffered image
     * @param width  The width of the image, absolute pixels
     * @param height The height of the image, absolute pixels
     * @param metric The font metric map
     * @return The GLFont result
     */
    public static GLFont fromBuffer(String name, BufferedImage image, int width, int height,
                                    GLFontMetrics metric)
    {
        if (name == null)
            throw new IllegalArgumentException("name may not be null");
        if (image == null)
            throw new IllegalArgumentException("image may not be null");
        if (metric == null)
            throw new IllegalArgumentException("metric may not be null");
        ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{
                8, 8, 8, 8}, true, false, Transparency.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, width, height, 4, null);
        BufferedImage texImage = new BufferedImage(glAlphaColorModel, raster, true, new Hashtable<Object, Object>());
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f, 0f, 0f, 0f));
        g.fillRect(0, 0, width, height);
        g.drawImage(image, 0, 0, null);

        byte[] data = ((DataBufferByte)texImage.getRaster().getDataBuffer()).getData();

        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.order(ByteOrder.nativeOrder());
        buffer.put(data, 0, data.length);
        buffer.flip();

        IntBuffer tmp = BufferUtils.createIntBuffer(1);
        GL11.glGenTextures(tmp);
        tmp.rewind();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tmp.get(0));
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
                buffer);
        tmp.rewind();
        int texIdx = tmp.get(0);
        GLFont font = new GLFont(name, texIdx, 0.44f, metric);
//		Fontbox.allocateFont(font);
        return font;
    }

    /**
     * Create a GLFont from a spritefont and XML descriptor
     *
     * @param name  The name of the font, case sensitive
     * @param image The image file
     * @param xml   The XML descriptor file
     * @return The GLFont result
     */
    public static GLFont fromSpritefont(String name, ResourceLocation image, ResourceLocation xml)
    {
        if (name == null)
            throw new IllegalArgumentException("name may not be null");
        if (image == null)
            throw new IllegalArgumentException("image may not be null");
        if (xml == null)
            throw new IllegalArgumentException("xml may not be null");
        try
        {
            IResource imageResource = Minecraft.getMinecraft().getResourceManager().getResource(image);
            InputStream stream = imageResource.getInputStream();
            if (stream == null)
                throw new IOException("Could not open image file.");
            BufferedImage buffer = ImageIO.read(stream);

            GLFontMetrics metric = GLFontMetrics.fromResource(xml, buffer.getWidth(), buffer.getHeight());
            GLFont f0 = fromBuffer(name, buffer, buffer.getWidth(), buffer.getHeight(), metric);
            return f0;

        } catch (IOException ioex)
        {
        }
        return null;
    }
}