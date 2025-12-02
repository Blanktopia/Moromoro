# Moromoro
Easily build custom items for your Minecraft server

## Setup

The following folders should be extracted from the Minecraft jar:
- `src/main/resources/default/models/` from `assets/minecraft/models/`
- `src/main/resources/default/items/` from `assets/minecraft/items/`

You can use the provided script to extract both:
```bash
./extract-models.sh ~/.minecraft/versions/1.21.5/1.21.5.jar
```

## Build
```
./gradlew shadowJar
```
