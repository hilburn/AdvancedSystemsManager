package advancedsystemsmanager.util;

import advancedsystemsmanager.reference.Textures;
import net.minecraft.client.Minecraft;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

public enum MouseCursor
{
    NULL,
    COLOUR_PICKER,
    UP_DOWN,
    LEFT_RIGHT,
    UP_LEFT,
    UP_RIGHT,
    MOVE,
    TEXT,
    HELP(7, 5);

    Cursor cursor;
    private static final int size = 32;

    MouseCursor()
    {
        this(size/2, size/2);
    }

    MouseCursor(int x, int y)
    {
        try
        {
            BufferedImage image = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(Textures.CURSORS).getInputStream());
            IntBuffer buffer = getByteBuffer(image, (ordinal() % 4) * size, (ordinal() / 4) * size, size, size);
            cursor = getCursor(buffer, x, y, size, size);
        } catch (Exception ignored)
        {
            return;
        }
    }

    private IntBuffer getByteBuffer(BufferedImage image, int x, int y, int height, int width)
    {
        int[] pixels = new int[height * width];
        image.getRGB(x, y, width, height, pixels, 0, width);
        return IntBuffer.wrap(pixels);
    }

    private Cursor getCursor(IntBuffer buf, int x, int y, int width, int height) throws IOException, LWJGLException {
        try {
            int yspot = height - y - 1;
            if (yspot < 0) {
                yspot = 0;
            }
            return new Cursor(width, height, x, yspot, 1, buf, null);
        } catch (Throwable e) {
            throw new LWJGLException(e);
        }
    }

    public void setCursor()
    {
        try
        {
            Mouse.setNativeCursor(cursor);
        } catch (LWJGLException ignored)
        {
        }
    }

    public static void resetMouseCursor()
    {
        try
        {
            Mouse.setNativeCursor(null);
        } catch (LWJGLException ignored)
        {
        }
    }
}
