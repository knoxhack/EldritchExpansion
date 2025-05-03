#!/bin/bash

# Create a temporary file
TMP_FILE=$(mktemp)

# Replace all occurrences of the problematic pattern
sed '179s/new ResourceLocation("minecraft:block\/water_still")/new ResourceLocation("minecraft", "block\/water_still")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

sed '184s/new ResourceLocation("minecraft:block\/water_flow")/new ResourceLocation("minecraft", "block\/water_flow")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

sed '218s/new ResourceLocation("minecraft:block\/water_still")/new ResourceLocation("minecraft", "block\/water_still")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

sed '223s/new ResourceLocation("minecraft:block\/water_flow")/new ResourceLocation("minecraft", "block\/water_flow")/' src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java > "$TMP_FILE"
cp "$TMP_FILE" src/main/java/com/eldritchvoid/modules/voidalchemy/VoidAlchemyFluids.java

# Clean up
rm "$TMP_FILE"
