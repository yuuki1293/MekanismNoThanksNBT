package yuuki1293.mekanism_ntn.mixin;

import mekanism.api.DataHandlerUtils;
import mekanism.api.NBTConstants;
import mekanism.api.Upgrade;
import mekanism.common.block.BlockMekanism;
import mekanism.common.block.interfaces.IHasTileEntity;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.UpgradeUtils;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlockMekanism.class)
public abstract class BlockMekanismMixin {
    @Inject(method = "getDrops", at = @At(value = "TAIL"), cancellable = true)
    private void getDrops(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> drops = cir.getReturnValue();
        ItemStack drop = drops.get(0);
        drops.set(0, drop.getItem().getDefaultInstance()); // Remove additional tags

        if (state.getBlock() instanceof IHasTileEntity<?> hasTile) {
            BlockEntity tile = hasTile.createDummyBlockEntity(state);
            if (tile instanceof TileEntityMekanism) {
                // Drop Inventory Items
                if (ItemDataUtils.hasData(drop, NBTConstants.ITEMS, Tag.TAG_LIST)) {
                    ListTag items = ItemDataUtils.getList(drop, NBTConstants.ITEMS);
                    int count = DataHandlerUtils.getMaxId(items, NBTConstants.ITEM);
                    for (int i = 0; i < count; i++) {
                        var itemCompound = items.getCompound(i);
                        var itemTag = itemCompound.getCompound(NBTConstants.ITEM);
                        if (itemCompound.contains(NBTConstants.SIZE_OVERRIDE)) { // bin
                            var stackSize = itemCompound.getInt(NBTConstants.SIZE_OVERRIDE);
                            List<ItemStack> partial = new ArrayList<>();
                            while(stackSize > 0){
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

                // Drop Upgrades
                if (ItemDataUtils.hasData(drop, NBTConstants.COMPONENT_UPGRADE, Tag.TAG_COMPOUND)) {
                    final var upgrades = Upgrade.buildMap(ItemDataUtils.getCompound(drop, NBTConstants.COMPONENT_UPGRADE));
                    upgrades.forEach((upgrade, count) -> {
                        final var upgradeStack = UpgradeUtils.getStack(upgrade, count);
                        drops.add(upgradeStack);
                    });
                }
            }
        }

        cir.setReturnValue(drops);
    }
}