package uz.newgen.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import uz.newgen.core.Theme;

/** Shisha (glass) uslubidagi panel: gradient, yorug' chiziq va yumaloq burchak effekti. */
public final class GlassRenderer {
    private GlassRenderer() {}

    public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h, float alpha) {
        int top = Theme.withAlpha(Theme.GLASS_TOP, alpha);
        int bottom = Theme.withAlpha(Theme.GLASS_BOTTOM, alpha);
        int border = Theme.withAlpha(Theme.GLASS_BORDER, alpha);
        int shine = Theme.withAlpha(Theme.GLASS_SHINE, alpha);

        g.fillGradient(x + 1, y, x + w - 1, y + h, top, bottom);
        g.fillGradient(x, y + 1, x + 1, y + h - 1, top, bottom);
        g.fillGradient(x + w - 1, y + 1, x + w, y + h - 1, top, bottom);

        g.fill(x + 1, y, x + w - 1, y + 1, border);
        g.fill(x + 1, y + h - 1, x + w - 1, y + h, border);
        g.fill(x, y + 1, x + 1, y + h - 1, border);
        g.fill(x + w - 1, y + 1, x + w, y + h - 1, border);

        g.fillGradient(x + 2, y + 1, x + w - 2, y + h / 2, shine, 0x00FFFFFF);
    }

    public static void label(GuiGraphicsExtractor g, String text, int x, int y, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        int w = mc.font.width(text) + 10;
        int h = mc.font.lineHeight + 8;
        panel(g, x, y, w, h, alpha);
        g.text(mc.font, text, x + 5, y + 4, Theme.withAlpha(Theme.TEXT, alpha), true);
    }

    public static void centered(GuiGraphicsExtractor g, String text, int cx, int y, int color) {
        Minecraft mc = Minecraft.getInstance();
        g.text(mc.font, text, cx - mc.font.width(text) / 2, y, color, true);
    }
}
