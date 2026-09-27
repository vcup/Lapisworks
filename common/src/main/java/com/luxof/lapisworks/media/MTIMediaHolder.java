package com.luxof.lapisworks.media;

import at.petrak.hexcasting.api.item.MediaHolderItem;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

/** i COULD implement MTI for <code>ItemStacks</code> but... Nah. */
public class MTIMediaHolder implements MediaTransferInterface {
    public MediaHolderItem innerItem;
    public ItemStack stack;

    public MTIMediaHolder(ItemStack from) {
        stack = from;
        innerItem = (MediaHolderItem)from.getItem();
    }

    @Override @Nullable public Vec3d getPosIfPossible() { return null; }
    @Override public void setMediaHere(long media) { innerItem.setMedia(stack, media); }
    @Override public long getMaxMedia() { return innerItem.getMaxMedia(stack); }
    @Override public long getMediaHere() { return innerItem.getMedia(stack); }
}
