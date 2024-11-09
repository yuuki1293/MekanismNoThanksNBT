package yuuki1293.mekanism_ntn.mixin;

import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static yuuki1293.mekanism_ntn.MekanismNTNConfig.*;

@Mixin(BlockMekanism.class)
public abstract class BlockMekanismMixin {
    @Inject(method = "getDrops", at = @At(value = "TAIL"), cancellable = true)
    private void getDrops(@NotNull BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        var block = state.getBlock();

        if (!(block instanceof IHasTileEntity<?> hasTile)) return; // Block has BlockEntity
        BlockEntity tile = hasTile.createDummyBlockEntity(state);
        if (!(tile instanceof TileEntityMekanism)) return; // Block is Mekanism BlockEntity
        // Execute if block is Mekanism block

        var registryName = block.getRegistryName();
        var blacklist = Blacklist.get().stream().map(ResourceLocation::tryParse).toList();
        var whitelist = Whitelist.get().stream().map(ResourceLocation::tryParse).toList();

        if (!Enabled.get()) return; // Master switch
        if (blacklist.contains(registryName)) return; // Blacklisted
        if (!whitelist.contains(registryName) && !DefaultEnabled.get()) return; // Default disabled and not whitelisted
        // Execute if whitelisted or default enabled

        List<ItemStack> drops = cir.getReturnValue();
        ItemStack drop = drops.get(0);
        drops.set(0, drop.getItem().getDefaultInstance()); // Remove additional tags

        // region Drop Inventory Items
        if (ItemDataUtils.hasData(drop, NBTConstants.ITEMS, Tag.TAG_LIST)) {
            ListTag items = ItemDataUtils.getList(drop, NBTConstants.ITEMS);
            int count = items.size();
            for (int i = 0; i < count; i++) {
                var itemCompound = items.getCompound(i);
                var itemTag = itemCompound.getCompound(NBTConstants.ITEM);
                if (itemCompound.contains(NBTConstants.SIZE_OVERRIDE)) { // bin
                    var stackSize = itemCompound.getInt(NBTConstants.SIZE_OVERRIDE);
                    List<ItemStack> partial = new ArrayList<>();
                    while (stackSize > 0) {
                        var amount = Math.min(stackSize, 64);
                        stackSize -= amount;
                        var newStack = ItemStack.of(itemTag);
                        newStack.setCount(amount);
                        partial.add(newStack);
                    }
                    drops.addAll(partial);
                } else {
                    drops.add(ItemStack.of(itemTag));
                }
            }
        }
        // endregion Drop Inventory Items

        // region Drop Upgrades
        if (ItemDataUtils.hasData(drop, NBTConstants.COMPONENT_UPGRADE, Tag.TAG_COMPOUND)) {
            var upgrades = Upgrade.buildMap(ItemDataUtils.getCompound(drop, NBTConstants.COMPONENT_UPGRADE));
            upgrades.forEach((upgrade, count) -> {
                final var upgradeStack = UpgradeUtils.getStack(upgrade, count);
                drops.add(upgradeStack);
            });

            var upgradeCompound = ItemDataUtils.getCompound(drop, NBTConstants.COMPONENT_UPGRADE);
            if(upgradeCompound.contains(NBTConstants.ITEMS, Tag.TAG_LIST)){
                var items = upgradeCompound.getList(NBTConstants.ITEMS, Tag.TAG_COMPOUND);
                var count = items.size();
                for (int i = 0; i < count; i++) {
                    var itemCompound = items.getCompound(i);
                    var itemTag = itemCompound.getCompound(NBTConstants.ITEM);
                    drops.add(ItemStack.of(itemTag));
                }
            }
        }
        // endregion Drop Upgrades

        cir.setReturnValue(drops);
    }
}
