package net.sircesarium.cartography.config;

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.sircesarium.cartography.Cartography;

public class CartographyClientConfig {
    public static final ConfigClassHandler<CartographyClientConfig> HANDLER = ConfigClassHandler.createBuilder(CartographyClientConfig.class)
            .id(Cartography.rl("client_config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("cartography-client.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = "client", group = "overlay")
    @TickBox
    @SerialEntry(comment = "Show the map overlay on screen")
    public boolean showOverlay = true;

    @AutoGen(category = "client", group = "overlay")
    @IntSlider(min = 0, max = 100, step = 5)
    @SerialEntry(comment = "Opacity of the map overlay (0-100)")
    public int overlayOpacity = 80;

    @AutoGen(category = "client", group = "hotkey")
    @TickBox
    @SerialEntry(comment = "Enable the map toggle hotkey")
    public boolean enableHotkey = true;
}
