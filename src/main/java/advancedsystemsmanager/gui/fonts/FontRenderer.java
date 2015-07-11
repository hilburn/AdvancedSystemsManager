package advancedsystemsmanager.gui.fonts;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class FontRenderer
{
    private CharData[] charArray = new CharData[256];
    private Map<Character, CharData> customChars = new HashMap<Character, CharData>();
    private boolean antiAlias;
    private int fontSize = 0;
    private int fontHeight = 0;
    private int textureID = Integer.MIN_VALUE;
    private int textureWidth = 512;
    private int textureHeight = 512;
    private Font font;
    private float zLevel;

    public FontRenderer(Font font, boolean antiAlias)
    {
        this(font, antiAlias, null);
    }

    public FontRenderer(Font font, boolean antiAlias, char[] additionalChars)
    {

        this.font = font;
        this.fontSize = font.getSize();
        this.antiAlias = antiAlias;
        loadFont(additionalChars);
    }

    private void loadFont(char[] customCharsArray)
    {
        if (customCharsArray != null && customCharsArray.length > 0)
        {
            textureWidth *= 2;
        }

        BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)imgTemp.getGraphics();

        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, textureWidth, textureHeight);

        int rowHeight = 0;
        int positionX = 0;
        int positionY = 0;

        if (customCharsArray == null) customCharsArray = new char[0];
        int customCharsLength = customCharsArray.length;

        for (int i = 0; i < 256 + customCharsLength; i++)
        {
            char ch = (i < 256) ? (char)i : customCharsArray[i - 256];

            BufferedImage fontImage = getFontImage(ch);

            CharData newCharData = new CharData();

            newCharData.width = fontImage.getWidth();
            newCharData.height = fontImage.getHeight();

            if (positionX + newCharData.width >= textureWidth)
            {
                positionX = 0;
                positionY += rowHeight + 1;
                rowHeight = 0;
            }

            newCharData.storedX = positionX;
            newCharData.storedY = positionY;

            if (newCharData.height > fontHeight)
            {
                fontHeight = newCharData.height;
            }

            if (newCharData.height > rowHeight)
            {
                rowHeight = newCharData.height;
            }
            g.drawImage(fontImage, positionX, positionY, null);

            positionX += newCharData.width + 1;

            if (i < 256)
            {
                charArray[i] = newCharData;
            } else
            {
                customChars.put(ch, newCharData);
            }

            fontImage = null;
        }
