package uz.newgen.fabric;

import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import uz.newgen.core.AltManager;
import uz.newgen.core.Theme;

/**
 * Offline Alt Manager oynasi. Ro'yxatdagi har bir nick alohida tugma,
 * shuning uchun sichqoncha hodisalari API'siga bog'liq emas.
 */
public final class AltScreen extends Screen {
    private final Screen parent;
    private final AltManager alts = NewGenClient.alts();
    private EditBox input;
    private String selected;
    private String status = "";
    private String pendingText = "";

    public AltScreen(Screen parent) {
        super(Component.literal("Alt Manager"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        input = new EditBox(font, cx - 100, 40, 130, 20, Component.literal("Nick"));
        input.setMaxLength(16);
        input.setValue(pendingText);
        addRenderableWidget(input);

        addRenderableWidget(Button.builder(Component.literal("Qo'shish"), b -> {
            String n = input.getValue().trim();
            status = alts.add(n) ? "Qo'shildi: " + n : "Nick noto'g'ri yoki mavjud (3-16, harf/raqam/_)";
            pendingText = "";
            rebuildWidgets();
        }).bounds(cx + 36, 40, 64, 20).build());

        List<String> list = alts.list();
        for (int i = 0; i < list.size(); i++) {
            String n = list.get(i);
            String label = n.equals(selected) ? "> " + n : n;
            addRenderableWidget(Button.builder(Component.literal(label), b -> {
                pendingText = input.getValue();
                selected = n;
                rebuildWidgets();
            }).bounds(cx - 100, 72 + i * 22, 200, 20).build());
        }

        addRenderableWidget(Button.builder(Component.literal("Almashish"), b -> {
            if (selected == null) {
                status = "Avval ro'yxatdan nick tanlang";
                return;
            }
            status = NewGenClient.switchTo(selected)
                    ? "Hozirgi nick: " + selected
                    : "Almashtirib bo'lmadi";
        }).bounds(cx - 100, height - 30, 64, 20).build());

        addRenderableWidget(Button.builder(Component.literal("O'chirish"), b -> {
            if (selected != null && alts.remove(selected)) {
                status = "O'chirildi: " + selected;
                selected = null;
                pendingText = input.getValue();
                rebuildWidgets();
            }
        }).bounds(cx - 32, height - 30, 64, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Yopish"), b -> onClose())
                .bounds(cx + 36, height - 30, 64, 20).build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int cx = width / 2;
        GlassRenderer.panel(g, cx - 110, 12, 220, height - 24, 1f);
        GlassRenderer.centered(g, "Alt Manager (offline)", cx, 22, Theme.TEXT);
        GlassRenderer.centered(g, status, cx, height - 46, Theme.TEXT);
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        minecraft.gui.setScreen(parent);
    }
}
