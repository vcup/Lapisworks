package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registry;

import vazkii.patchouli.api.PatchouliAPI;

// really evil fucking bullshit to circumvent some stuff that was probably there for a reason
// why?
// i like pretty shit
// i guess you could also make the excuse for originality too, afaik no one has done
// per-world shape patterns
public class ThemConfigFlags {

    public static HashMap<String, Integer> chosenFlags = new HashMap<>();
    public static HashMap<String, String> specificToGenericId = new HashMap<>();
    public static HashMap<String, List<String>> allPerWorldShapePatterns = new HashMap<>();
    public static HashSet<String> pwShapePatterns = new HashSet<>();

    public static boolean isPWShapePattern(String nonGenericId) {
        return pwShapePatterns.contains(nonGenericId);
    }

    public static NbtCompound turnChosenIntoNbt() {
        NbtCompound nbt = new NbtCompound();
        chosenFlags.forEach((key, val) -> { nbt.putInt(key, val); });
        return nbt;
    }

    /** Call after you register the variant patterns.
     * <p>Grabs all patterns with the id <code>{genericId}{X}</code> where X is an int.
     * Well it technically grabs all patterns prefixed
     * <p>There needn't be a range of patterns with X between 0 and (amount prefixed with genericId).
     * <p>Yes, you may use this during registration to add variants to existing PW Shape Patterns. */
    public static void registerPWShapePattern(String genericId) {
        chosenFlags.put(genericId, null);

        List<String> sigs = new ArrayList<>();
        Map<String, String> unsorted = new HashMap<>();

        Registry<ActionRegistryEntry> registry = IXplatAbstractions.INSTANCE.getActionRegistry();
        int genericIdLen = genericId.length();

        for (var key : registry.getKeys()) {

            String id = key.getValue().toString();
            if (id.startsWith(genericId) && isInt(id.substring(genericIdLen))) {
                specificToGenericId.put(id, genericId);
                unsorted.put(id, registry.get(key).prototype().anglesSignature());
                pwShapePatterns.add(id);
            }

        }

        for (Integer i = 0; i < unsorted.keySet().size(); i++) {
            String id = genericId + i.toString();
            if (unsorted.containsKey(id))
                sigs.add(unsorted.get(id));
        }

        allPerWorldShapePatterns.put(genericId, sigs);
    }

    private static boolean isInt(String str) {
        try { Integer.parseInt(str); return true; }
        catch (NumberFormatException e) { return false; }
    }

    /** Call after initializing your PW Shape Patterns.
     * <p>Safe to call more than once. */
    public static void declareEm() {
        allPerWorldShapePatterns.keySet().forEach(
            (String id) -> {
                List<String> sigs = allPerWorldShapePatterns.get(id);
                for (int i = 0; i < sigs.size(); i++) {
                    PatchouliAPI.get().setConfigFlag(id + String.valueOf(i), false);
                }
            }
        );
    }
}
