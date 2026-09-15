// Any new smithing templates must be added using scripts in the "preInit" folder.

// ---
// This class holds all smithing table gui slot textures that come bundled with Smithing Table.
import static git.jbredwards.smithing_table.api.SmithingSlotInfo.*

// ---
// This is what actually adds the smithing template.
// Each new smithing template requires its own builder.
mods.smithing_table.templates.builder()
    .name('example')
    .equipmentSlotInfo('This is example hover text', EMPTY_SLOT_EQUIPMENT)
    .materialSlotInfo('Example hover text for material', [EMPTY_SLOT_GEM_DIAMOND])
    .register()

// ---
// See https://github.com/jbredwards/Smithing-Table/blob/1.12.2/src/main/java/git/jbredwards/smithing_table/mod/common/compat/groovyscript/GRSSmithingTemplates.java
// to browse all "mods.smithing_table.templates" functions.
