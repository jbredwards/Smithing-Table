// Any changes to smithing recipes must be applied using the "crafttweaker" loader.
// https://docs.blamejared.com/1.12/en/AdvancedFunctions/Preprocessors/LoaderPreprocessor/#what-it-does
#loader crafttweaker

// ---
// Adds new smithing recipes that turn iron tools (with any damage) into diamond tools, using the smithing template created by the "templates.zs" example script.
// The <smithing_template:SMITHING_TEMPLATE_ID> is a special ingredient shortcut added by Smithing Table.
// Note: Due to a limitation with CraftTweaker, custom ingredient types themselves cannot be added, so this is just equivilant to:
// <smithing_table:template>.withTag({smithing_table: {TemplateId: "SMITHING_TEMPLATE_ID"}}).
mods.smithing_table.recipes.add("test1", <smithing_template:crafttweaker:example>, <minecraft:iron_axe>.anyDamage(), <minecraft:diamond>, <minecraft:diamond_axe>);
mods.smithing_table.recipes.add("test2", <smithing_template:crafttweaker:example>, <minecraft:iron_hoe>.anyDamage(), <minecraft:diamond>, <minecraft:diamond_hoe>);
mods.smithing_table.recipes.add("test3", <smithing_template:crafttweaker:example>, <minecraft:iron_pickaxe>.anyDamage(), <minecraft:diamond>, <minecraft:diamond_pickaxe>);
mods.smithing_table.recipes.add("test4", <smithing_template:crafttweaker:example>, <minecraft:iron_shovel>.anyDamage(), <minecraft:diamond>, <minecraft:diamond_shovel>);
mods.smithing_table.recipes.add("test5", <smithing_template:crafttweaker:example>, <minecraft:iron_sword>.anyDamage(), <minecraft:diamond>, <minecraft:diamond_sword>);

// ---
// An example smithing recipe that uses no smithing template.
mods.smithing_table.recipes.add("test6", null, <minecraft:stone_sword>.anyDamage(), <ore:ingotIron>, <minecraft:iron_sword>);

// ---
// See https://github.com/jbredwards/Smithing-Table/blob/1.12.2/src/main/java/git/jbredwards/smithing_table/mod/common/compat/crafttweaker/CRTSmithingRecipes.java
// to browse all "mods.smithing_table.recipes" functions.
