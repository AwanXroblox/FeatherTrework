package uz.newgen.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/** Offline akkauntlar ro'yxati. Har qatorda bitta nick saqlanadi. */
public final class AltManager {
    private static final Pattern VALID = Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    private final Path file;
    private final List<String> names = new ArrayList<>();

    public AltManager(Path file) {
        this.file = file;
        load();
    }

    public static boolean isValid(String name) {
        return name != null && VALID.matcher(name).matches();
    }

    public List<String> list() {
        return Collections.unmodifiableList(names);
    }

    /** @return true, agar qo'shildi (nick to'g'ri va takrorlanmagan bo'lsa). */
    public boolean add(String name) {
        if (!isValid(name) || names.contains(name)) return false;
        names.add(name);
        save();
        return true;
    }

    public boolean remove(String name) {
        boolean removed = names.remove(name);
        if (removed) save();
        return removed;
    }

    private void load() {
        names.clear();
        try {
            if (Files.exists(file)) {
                for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
                    String n = line.trim();
                    if (isValid(n) && !names.contains(n)) names.add(n);
                }
            }
        } catch (IOException ignored) {
            // buzilgan fayl bo'lsa, bo'sh ro'yxat bilan davom etamiz
        }
    }

    private void save() {
        try {
            if (file.getParent() != null) Files.createDirectories(file.getParent());
            Files.write(file, names, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // yozib bo'lmasa ham o'yin to'xtamasligi kerak
        }
    }
}
