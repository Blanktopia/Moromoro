# Moromoro
Easily build custom items for your Minecraft server

## Setup

`src/main/resources/default/items/` holds the vanilla item definitions (`assets/minecraft/items/`) of the target client version.
Moromoro uses them as the fallback of each generated item definition.
To update them, run the script with the client jar of the new version:
```bash
./scripts/extract-models.sh ~/.minecraft/versions/26.3/26.3.jar
```

## Build
```
./gradlew shadowJar
```
