package uz.newgen.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import java.util.Optional;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import uz.newgen.core.AltManager;
import uz.newgen.core.ClickCounter;
import uz.newgen.core.HudModule;
import uz.newgen.core.ModuleRegistry;
import uz.newgen.core.OfflineUuid;
import uz.newgen.core.Theme;
import uz.newgen.fabric.mixin.MinecraftAccessor;

public final class NewGenClient implements ClientModInitializer {
    public static final String MOD_ID = "newgen";

    private static AltManager alts;
    private static ModuleRegistry modules;
    private static McInfo info;
    private static final ClickCounter CLICKS = new ClickCounter();
    private static float fade;

    public static AltManager alts() {
        return alts;
    }

    /** Offline nickga o'yin ichida almashish (qayta ishga tushirmasdan). */
    public static void switchTo(String name) {
        Minecraft mc = Minecraft.getInstance();
        User user = new User(name, OfflineUuid.of(name), "0",
                Optional.empty(), Optional.empty(), User.Type.LEGACY);
        ((MinecraftAccessor) mc).newgen$setUser(user);
    }

    @Override
    public void onInitializeClient() {
        Path dir = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        alts = new AltManager(dir.resolve("alts.txt"));
        modules = new ModuleRegistry(dir.resolve("hud.properties"), CLICKS);
        info = new McInfo();

        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main"));
        KeyMapping menuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.newgen.altmenu", InputConstants.Type.KEYSYM, InputConstants.KEY_RSHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            boolean down = mc.getWindow() != null
                    && GLFW.glfwGetMouseButton(mc.getWindow().handle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            CLICKS.update(down, System.currentTimeMillis());
            while (menuKey.consumeClick()) {
                mc.gui.setScreen(new AltScreen(null));
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"), NewGenClient::renderHud);
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;
        fade = Math.min(1f, fade + 0.04f);
        float alpha = Theme.ease(fade);
        for (HudModule m : modules.all()) {
            if (m.enabled) GlassRenderer.label(graphics, m.render(info), m.x, m.y, alpha);
        }
    }
}
