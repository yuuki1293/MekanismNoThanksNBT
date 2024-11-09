package yuuki1293.mekanism_ntn;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MekanismNTN.MODID)
public class MekanismNTN {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "mekanism_ntn";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MekanismNTN() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MekanismNTNConfig.builder.build());
        LOGGER.debug("Mekanism NTN config loaded");
    }
}
