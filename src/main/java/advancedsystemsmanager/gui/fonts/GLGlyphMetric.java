package advancedsystemsmanager.gui.fonts;

/**
 * Metrics about a character
 *
 * @author AfterLifeLochie
 */
public class GLGlyphMetric
{

    /**
     * The character's width
     */
    public int width;
    /**
     * The character's height
     */
    public int height;

    /**
     * The character's ascent
     */
    public int ascent;

    /**
     * The u-coordinate of the texture
     */
    public int ux;
    /**
     * The v-coordinate of the texture
     */
    public int vy;

    /**
     * Creates a new GlpyhMetric.
     *
     * @param w The character's glyph width. Used to compute the size of the
     *          glyph on the screen and the size of the texture region.
     * @param h The character's height. Used to compute the size of the glyph
     *          on the screen and the size of the texture region.
     * @param a The character's ascent (ie, the distance from the baseline of
     *          the character to the top of the character). Used to calculate
     *          the vertical offset of the rendered character.
     * @param u The u origin-coordinate of the texture
     * @param v The v origin-coordinate of the texture
     */
    public GLGlyphMetric(int w, int h, int a, int u, int v)
    {
        width = w;
        height = h;
        ascent = a;
        ux = u;
        vy = v;
    }

    @Override
    public String toString()
    {
        return "GLGlyphMetric { width: " + width + ", height: " + height + ", ascent: " + ascent + ", ux: " + ux
                + ", vy: " + vy + " }";
    }
}