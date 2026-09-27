package com.luxof.lapisworks.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import static com.luxof.lapisworks.Lapisworks.isModLoaded;
import static com.luxof.lapisworks.mixin.plugins.ModSpecificMCP.verDifference;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.luxof.lapisworks.platform.LapisworksPlatform;

/**
 * The condition Lapisworks' own data files use to require a mod <em>at a specific version</em>,
 * e.g. {@code "hexical@=2.0.0"}.
 * <p>
 * Fabric's stock {@code fabric:all_mods_loaded} only checks presence, which is not enough for
 * interop that targets a particular addon release, so Lapisworks registers its own condition. The
 * predicate below is loader-agnostic; only the registration hook differs (Fabric registers a
 * {@code ResourceConditions} entry, NeoForge registers a {@code ICondition} serializer), so the
 * hook goes through this class and each platform module supplies it.
 */
public class LapisResourceCons {
    /** The condition type other data files should reference, without the namespace. */
    public static final String CONDITION_PATH = "all_mods_loaded_with_specific_versions";

    private static String getVersion(Matcher match) {
        try { return match.group(2); }
        catch (IllegalStateException e) {
            throw new JsonParseException(
                "This constraint type requires a version that wasn't provided: " + match.group(1)
            );
        }
    }

    /**
     * Evaluates the condition body: a {@code values} array of {@code modid@<constraint>} strings,
     * all of which must hold. Throws {@link JsonParseException} on a malformed body, matching the
     * original behaviour.
     */
    public static boolean test(JsonObject json) {
        for (JsonElement ele : json.get("values").getAsJsonArray()) {
            if (!(ele instanceof JsonPrimitive primitive) || !primitive.isString())
                throw new JsonParseException("Invalid mod@version pair: " + ele.toString());

            String[] modAndVersion = ele.getAsString().split("@");
            if (modAndVersion.length != 2)
                throw new JsonParseException("Invalid mod@version pair: " + ele.getAsString());

            String modId = modAndVersion[0];
            String versionConstraint = modAndVersion[1];

            Pattern constraintPattern = Pattern.compile("(<=|>=|!=|[<>=*])(.*)");
            String constraintType;
            Matcher matcher = constraintPattern.matcher(versionConstraint);
            try {
                if (!matcher.find()) throw new IllegalStateException();
                constraintType = matcher.group(1);
            } catch (IllegalStateException e) {
                throw new JsonParseException("Invalid version constraint: " + versionConstraint);
            }

            if (!isModLoaded(modId)) return false;
            Integer verDiff = verDifference(modId, getVersion(matcher));

            if (!switch (constraintType) {
                case "<=" -> verDiff <= 0;
                case ">=" -> verDiff >= 0;
                case "<" -> verDiff < 0;
                case ">" -> verDiff > 0;
                case "!=" -> verDiff != 0;
                case "=" -> verDiff == 0;
                case "*" -> true;
                default -> false;
            })
                return false;
        }
        return true;
    }

    /**
     * Registers the condition type with whichever resource-condition system this loader has.
     * Called from shared init.
     */
    public static void doBondagePlay() {
        LapisworksPlatform.INSTANCE.registerResourceCondition(CONDITION_PATH, LapisResourceCons::test);
    }
}
