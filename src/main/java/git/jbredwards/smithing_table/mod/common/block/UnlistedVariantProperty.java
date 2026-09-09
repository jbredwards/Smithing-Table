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

package git.jbredwards.smithing_table.mod.common.block;

import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 *
 * @author jbred
 *
 */
public enum UnlistedVariantProperty implements IUnlistedProperty<TableData>
{
    INSTANCE;

    @Nonnull
    @Override
    public String getName() {
        return "variant";
    }

    @Override
    public boolean isValid(@Nullable final TableData value) {
        return value != null && value.meta >= 0 && value.item.delegate.name() != null;
    }

    @Nonnull
    @Override
    public Class<TableData> getType() {
        return TableData.class;
    }

    @Nonnull
    @Override
    public String valueToString(@Nullable final TableData value) {
        return Objects.toString(value);
    }
}
