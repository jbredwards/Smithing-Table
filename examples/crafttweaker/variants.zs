// Any changes to smithing variants must be applied using the "crafttweaker" loader.
// https://docs.blamejared.com/1.12/en/AdvancedFunctions/Preprocessors/LoaderPreprocessor/#what-it-does
#loader crafttweaker

// ---
// This mod provides its smithing table crafting recipe generator as a "config/smithing_table/recipe.json" file.
// You can change the auto-generated recipes using that file, where the <ore:plankWood> ingredient is replaced for all variants at runtime.
// Clearing that file's contents (so it's an empty file) will disable any automatic recipe generation.

// ---
// Removes all default smithing table variants.
// This does not remove the oak variant, which must exist as the "default" variant.
// Note: Removed variants must have their crafting recipes manually removed.
mods.smithing_table.variants.removeAll();

// ---
// Adds a diamond block smithing table variant.
// Note: Any new variants must have their crafting recipes created manually.
mods.smithing_table.variants.add(<minecraft:diamond_block>);

// ---
// See https://github.com/jbredwards/Smithing-Table/blob/1.12.2/src/main/java/git/jbredwards/smithing_table/mod/common/compat/crafttweaker/CRTSmithingVariants.java
// to browse all "mods.smithing_table.variants" functions.
