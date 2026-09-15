/*
 * Copyright (C) <2026 to Present> <jbredwards>
 *
 * All rights are reserved, except where explicitly granted by the original
 * copyright holder or where explicitly granted by the Mod Permissions License as
 * published by Jbredwards, either version 1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 *
 * See the Mod Permissions License for more details
 * <https://www.github.com/jbredwards/mod-permissions-license>.
 */

package git.jbredwards.smithing_table.mod.common.compat.crafttweaker;

import crafttweaker.annotations.BracketHandler;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.DataString;
import crafttweaker.api.data.IData;
import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import crafttweaker.zenscript.GlobalRegistry;
import crafttweaker.zenscript.IBracketHandler;
import git.jbredwards.smithing_table.api.SmithingContent;
import git.jbredwards.smithing_table.mod.SmithingTable;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.compiler.IEnvironmentGlobal;
import stanhebben.zenscript.expression.ExpressionCallStatic;
import stanhebben.zenscript.expression.ExpressionString;
import stanhebben.zenscript.parser.Token;
import stanhebben.zenscript.symbols.IZenSymbol;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jbred
 *
 */
@ZenRegister
@BracketHandler(priority = 100)
public final class CRTHandler implements IBracketHandler
{
    @Nonnull
    public static IItemStack parse(@Nonnull final String str) {
        // CraftTweaker doesn't allow for custom ingredient types, so we're stuck using NBT instead. Causes some issues, like checking whole tag & damage.
        return new MCItemStack(new ItemStack(SmithingContent.SMITHING_TEMPLATE)).withTag(tag(SmithingTable.MOD_ID, tag("TemplateId", new DataString(str))));
    }

    @Nonnull
    private static DataMap tag(@Nonnull final String key, @Nonnull final IData value) {
        @Nonnull final Map<String, IData> map = new HashMap<>();
        map.put(key, value);
        return new DataMap(map, false);
    }

    @Nullable
    @Override
    public IZenSymbol resolve(@Nonnull final IEnvironmentGlobal environment, @Nonnull final List<Token> tokens) {
        if(tokens.size() > 2 && tokens.get(0).getValue().equals("smithing_template") && tokens.get(1).getValue().equals(":")) {
            @Nonnull final String str = tokens.stream().skip(2).map(Token::getValue).collect(Collectors.joining());
            @Nonnull final IJavaMethod method = JavaMethod.get(GlobalRegistry.getTypes(), CRTHandler.class, "parse", String.class);
            return position -> new ExpressionCallStatic(position, environment, method, new ExpressionString(position, str));
        }

        return null;
    }

    @Nonnull
    @Override
    public Class<?> getReturnedClass() {
        return IItemStack.class;
    }

    @Nonnull
    @Override
    public String getRegexMatchingString() {
        return "smithing_template:.*:.*";
    }
}
