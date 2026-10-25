package uz.newgen.fabric;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uz.newgen.core.AltManager;
import uz.newgen.core.Theme;

/** Offline Alt Manager oynasi: nick qo'shish, tanlash, o'chirish va almashish. */
public final class AltScreen extends Screen {
    private final Screen parent;
    private final AltManager alts = NewGenClient.alts();
    private EditBox input;
    private String selected;
    private String status = "";

    public AltScreen(Screen parent) {
        super(Component.literal("Alt Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        input = new EditBox(font, cx - 100, 40, 130, 20, Component.literal("Nick"));
        input.setMaxLength(16);
        addRenderableWidget(input);
        addRenderableWidget(Button.builder(Component.literal("Qo'shish"), b -> {
            String n = input.getValue().trim();
            status = alts.add(n) ? "Qo'shildi: " + n : "Nick noto'g'ri yoki mavjud (3-16, harf/raqam/_)";
            input.setValue("");
        }).bounds(cx + 36, 40, 64, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Almashish"), b -> {
            if (selected == null) {
                status = "Avval ro'yxatdan nick tanlang";
                return;
            }
            NewGenClient.switchTo(selected);
            status = "Hozirgi nick: " + selected;
        }).bounds(cx - 100, height - 30, 64, 20).build());

        addRenderableWidget(Button.builder(Component.literal("O'chirish"), b -> {
            if (selected != null && alts.remove(selected)) {
                status = "O'chirildi: " + selected;
                selected = null;
            }
        }).bounds(cx - 32, height - 30, 64, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Yopish"), b -> onClose())
                .bounds(cx + 36, height - 30, 64, 20).build());
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        List<String> list = alts.list();
        int top = 72;
        int cx = width / 2;
        for (int i = 0; i < list.size(); i++) {
            int y = top + i * 18;
            if (mx >= cx - 100 && mx <= cx + 100 && my >= y && my <= y + 16) {
                selected = list.get(i);
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float delta) {
        renderTransparentBackground(g);
        int cx = width / 2;
        GlassRenderer.panel(g, cx - 110, 12, 220, height - 24, 1f);
        g.drawCenteredString(font, "Alt Manager (offline)", cx, 22, Theme.TEXT);

        List<String> list = alts.list();
        for (int i = 0; i < list.size(); i++) {
            String n = list.get(i);
            int y = 72 + i * 18;
            boolean sel = n.equals(selected);
            GlassRenderer.panel(g, cx - 100, y, 200, 16, sel ? 1f : 0.45f);
            g.drawString(font, n, cx - 94, y + 4, Theme.TEXT, true);
        }
        g.drawCenteredString(font, status, cx, height - 46, Theme.TEXT);
        super.render(g, mx, my, delta);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }
}
