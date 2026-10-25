package uz.newgen.core;

import java.util.function.Function;

/** Bitta HUD paneli: nomi, holati, joylashuvi va matn manbai. */
public final class HudModule {
    public final String id;
    public boolean enabled = true;
    public int x;
    public int y;
    private final Function<GameInfo, String> text;

    public HudModule(String id, int x, int y, Function<GameInfo, String> text) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.text = text;
    }

    public String render(GameInfo info) {
        return text.apply(info);
    }
}
