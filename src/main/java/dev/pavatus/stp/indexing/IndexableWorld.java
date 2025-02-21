package dev.pavatus.stp.indexing;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public interface IndexableWorld {

    @ApiStatus.Internal
    int stp$getIndex();
    void stp$setIndex(int index);

    interface Holder {

        List<IndexableWorld> stp$worlds();
        void stp$refresh();

        void stp$add(IndexableWorld world);
        void stp$remove(IndexableWorld world);
        IndexableWorld stp$get(int index);

        int stp$getWorldIndex(IndexableWorld world);
    }
}
