// Any changes to smithing recipes must be applied using scripts in the "postInit" folder.

// ---
// An example crafting recipe that uses a smithing template, and outputs a smithing template.
// smithing_template(string) returns a new SmithingTemplateIngredient as an IIngredient.
// smithing_template_instance(string) finds the SmithingTemplate instance registered to the provided id.
crafting.shapedBuilder()
    .shape('NTN', 'NDN', 'NNN')
    .key([N: ore('stone'), T: smithing_template('placeholdername:example'), D: ore('ingotIron')])
    .output(smithing_template_instance('placeholdername:example').serialize() * 2)
    .register()

// ---
// An example smithing recipe that turns an iron pickaxe (with any damage) into a diamond pickaxe, using the smithing template created by the "templates.groovy" example script.
// Each new smithing template requires its own builder.
mods.smithing_table.recipes.builder()
    .input(smithing_template('placeholdername:example'), item('minecraft:iron_pickaxe:*'), ore('gemDiamond'))
    .output(item('minecraft:diamond_pickaxe'))
    .register()

// ---
// An example smithing recipe that uses no smithing template.
mods.smithing_table.recipes.builder()
    .input(ore('dirt'), ore('logWood'))
    .output(item('minecraft:nether_star') * 64)
    .register

// ---
// An example smithing recipe that uses a normal ingredient in the template slot.
mods.smithing_table.recipes.builder()
    .input(item("minecraft:stone_slab"), ore('dustRedstone'), item('minecraft:redstone_torch'))
    .output(item('minecraft:repeater'))
    .register()

// ---
// See https://github.com/jbredwards/Smithing-Table/blob/1.12.2/src/main/java/git/jbredwards/smithing_table/mod/common/compat/groovyscript/GRSSmithingRecipes.java
// to browse all "mods.smithing_table.recipes" functions.
