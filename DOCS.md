

1. 

WorldRenderer:
1. this.setupTerrain(camera, frustum, bl, this.client.player.isSpectator()); // terain setup
2. this.updateChunks(camera); // compile chunks










ServerWorld -> ServerChunkManager -> ThreadedAnvilChunkStorage
1. Inject in ServerWorld#sendToPlayerIfNearby
2. Inject in PlayerManager#sendToAround
3. Inject in PlayerChunkWatchingManager#getPlayersWatchingChunk
4. Inject in ThreadedAnvilChunkStorage#entityTrackers

Related:
1. Call EntityTrackerEntry#startTracking
2. ThreadedAnvilChunkStorage#handlePlayerAddedOrRemoved
3. ServerPlayerEntity#updatePosition / ThreadedAnvilChunkStorage#updatePosition