package dev.pavatus.stp.indexing;

public interface SServerWorld {
    void stp$setIndex(int index);
    int stp$index();

    default boolean stp$hasIndex() {
        return this.stp$index() != -1;
    }
}
