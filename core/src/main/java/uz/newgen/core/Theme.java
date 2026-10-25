package uz.newgen.core;

/** Ko'k "liquid glass" ranglari (ARGB) va silliq o'tish matematikasi. */
public final class Theme {
    private Theme() {}

    public static final int GLASS_TOP = 0x884FA3FF;
    public static final int GLASS_BOTTOM = 0x66176BFF;
    public static final int GLASS_BORDER = 0xCC9CD0FF;
    public static final int GLASS_SHINE = 0x55FFFFFF;
    public static final int TEXT = 0xFFFFFFFF;

    /** Easing: boshida va oxirida sekin (smoothstep). */
    public static float ease(float t) {
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static int withAlpha(int argb, float alpha) {
        int a = Math.round(((argb >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, alpha)));
        return (a << 24) | (argb & 0x00FFFFFF);
    }
}
