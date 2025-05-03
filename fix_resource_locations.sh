#!/bin/bash

# Create a temporary file
TMP_FILE=$(mktemp)

# Replace the first instance at line 179
sed '179s/ResourceLocation.of("minecraft:block\/water_still", :)/new ResourceLocation("minecraft:block\/water_still")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

# Replace the second instance at line 184
sed '184s/ResourceLocation.of("minecraft:block\/water_flow", :)/new ResourceLocation("minecraft:block\/water_flow")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

# Replace the third instance at line 218
sed '218s/ResourceLocation.of("minecraft:block\/water_still", :)/new ResourceLocation("minecraft:block\/water_still")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

# Replace the fourth instance at line 223
sed '223s/ResourceLocation.of("minecraft:block\/water_flow", :)/new ResourceLocation("minecraft:block\/water_flow")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

# Clean up
rm "$TMP_FILE"