//        try
//        {
//
//            ImageIO.write(imgTemp, "PNG", new File("test.png"));
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
        textureID = loadTexture(imgTemp, textureID);
    }

    private BufferedImage getFontImage(char ch)
    {
        BufferedImage tempFontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D)tempFontImage.getGraphics();
        if (antiAlias)
        {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);

        FontMetrics fontMetrics = g.getFontMetrics();
        int charWidth = fontMetrics.charWidth(ch);

        if (charWidth <= 0)
        {
            charWidth = 1;
        }
        int charHeight = fontMetrics.getHeight();
        if (charHeight <= 0)
        {
            charHeight = fontSize;
        }

        BufferedImage fontImage = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gt = (Graphics2D)fontImage.getGraphics();
        if (antiAlias)
        {
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        gt.setFont(font);

        gt.setColor(Color.WHITE);
        int charX = 0;
        int charY = 0;
        gt.drawString(String.valueOf(ch), (charX), (charY) + fontMetrics.getAscent());
        return fontImage;
    }

    public int loadTexture(BufferedImage image, int textureID)
    {

        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++)
        {
            for (int x = 0; x < image.getWidth(); x++)
            {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte)((pixel >> 16) & 0xFF));
                buffer.put((byte)((pixel >> 8) & 0xFF));
                buffer.put((byte)(pixel & 0xFF));
                buffer.put((byte)((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();
        if (textureID == Integer.MIN_VALUE) textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        return textureID;
    }

    public int getWidth(String string, int height)
    {
        return (int)Math.ceil(getWidth(string) * (float)height / fontHeight);
    }

    public int getWidth(String string)
    {
        int width = 0;
        CharData charData;
        char currentChar;
        for (int i = 0; i < string.length(); i++)
        {
            currentChar = string.charAt(i);
            if (currentChar < 256)
            {
                charData = charArray[currentChar];
            } else
            {
                charData = customChars.get(currentChar);
            }

            if (charData != null)
                width += charData.width;
        }
        return width;
    }

    /**
     * Trims a string to a specified width, and will reverse it if par3 is set.
     */
    public String trimStringToWidth(String string, int newLength, boolean reverse)
    {
        StringBuilder stringbuilder = new StringBuilder();
        int width = 0;
        int k = reverse ? string.length() - 1 : 0;
        int l = reverse ? -1 : 1;
        boolean flag1 = false;
        boolean flag2 = false;

        for (int i1 = k; i1 >= 0 && i1 < string.length() && width < newLength; i1 += l)
        {
            char thisChar = string.charAt(i1);
            int thisWidth = getCharWidth(thisChar);

            if (flag1)
            {
                flag1 = false;

                if (thisChar != 108 && thisChar != 76)
                {
                    if (thisChar == 114 || thisChar == 82)
                    {
                        flag2 = false;
                    }
                } else
                {
                    flag2 = true;
                }
            } else if (thisWidth < 0)
            {
                flag1 = true;
            } else
            {
                width += thisWidth;

                if (flag2)
                {
                    ++width;
                }
            }

            if (width > newLength)
            {
                break;
            }

            if (reverse)
            {
                stringbuilder.insert(0, thisChar);
            } else
            {
                stringbuilder.append(thisChar);
            }
        }

        return stringbuilder.toString();
    }

    private int getCharWidth(char character)
    {
        CharData charData;
        if (character < 256)
        {
            charData = charArray[character];
        } else
        {
            charData = customChars.get(character);
        }
        return charData == null ? 0 : charData.width;
    }

    public int getHeight(String string)
    {
        return fontHeight;
    }

    public int getLineHeight()
    {
        return fontHeight;
    }

    public void drawString(float x, float y, String string)
    {
        drawString(x, y, string, 0xFFFFFFFF);
    }

    public void drawString(float x, float y, String string, int colour)
    {
        drawString(x, y, string, colour, 0, string.length() - 1);
    }

    public void drawString(float x, float y, String string, int colour, int startIndex, int endIndex)
    {
        float red = (float)(colour >> 16 & 255) / 255.0F;
        float blue = (float)(colour >> 8 & 255) / 255.0F;
        float green = (float)(colour & 255) / 255.0F;
        glColor3f(red, blue, green);
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        CharData charData;
        char charCurrent;

        glBegin(GL_QUADS);

        int width = 0;
        for (int i = 0; i < string.length(); i++)
        {
            charCurrent = string.charAt(i);
            if (charCurrent < 256)
            {
                charData = charArray[charCurrent];
            } else
            {
                charData = customChars.get(charCurrent);
            }

            if (charData != null)
            {
                if ((i >= startIndex) || (i <= endIndex))
                {
                    drawQuad((x + width), y, charData.storedX,
                            charData.storedY, charData.width,
                            charData.height);
                }
                width += charData.width;
            }
        }

        glEnd();
    }

    private void drawQuad(float drawX, float drawY, float srcX, float srcY, float w, float h)
    {
        float tSrcX = srcX / textureWidth;
        float tSrcY = srcY / textureHeight;
        float drawX2 = drawX + w;
        float drawY2 = drawY + h;
        float u = (w / textureWidth);
        float v = (h / textureHeight);

        glTexCoord2f(tSrcX, tSrcY);
        glVertex3f(drawX, drawY, zLevel);
        glTexCoord2f(tSrcX, tSrcY + v);
        glVertex3f(drawX, drawY2, zLevel);
        glTexCoord2f(tSrcX + u, tSrcY + v);
        glVertex3f(drawX2, drawY2, zLevel);
        glTexCoord2f(tSrcX + u, tSrcY);
        glVertex3f(drawX2, drawY, zLevel);
    }

    public void drawScaledString(float x, float y, String string, int colour, int height)
    {
        float scale = (float)height / getHeight();
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1);
        drawString(0, 0, string, colour);
        glPopMatrix();
    }

    public int getHeight()
    {
        return fontHeight;
    }

    /**
     * Inserts newline and formatting into a string to wrap it within the specified width.
     */
    private String wrapFormattedStringToWidth(String string, int maxWidth)
    {
        int j = this.sizeStringToWidth(string, maxWidth);

        if (string.length() <= j)
        {
            return string;
        } else
        {
            String s1 = string.substring(0, j);
            char c0 = string.charAt(j);
            boolean flag = c0 == 32 || c0 == 10;
            String s2 = string.substring(j + (flag ? 1 : 0));
            return s1 + "\n" + this.wrapFormattedStringToWidth(s2, maxWidth);
        }
    }

    /**
     * Determines how many characters from the string will fit into the specified width.
     */
    private int sizeStringToWidth(String string, int width)
    {
        int stringLength = string.length();
        int k = 0;
        int l = 0;
        int i1 = -1;

        for (boolean flag = false; l < stringLength; ++l)
        {
            char c0 = string.charAt(l);

            switch (c0)
            {
                case 10:
                    --l;
                    break;
                case 167:
                    if (l < stringLength - 1)
                    {
                        ++l;
                        char c1 = string.charAt(l);

                        if (c1 != 108 && c1 != 76)
                        {
                            if (c1 == 114 || c1 == 82)
                            {
                                flag = false;
                            }
                        } else
                        {
                            flag = true;
                        }
                    }

                    break;
                case 32:
                    i1 = l;
                default:
                    k += this.getCharWidth(c0);

                    if (flag)
                    {
                        ++k;
                    }
            }

            if (c0 == 10)
            {
                ++l;
                i1 = l;
                break;
            }

            if (k > width)
            {
                break;
            }
        }

        return l != stringLength && i1 != -1 && i1 < l ? i1 : l;
    }

    private static class CharData
    {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}
