# SetSpawn

A lightweight and modern plugin for Paper/Spigot 1.21.x that allows players and admins to set and teleport to the spawn point.

## Features
- `/setspawn` - Sets the world spawn point to your current location.
- `/spawn` - Teleports you to the world spawn point.
- Fully configurable messages and cooldowns.
- Warmup feature (cancels teleport if the player moves).
- Uses the modern Paper Adventure API (Component).

## Commands & Permissions
| Command | Permission | Default |
|---------|------------|---------|
| `/setspawn` | `setspawn.set` | OP |
| `/spawn` | `setspawn.use` | Everyone |
| `/setspawn reload` | `setspawn.reload` | OP |

## Installation
1. Download the latest `.jar` from the Releases tab.
2. Drop it into your server's `plugins` folder.
3. Restart the server.
4. Configure `config.yml` to your liking.MIT License