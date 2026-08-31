package git.jbredwards.smithing_table.mod.common.block;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A generic item-metadata pair class.
 *
 * @author jbred
 *
 */
public final class TableData
{
    @Nonnull
    public static final TableData DEFAULT = new TableData(Item.getItemFromBlock(Blocks.PLANKS), 0);

    @Nonnull
    public final Item item;
    public final int meta;

    public TableData(@Nonnull final Item itemIn, final int metaIn) {
        item = Objects.requireNonNull(itemIn);
        meta = metaIn;
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if(obj == null || obj.getClass() != TableData.class) return false;
        @Nonnull final TableData o = (TableData)obj;
        return o.item == item && o.meta == meta;
    }

    @Override
    public int hashCode() {
        return (item.hashCode() << 8) + meta;
    }

    @Nonnull
    @Override
    public String toString() {
        return serializeNBT().toString();
    }

    @Nonnull
    public NBTTagCompound serializeNBT() {
        @Nonnull final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("PlanksId", Objects.toString(item.getRegistryName()));
        tag.setInteger("PlanksMeta", meta);
        return tag;
    }

    @Nonnull
    public static TableData deserialize(@Nonnull final NBTTagCompound tag) {
        @Nullable final Item item = Item.getByNameOrId(tag.getString("PlanksId"));
        return item != null ? new TableData(item, tag.getInteger("PlanksMeta")) : DEFAULT;
    }
}
