package uz.newgen.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
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
    public static boolean switchTo(String name) {
        try {
            User user = newUser(name);
            ((MinecraftAccessor) Minecraft.getInstance()).newgen$setUser(user);
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
    }

    /**
     * User konstruktori versiyalar orasida o'zgaradi, shuning uchun mos konstruktorni
     * parametr turlariga qarab tanlaymiz (String, UUID, Optional, enum).
     */
    private static User newUser(String name) throws ReflectiveOperationException {
        UUID uuid = OfflineUuid.of(name);
        for (Constructor<?> c : User.class.getDeclaredConstructors()) {
            Class<?>[] types = c.getParameterTypes();
            boolean hasUuid = Arrays.asList(types).contains(UUID.class);
            Object[] args = new Object[types.length];
            boolean ok = true;
            int strings = 0;
            for (int i = 0; i < types.length && ok; i++) {
                Class<?> t = types[i];
                if (t == UUID.class) {
                    args[i] = uuid;
                } else if (t == String.class) {
                    strings++;
                    if (strings == 1) args[i] = name;
                    else if (!hasUuid && strings == 2) args[i] = uuid.toString();
                    else args[i] = "0";
                } else if (t == Optional.class) {
                    args[i] = Optional.empty();
                } else if (t.isEnum()) {
                    args[i] = pickEnum(t);
                } else if (t.isPrimitive()) {
                    ok = false;
                } else {
                    args[i] = null;
                }
            }
            if (!ok) continue;
            c.setAccessible(true);
            return (User) c.newInstance(args);
        }
        throw new IllegalStateException("User konstruktori topilmadi");
    }

    private static Object pickEnum(Class<?> type) {
        Object[] constants = type.getEnumConstants();
        for (Object o : constants) {
            if (((Enum<?>) o).name().equals("LEGACY")) return o;
        }
        return constants.length > 0 ? constants[0] : null;
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
                "key.newgen.altmenu", InputConstants.KEY_RSHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            // CPS: "hujum" tugmasi (standart: chap sichqoncha tugmasi)
            CLICKS.update(mc.options.keyAttack.isDown(), System.currentTimeMillis());
            while (menuKey.consumeClick()) {
                mc.gui.setScreen(new AltScreen(null));
            }
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath(MOD_ID, "hud"), NewGenClient::renderHud);
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        fade = Math.min(1f, fade + 0.04f);
        float alpha = Theme.ease(fade);
        for (HudModule m : modules.all()) {
            if (m.enabled) GlassRenderer.label(graphics, m.render(info), m.x, m.y, alpha);
        }
    }
}
