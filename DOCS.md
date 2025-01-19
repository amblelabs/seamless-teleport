# How shit should work?
## Server:
When player walks into the proximity of a portal,
a player's [ghost] gets created at the other side.

The [ghost] delegates all method calls to the original player,
except for the custom `ServerPlayNetworkHandler`.

The [ghost] is locked to the portal's destination position.

When player walks through the portal, the [ghost] 
at the same side as the player gets removed.
