package dev.zenith.command;

import dev.zenith.ReaderPlugin;

public class NarratorCommand {

    private static final ReaderPlugin reader = new ReaderPlugin();

    public static void execute(String[] args) {

        if (args.length == 0) {
            System.out.println("Usage: Narrator ON/OFF");
            return;
        }

        switch (args[0].toLowerCase()) {

            case "on":
                reader.loadFile();
                reader.start();
                System.out.println("[Narrator] Started");
                break;

            case "off":
                reader.stop();
                System.out.println("[Narrator] Stopped");
                break;

            default:
                System.out.println("Usage: Narrator ON/OFF");
        }
    }
}
