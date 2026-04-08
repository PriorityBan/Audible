package dev.zenith.narrator.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.zenith.command.api.Command;
import com.zenith.command.api.CommandCategory;
import com.zenith.command.api.CommandContext;
import com.zenith.command.api.CommandUsage;
import com.zenith.discord.Embed;
import dev.zenith.ReaderPlugin;

import static com.zenith.command.brigadier.ToggleArgumentType.getToggle;
import static com.zenith.command.brigadier.ToggleArgumentType.toggle;

public class NarratorCommand extends Command {

    private static final ReaderPlugin reader = new ReaderPlugin();

    @Override
    public CommandUsage commandUsage() {
        return CommandUsage.builder()
            .name("narrator")
            .category(CommandCategory.MODULE)
            .description("Controls the book narrator")
            .usageLines("on/off")
            .build();
    }

    @Override
    public LiteralArgumentBuilder<CommandContext> register() {
        return command("narrator")
            .then(argument("toggle", toggle()).executes(c -> {

                boolean enabled = getToggle(c, "toggle");

                if (enabled) {
                    reader.loadFile();
                    reader.start(c); // ✅ CORRECT
                } else {
                    reader.stop();
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
