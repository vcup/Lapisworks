package com.luxof.lapisworks.mixin.plugins;

/**
 * Gates the hexical mixins that work against both hexical 1.5.0 and 2.0.0.
 * <p>
 * Only {@code ItemEntityMixin2} lives in this bucket: it supports the Cradle, which
 * {@code Lapixical.initHexicalInterop} registers on either version. Mixins that reach for
 * hexical 2.0.0-only classes belong in {@code lapisworks.fulllapixical.mixins.json} instead, which
 * is gated on {@code >= 2.0.0}.
 */
public class LapixicalMixinConfigPlugin extends ModSpecificMCP {
    public LapixicalMixinConfigPlugin() {
        super("hexical", null);
    }
}
