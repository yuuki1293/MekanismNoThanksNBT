package yuuki1293.mekanism_ntn;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.stream.Stream;

public class MekanismNTNConfig {
    private static final List<String> defaultWhiteList = List.of();
    private static final List<String> defaultBlackList = Stream.of(
        "basic_bin", "advanced_bin", "elite_bin", "ultimate_bin", "creative_bin", // All Bins
        "basic_induction_cell", "advanced_induction_cell", "elite_induction_cell", "ultimate_induction_cell", // All Induction Cells
        "digital_miner", // Digital Miner
        "personal_barrel", "personal_chest", // Personal Barrel and Chest
        "logistical_sorter", // Logistical Sorter
        "basic_fluid_tank", "advanced_fluid_tank", "elite_fluid_tank", "ultimate_fluid_tank", "creative_fluid_tank", // All Fluid Tanks
        "basic_energy_cube", "advanced_energy_cube", "elite_energy_cube", "ultimate_energy_cube", "creative_energy_cube", // All Energy Cubes
        "basic_chemical_tank", "advanced_chemical_tank", "elite_chemical_tank", "ultimate_chemical_tank", "creative_chemical_tank", // All Chemical Tanks
        "cardboard_box" // CardBoard Box
    ).map(value -> "mekanism:" + value).toList();

    public static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue Enabled;
    public static final ForgeConfigSpec.BooleanValue DefaultEnabled;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> Whitelist;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> Blacklist;

    static {
        builder.comment("This is Mekanism No Thanks NBT Config");
        builder.push("general");

        // region [general]
        Enabled = builder.comment("master switch. default = true")
            .define("enabled", true);
        DefaultEnabled = builder.comment("default mode. default = true")
            .define("defaultEnabled", true);
        Whitelist = builder.comment("target block whitelist.")
            .defineList("whitelist", defaultWhiteList, element -> true);
        Blacklist = builder.comment("target block blacklist.")
            .defineList("blacklist", defaultBlackList, element -> true);
        builder.pop();
        // endregion [general]
    }
}
