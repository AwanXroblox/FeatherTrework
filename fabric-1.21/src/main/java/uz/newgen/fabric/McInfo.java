package uz.newgen.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import uz.newgen.core.GameInfo;

/** GameInfo'ning Minecraft 1.21.4 uchun amalga oshirilishi. */
public final class McInfo implements GameInfo {
    private final Minecraft mc = Minecraft.getInstance();

    @Override
    public int fps() {
        return mc.getFps();
    }

    @Override
    public int ping() {
        if (mc.player == null || mc.getConnection() == null) return -1;
        PlayerInfo info = mc.getConnection().getPlayerInfo(mc.player.getUUID());
        return info == null ? -1 : info.getLatency();
    }

    @Override
    public double x() {
        return mc.player == null ? 0 : mc.player.getX();
    }

    @Override
    public double y() {
        return mc.player == null ? 0 : mc.player.getY();
    }

    @Override
    public double z() {
        return mc.player == null ? 0 : mc.player.getZ();
    }

    @Override
    public String facing() {
        return mc.player == null ? "-" : mc.player.getDirection().getName().toUpperCase();
    }
}
