package dev.zenith;

import com.zenith.plugin.api.Plugin;
import com.zenith.plugin.api.PluginAPI;
import com.zenith.plugin.api.ZenithProxyPlugin;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import dev.zenith.module.ReaderModule;
import dev.zenith.narrator.command.NarratorCommand;

@Plugin(
    id = "narrator-plugin",
    version = "1.0",
    description = "Chat narrator plugin",
    authors = {"you"},
    mcVersions = "*"
)
public class NarratorPlugin implements ZenithProxyPlugin {

    public static ComponentLogger LOG;

    @Override
    public void onLoad(PluginAPI pluginAPI) {
        LOG = pluginAPI.getLogger();
        LOG.info("Narrator Plugin loading...");

        // ✅ Register your module (VERY IMPORTANT)
        pluginAPI.registerModule(new ReaderModule());

        // ✅ Register your command
        pluginAPI.registerCommand(new NarratorCommand());

        LOG.info("Narrator Plugin loaded!");
    }
}
