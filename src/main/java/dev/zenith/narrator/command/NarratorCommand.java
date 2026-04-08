package dev.zenith.narrator.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;

import dev.zenith.module.ReaderModule;

import static com.zenith.Globals.MODULE;
import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;

public class NarratorCommand extends Command {

    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("narrator")
            .category(CommandCategory.MODULE)
            .description("Controls the narrator module")
            .usageLines("on/off")
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("narrator")
            .then(argument("toggle", toggle()).executes(c -> {

                boolean enabled = getToggle(c, "toggle");

                // ✅ Get your module
                var module = MODULE.get(ReaderModule.class);

                if (enabled) {
                    module.enable();   // start reading
                } else {
                    module.disable();  // stop reading
                }

                c.getSource().getEmbed()
                    .title("Narrator " + toggleStrCaps(enabled));
            }));
    }

    @Override
    public void defaultEmbed(Embed embed) {
        embed
            .primaryColor()
            .addField("Status", "Use narrator on/off");
    }
}
