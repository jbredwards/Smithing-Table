// Any new smithing templates must be added using the "preinit" loader.
// https://docs.blamejared.com/1.12/en/AdvancedFunctions/Preprocessors/LoaderPreprocessor/#what-it-does
#loader preinit

// ---
// Smithing table gui slot texture constants.
// If an array of textures is used (like EMPTY_SLOT_EQUIPMENT), the gui will gradually cycle through each texture.
var EMPTY_SLOT_GEM_DIAMOND = "smithing_table:gui/slot/gem_diamond";
var EMPTY_SLOT_EQUIPMENT = [
    "minecraft:items/empty_armor_slot_helmet",
    "smithing_table:gui/slot/tool_sword",
    "minecraft:items/empty_armor_slot_chestplate",
    "smithing_table:gui/slot/tool_pickaxe",
    "minecraft:items/empty_armor_slot_leggings",
    "smithing_table:gui/slot/tool_axe",
    "minecraft:items/empty_armor_slot_boots",
    "smithing_table:gui/slot/tool_hoe",
    "smithing_table:gui/slot/tool_shovel"
] as string[];

// ---
// This is what actually adds the smithing template.
// Each new smithing template requires its own builder.
mods.smithing_table.templates.builder()
    .name('example')
    .equipmentSlotInfo('Example hover text for equipment', EMPTY_SLOT_EQUIPMENT)
    .materialSlotInfo('Example hover text for material', [EMPTY_SLOT_GEM_DIAMOND])
    .register();

// ---
// See https://github.com/jbredwards/Smithing-Table/blob/1.12.2/src/main/java/git/jbredwards/smithing_table/mod/common/compat/crafttweaker/CRTSmithingTemplates.java
// to browse all "mods.smithing_table.templates" functions.
