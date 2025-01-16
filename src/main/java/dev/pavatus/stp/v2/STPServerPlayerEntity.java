package dev.pavatus.stp.v2;

import net.minecraft.util.math.Vec3d;

public interface STPServerPlayerEntity {
    void stp$markPortaled();
    void stp$unsetPortaled();
    boolean stp$isPortaled();

    GhostServerPlayerEntity stp$createGhost();
}
