package org.example;

import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class ReaderPlugin {

    /* ================================
       🔧 EDITABLE SETTINGS (CHANGE THESE)
       ================================ */

    private int minDelayMs = 3000;         // ⏱ Minimum delay
    private int maxDelayMs = 4000;         // ⏱ Maximum delay
    private boolean randomOrder = false; // 🎲 Shuffle lines
    private boolean loop = false;        // 🔁 Loop when finished
    private int maxMessages = -1;       // 📊 Limit (-1 = infinite)
    private String fileName = "FinalSmartSpam.txt"; // 📂 Your file

    /* ================================
       ⚙️ INTERNAL STATE (DON'T TOUCH)
       ================================ */

    private List<String> messages = new ArrayList<>();
    private int index = 0;
    private boolean running = false;

    /* ================================
       📂 LOAD TEXT FILE
       ================================ */

    public void loadFile() {
        try {
            messages = Files.readAllLines(Paths.get(fileName))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();

            if (randomOrder) {
                Collections.shuffle(messages);
            }

            System.out.println("[ReaderPlugin] Loaded " + messages.size() + " lines");

        } catch (IOException e) {
            e.printStackTrace();
            messages = List.of("Error loading file");
        }
    }

    /* ================================
       🚀 START SPAMMING
       ================================ */

    public void start() {
        if (running) return;

        running = true;
        MinecraftClient client = MinecraftClient.getInstance();

        new Thread(() -> {
            int sent = 0;

            while (running) {
                try {
                    if (messages.isEmpty()) continue;

                    if (index >= messages.size()) {
                        if (loop) {
                            index = 0;
                            if (randomOrder) Collections.shuffle(messages);
                        } else {
                            break;
                        }
                    }

                    String msg = messages.get(index);

                    if (client.player != null) {
                        client.player.networkHandler.sendChatMessage(msg);
                    }

                    index++;
                    sent++;

                    if (maxMessages != -1 && sent >= maxMessages) {
                        break;
                    }

                    int randomDelay = minDelay + new Random().nextInt(maxDelay - minDelay + 1);
                    Thread.sleep(randomDelay);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            running = false;
            System.out.println("[ReaderPlugin] Stopped");

        }).start();
    }

    /* ================================
       🛑 STOP
       ================================ */

    public void stop() {
        running = false;
    }

    /* ================================
       🎮 SIMPLE TOGGLE (OPTIONAL)
       ================================ */

    public void toggle() {
        if (running) {
            stop();
        } else {
            loadFile();
            start();
        }
    }
}
