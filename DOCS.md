# How shit should work?
## Server:
When player walks into the proximity of a portal,
a player's [ghost] gets created at the other side.

The [ghost] delegates all method calls to the original player,
except for the custom `ServerPlayNetworkHandler`.

The [ghost] is locked to the portal's destination position.

When player walks through the portal, the [ghost] 
at the same side as the player gets removed.


# Why shit doesn't work?
No fucking idea.


# How shit should work (v2.0):
ServerWorld -> ServerChunkManager -> ThreadedAnvilChunkStorage
1. Inject in ServerWorld#sendToPlayerIfNearby
2. Inject in PlayerManager#sendToAround
3. Inject in PlayerChunkWatchingManager#getPlayersWatchingChunk
4. Inject in ThreadedAnvilChunkStorage#entityTrackers

Related:
1. Call EntityTrackerEntry#startTracking
2. ThreadedAnvilChunkStorage#handlePlayerAddedOrRemoved
3. ServerPlayerEntity#updatePosition / ThreadedAnvilChunkStorage#updatePosition