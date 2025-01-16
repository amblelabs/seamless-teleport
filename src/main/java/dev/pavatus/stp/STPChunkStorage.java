package dev.pavatus.stp;

import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Set;

public interface STPChunkStorage {
    ReferenceSet<ServerPlayerEntity> stp$watching();
}
