package com.luxof.lapisworks.mixin;

import com.google.gson.JsonObject;

import com.luxof.lapisworks.mixinsupport.AccessPWBookEntries;

import dev.tizu.hexcessible.entries.BookEntries;
import dev.tizu.hexcessible.entries.BookEntries.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;

@Mixin(value = BookEntries.class, remap = false)
public class BookEntriesMixin implements AccessPWBookEntries {

    // this should be fine... Right?
    private static HashMap<String, List<BookEntries.Entry>> pwShapeEntries = new HashMap<>();

    @Inject(
        method = "reindex",
        at = @At("HEAD")
    )
    private void lapisworks$clearMyPWShapeEntries(CallbackInfo ci) {
        // mixin does this before init-ing my field
        if (pwShapeEntries == null) pwShapeEntries = new HashMap<>();
        else pwShapeEntries.clear();
    }

    @Inject(
        method = "lambda$reindex$1",
        at = @At("HEAD")
    )
    private static void lapisworks$putInPWShapeEntries(
        HashMap<String, List<BookEntries.Entry>> entries,
        BookEntry entry,
        HashMap<String, Supplier<Boolean>> locked,
        Identifier entryId,
        AtomicInteger pagei,
        BookPage page,
        CallbackInfo ci
    ) {
        JsonObject root = page.sourceObject;
        if (
            root == null ||
            !JsonHelper.getString(root, "type").equals("hexcasting:lapisworks/pwshape")
        ) return;

        String genericId = JsonHelper.getString(root, "op_id");
        String desc = JsonHelper.getString(root, "text", "");
        String in = JsonHelper.getString(root, "input", "");
        String out = JsonHelper.getString(root, "output", "");

        pwShapeEntries.computeIfAbsent(genericId, str -> new ArrayList<>())
            .add(new BookEntries.Entry(genericId, entryId, desc, in, out, pagei.getAndIncrement()));
    }

    @Override
    public List<Entry> getEntriesOfPWShapePattern(String genericId) {
        return pwShapeEntries.computeIfAbsent(genericId, str -> new ArrayList<>());
    }
}
