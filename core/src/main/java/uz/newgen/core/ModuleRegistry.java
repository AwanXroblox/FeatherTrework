package uz.newgen.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/** Barcha HUD modullari va ularning sozlamalarini saqlash/yuklash. */
public final class ModuleRegistry {
    private final List<HudModule> modules = new ArrayList<>();
    private final Path file;
    private final ClickCounter clicks;

    public ModuleRegistry(Path file, ClickCounter clicks) {
        this.file = file;
        this.clicks = clicks;
        modules.add(new HudModule("fps", 6, 6, i -> i.fps() + " FPS"));
        modules.add(new HudModule("cps", 6, 28, i -> clicks.cps(System.currentTimeMillis()) + " CPS"));
        modules.add(new HudModule("ping", 6, 50, i -> i.ping() < 0 ? "Ping -" : i.ping() + " ms"));
        modules.add(new HudModule("coords", 6, 72,
                i -> String.format(Locale.ROOT, "XYZ %.0f %.0f %.0f", i.x(), i.y(), i.z())));
        modules.add(new HudModule("facing", 6, 94, i -> i.facing()));
        load();
    }

    public List<HudModule> all() {
        return Collections.unmodifiableList(modules);
    }

    public void save() {
        Properties p = new Properties();
        for (HudModule m : modules) {
            p.setProperty(m.id + ".enabled", Boolean.toString(m.enabled));
            p.setProperty(m.id + ".x", Integer.toString(m.x));
            p.setProperty(m.id + ".y", Integer.toString(m.y));
        }
        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            try (OutputStream out = Files.newOutputStream(file)) {
                p.store(out, "NewGen HUD");
            }
        } catch (IOException ignored) {
        }
    }

    private void load() {
        if (!Files.exists(file)) return;
        Properties p = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            p.load(in);
        } catch (IOException e) {
            return;
        }
        for (HudModule m : modules) {
            m.enabled = Boolean.parseBoolean(p.getProperty(m.id + ".enabled", "true"));
            m.x = parse(p.getProperty(m.id + ".x"), m.x);
            m.y = parse(p.getProperty(m.id + ".y"), m.y);
        }
    }

    private static int parse(String s, int fallback) {
        try {
            return s == null ? fallback : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
