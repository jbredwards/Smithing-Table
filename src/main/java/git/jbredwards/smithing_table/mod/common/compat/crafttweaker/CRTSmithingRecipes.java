package git.jbredwards.smithing_table.mod.common.compat.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.OnRegister;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.events.ActionApplyEvent;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.api.SmithingRecipe;
import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author jbred
 *
 */
@ZenRegister
@ZenClass("mods." + SmithingTable.MOD_ID + ".recipes")
public final class CRTSmithingRecipes
{
    @Nonnull static final List<IAction> addActions = new ArrayList<>();
    @Nonnull static final List<IAction> removeActions = new ArrayList<>();

    @OnRegister
    public static void preInit() {
        MinecraftForge.EVENT_BUS.register(CRTSmithingRecipes.class);
    }

    @SubscribeEvent
    static void postInit(@Nonnull final ActionApplyEvent.Post event) {
        removeActions.forEach(CraftTweakerAPI::apply);
        addActions.forEach(CraftTweakerAPI::apply);
    }

    @ZenMethod
    public static void add(@Nullable final String loc, @Nullable final IIngredient template, @Nullable final IIngredient equipment, @Nullable final IIngredient material, @Nullable final IIngredient output, @Optional @Nullable final String soundLoc) {
        if(loc == null) CraftTweakerAPI.logError("Cannot add recipe with a null name!");
        else runnable(addActions, "Adding recipe with id: " + loc, () -> {
            @Nonnull final SoundEvent sound = java.util.Optional.ofNullable(soundLoc).map(s -> SoundEvent.REGISTRY.getObject(new ResourceLocation(s))).orElse(SmithingContent.BLOCK_SMITHING_TABLE_USE);
            SmithingRecipe.REGISTRY.register(new SmithingRecipe.Impl(sound, CraftTweakerMC.getIngredient(template), CraftTweakerMC.getIngredient(equipment), CraftTweakerMC.getIngredient(material), CraftTweakerMC.getItemStack(output)).setRegistryName(loc));
        });
    }

    @ZenMethod
    public static void remove(@Nullable final String loc) {
        if(loc == null) CraftTweakerAPI.logError("Cannot remove recipe with a null name!");
        else runnable(removeActions, "Removing recipe with id: " + loc, () -> removeKeys(Stream.of(new ResourceLocation(loc))));
    }

    @ZenMethod
    public static void removeAll() {
        runnable(removeActions, "Removing all recipes", () -> removeKeys(SmithingRecipe.REGISTRY.getKeys().stream()));
    }

    @ZenMethod
    public static void removeByTemplate(@Nullable final IIngredient template) {
        runnable(removeActions, "Removing all recipes with template: " + template, () -> {
            @Nonnull final ItemStack[] matching = CraftTweakerMC.getIngredient(template).getMatchingStacks();
            removeKeys(SmithingRecipe.REGISTRY.getValuesCollection().stream()
                    .filter(recipe -> {
                        for(@Nonnull final ItemStack stack : matching) if(recipe.getTemplateIngredient().test(stack)) return true;
                        return false;
                    })
                    .map(SmithingRecipe::getRegistryName));
        });
    }

    @ZenMethod
    public static void removeByEquipment(@Nullable final IIngredient equipment) {
        runnable(removeActions, "Removing all recipes with equipment: " + equipment, () -> {
            @Nonnull final ItemStack[] matching = CraftTweakerMC.getIngredient(equipment).getMatchingStacks();
            removeKeys(SmithingRecipe.REGISTRY.getValuesCollection().stream()
                    .filter(recipe -> {
                        for(@Nonnull final ItemStack stack : matching) if(recipe.getEquipmentIngredient().test(stack)) return true;
                        return false;
                    })
                    .map(SmithingRecipe::getRegistryName));
        });
    }

    @ZenMethod
    public static void removeByMaterial(@Nullable final IIngredient material) {
        runnable(removeActions, "Removing all recipes with material: " + material, () -> {
            @Nonnull final ItemStack[] matching = CraftTweakerMC.getIngredient(material).getMatchingStacks();
            removeKeys(SmithingRecipe.REGISTRY.getValuesCollection().stream()
                    .filter(recipe -> {
                        for(@Nonnull final ItemStack stack : matching) if(recipe.getMaterialIngredient().test(stack)) return true;
                        return false;
                    })
                    .map(SmithingRecipe::getRegistryName));
        });
    }

    @ZenMethod
    public static void removeByInput(@Nullable final IIngredient input) {
        runnable(removeActions, "Removing all recipes with input: " + input, () -> {
            @Nonnull final ItemStack[] matching = CraftTweakerMC.getIngredient(input).getMatchingStacks();
            removeKeys(SmithingRecipe.REGISTRY.getValuesCollection().stream()
                    .filter(recipe -> {
                        for(@Nonnull final ItemStack stack : matching) if(recipe.getTemplateIngredient().test(stack)
                        || recipe.getEquipmentIngredient().test(stack) || recipe.getMaterialIngredient().test(stack)) return true;
                        return false;
                    })
                    .map(SmithingRecipe::getRegistryName));
        });
    }

    @ZenMethod
    public static void removeByOutput(@Nullable final IIngredient output) {
        runnable(removeActions, "Removing all recipes with output: " + output, () -> {
            @Nonnull final Ingredient ingredient = CraftTweakerMC.getIngredient(output);
            removeKeys(SmithingRecipe.REGISTRY.getValuesCollection().stream()
                    .filter(recipe -> ingredient.test(recipe.getResult()))
                    .map(SmithingRecipe::getRegistryName));
        });
    }

    private static void removeKeys(@Nonnull final Stream<ResourceLocation> keys) {
        keys.forEach(((IForgeRegistryModifiable<SmithingRecipe>)SmithingRecipe.REGISTRY)::remove);
    }
    
    private static void runnable(@Nonnull final List<IAction> actions, @Nonnull final String describe, @Nonnull final Runnable action) {
        if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) CraftTweakerAPI.apply(runnable(describe, action));
        else actions.add(runnable(describe, action));
    }
 
    @Nonnull
    private static IAction runnable(@Nonnull final String describe, @Nonnull final Runnable action) {
        return new IAction() {
            @Override
            public void apply() {
                action.run();
            }

            @Nonnull
            @Override
            public String describe() {
                return describe;
            }
        };
    }
}
