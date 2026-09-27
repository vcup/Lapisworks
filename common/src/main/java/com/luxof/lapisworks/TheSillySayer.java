package com.luxof.lapisworks;

import static com.luxof.lapisworks.Lapisworks.log;

import java.util.List;

public class TheSillySayer {
    private static List<String> list(String... strs) {
        return List.of(strs);
    }
    private static List<List<String>> thingsNormally = List.of(
        list("Luxof's pet Lapisworks is getting a bit hyperactive."),
        list(
            "Oh wow, you actually loaded up Minecraft?",
            "How many days left in the phase?",
            "Anyway, I have to tell you something REALLY important!"
        ),
        list("This is literally 3D Terraria. To demonstrate, I've--"),
        list("Find the creature NOW!")
    );
    private static List<List<String>> thingsInterop = List.of(
        list("It's barfing! It's barfing!"),
        list("Feed your pet Lapis redstone, it's big enough to eat it."),
        list("--summoned the Terrarian here. You can't see him because he's two dimensional."),
        list("Oh, it's right here.")
    );
    private static List<List<String>> thingsNoInterop = List.of(
        list("Doctors recommend you feed your pet Lapis at least one redstone dust a day."),
        list("Don't feed your pet Lapis redstone, that's going to kill it!!"),
        list("--wait, where am I?"),
        list("Feed it redstone!")
    );

    private static int indexToday = (int)Math.floor(Math.random() * thingsNormally.size());
    private static void say(List<List<String>> things) {
        for (String str : things.get(indexToday)) {
            log(str);
        }
    }

    public static void sayNormal() { say(thingsNormally); }
    public static void sayInterop() { say(thingsInterop); }
    public static void sayNoInterop() { say(thingsNoInterop); }
}
