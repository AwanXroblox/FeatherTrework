package uz.newgen.core;

/** Har bir Minecraft versiyasi qatlami shu interfeysni amalga oshiradi. */
public interface GameInfo {
    int fps();

    /** Ping millisekundda, noma'lum bo'lsa -1. */
    int ping();

    double x();

    double y();

    double z();

    String facing();
}
