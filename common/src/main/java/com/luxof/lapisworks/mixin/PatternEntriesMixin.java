package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.init.ThemConfigFlags;
import com.luxof.lapisworks.mixinsupport.AccessPWBookEntries;
import com.luxof.lapisworks.mixinsupport.HexcessiblePWShapeSupport;

import static com.luxof.lapisworks.init.Mutables.Mutables.wizardDiariesGainableAdvancements;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;
import static com.luxof.lapisworks.init.ThemConfigFlags.specificToGenericId;

import dev.tizu.hexcessible.entries.BookEntries;
import dev.tizu.hexcessible.entries.PatternEntries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.base.ClientAdvancements;

@Mixin(value = PatternEntries.class, remap = false)
public class PatternEntriesMixin implements HexcessiblePWShapeSupport {

    @Shadow private List<PatternEntries.Entry> entries;
    @Shadow private List<String> perWorld;
    @Unique public HashMap<String, PatternEntries.Entry> pwShapePatterns = new HashMap<>();

    @Inject(
        method = "reindex",
        at = @At("HEAD")
    )
    public void lapisworks$clearThosePWShapePatternEntries(CallbackInfo ci) {
        pwShapePatterns.clear();
    }

    @Inject(
        method = "reindex",
        at = @At("TAIL")
    )
    private void lapisworks$removePWShapePatterns(CallbackInfo ci) {
        List<PatternEntries.Entry> toRemove = new ArrayList<>();
        for (PatternEntries.Entry entry : entries) {
            String specificId = entry.id();

            if (ThemConfigFlags.isPWShapePattern(specificId)) {
                String genericId = specificToGenericId.get(specificId);
                toRemove.add(entry);

                List<BookEntries.Entry> bookEntries = getEntriesForPWShapePattern(genericId);

                /** "more like IHexplatAbstractions" -mauve. i saw that comment mauve */
                pwShapePatterns.put(
                    specificId,
                    new PatternEntries.Entry(
                        specificId,
                        entry.name(),
                        () -> false,
                        entry.dir(),
                        // entry.sig() calls entry.isPerWorld() which throws an error at this stage
                        List.of(IXplatAbstractions.INSTANCE.getActionRegistry()
                            .get(new Identifier(entry.id()))
                            .prototype()
                            .getAngles()),
                        bookEntries,
                        0
                    )
                );
            }
        }
        entries.removeAll(toRemove);
    }

    @Unique
    private static List<BookEntries.Entry> getEntriesForPWShapePattern(String genericId) {
        // for some reason these lists are always fucking empty
        // in fact, the entire book is empty at this stage.
        // why???
        return ((AccessPWBookEntries)BookEntries.INSTANCE).getEntriesOfPWShapePattern(genericId);
    }

    @Override
    public void unlockPWShapeByAdvancement(Identifier advancementId) {
        String genericId = wizardDiariesGainableAdvancements.get(advancementId);
        if (genericId == null) return;
        int chosen = chosenFlags.get(genericId);

        PatternEntries.Entry entry = pwShapePatterns.get(genericId + String.valueOf(chosen));
        entries.add(entry);
    }

    @Override
    public void calibratePWShapeUnlocks() {
        for (Identifier advancement : wizardDiariesGainableAdvancements.keySet()) {
            if (ClientAdvancements.hasDone(advancement.toString()))
                unlockPWShapeByAdvancement(advancement);
        }
    }
}
