package uz.newgen.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public final class CoreSmokeTest {
    private static void check(boolean ok, String msg) {
        if (!ok) throw new AssertionError(msg);
        System.out.println("OK  " + msg);
    }

    public static void main(String[] args) throws Exception {
        UUID u = OfflineUuid.of("Steve");
        check(u.version() == 3, "offline UUID v3");
        check(u.equals(OfflineUuid.of("Steve")), "offline UUID deterministik");
        check(!u.equals(OfflineUuid.of("Alex")), "boshqa nick = boshqa UUID");

        Path dir = Files.createTempDirectory("newgen-test");
        AltManager alts = new AltManager(dir.resolve("alts.txt"));
        check(alts.add("Steve_01"), "to'g'ri nick qo'shiladi");
        check(!alts.add("Steve_01"), "takror qo'shilmaydi");
        check(!alts.add("ab"), "juda qisqa nick rad etiladi");
        check(!alts.add("bad nick!"), "noto'g'ri belgilar rad etiladi");
        check(new AltManager(dir.resolve("alts.txt")).list().contains("Steve_01"), "fayldan qayta o'qiladi");
        check(alts.remove("Steve_01") && alts.list().isEmpty(), "o'chirish ishlaydi");

        ClickCounter cc = new ClickCounter();
        cc.update(true, 0);
        cc.update(false, 50);
        cc.update(true, 100);
        check(cc.cps(200) == 2, "CPS 2 bosishni sanaydi");
        check(cc.cps(1500) == 0, "1 soniyadan keyin CPS 0");

        ModuleRegistry reg = new ModuleRegistry(dir.resolve("hud.properties"), cc);
        reg.all().get(0).x = 42;
        reg.all().get(1).enabled = false;
        reg.save();
        ModuleRegistry reg2 = new ModuleRegistry(dir.resolve("hud.properties"), cc);
        check(reg2.all().get(0).x == 42 && !reg2.all().get(1).enabled, "HUD sozlamalari saqlanadi");

        check(Theme.ease(0f) == 0f && Theme.ease(1f) == 1f && Theme.ease(0.5f) == 0.5f, "easing chegaralari");
        System.out.println("Hammasi o'tdi");
    }
}
