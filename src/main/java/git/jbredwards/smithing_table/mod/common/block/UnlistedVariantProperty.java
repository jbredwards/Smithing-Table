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
