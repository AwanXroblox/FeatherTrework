package uz.newgen.core;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Minecraft offline-rejim UUID'si: MD5 (v3) asosida "OfflinePlayer:<nick>" dan. */
public final class OfflineUuid {
    private OfflineUuid() {}

    public static UUID of(String name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }
}
