package com.arcaryx.cobblemonintegrations.mixin.cobblemon;

import com.cobblemon.mod.common.pokemon.evolution.adapters.NbtItemPredicateAdapter;
import com.cobblemon.mod.common.pokemon.evolution.predicate.NbtItemPredicate;
import com.google.gson.*;
import net.minecraft.advancements.critereon.NbtPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(value = NbtItemPredicateAdapter.class, remap = false)
public abstract class MixinNbtItemPredicateAdapter {

    @Inject(method = "serialize(Lcom/cobblemon/mod/common/pokemon/evolution/predicate/NbtItemPredicate;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
    at = @At("HEAD"), require = 1, cancellable = true)
    private void serializeFix(NbtItemPredicate predicate, Type type, JsonSerializationContext context, CallbackInfoReturnable<JsonElement> cir) {
        var item = predicate.getItem();
        var ct = item.getClass();
        var serializedItemCondition = context.serialize(item, ct);

        if (predicate.getNbt() == NbtPredicate.ANY) {
            cir.setReturnValue(serializedItemCondition);
            return;
        }
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.add("item", serializedItemCondition);
        jsonObject1.add("nbt", predicate.getNbt().serializeToJson());
        cir.setReturnValue(jsonObject1);
        return;
    }
}
