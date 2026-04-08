package dev.zenith;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import com.zenith.command.api.CommandContext;

public class ReaderPlugin {

    /* ================================
       🔧 EDITABLE SETTINGS (CHANGE THESE)
       ================================ */
    private final Random random = new Random();
    private int minDelayMs = 3000;
    private int maxDelayMs = 4000;
    private boolean randomOrder = false;
    private boolean loop = false;
    private int maxMessages = -1;
    private String fileName = "FinalSmartSpam.txt";
    private String progressFile = "reader_progress.txt";

    /* ================================
       ⚙️ INTERNAL STATE
       ================================ */
    private List<String> messages = new ArrayList<>();
    private int index = 0;
    private boolean running = false;
    private CommandContext ctx;

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

        if (index >= messages.size()) {
            index = 0;
        }
    }

    /* ================================
       🚀 START
       ================================ */
    public void start(CommandContext ctx) {
        this.ctx = ctx;

        loadProgress();
        if (running) return;

        running = true;

        new Thread(() -> {
            int sent = 0;

            while (running) {
                try {
                    if (messages.isEmpty()) {
                        Thread.sleep(1000);
                        continue;
                    }

                    if (index >= messages.size()) {
                        if (loop) {
                            index = 0;
                            if (randomOrder) Collections.shuffle(messages);
                        } else {
                            break;
                        }
                    }

                    String msg = messages.get(index);

                    // ✅ Correct Zenith output
                    ctx.getSource().getEmbed().description(msg).send();

                    index++;
                    sent++;
                    saveProgress();

                    if (maxMessages != -1 && sent >= maxMessages) {
                        break;
                    }

                    int randomDelay = minDelayMs + random.nextInt(maxDelayMs - minDelayMs + 1);
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
       💾 SAVE PROGRESS
       ================================ */
    private void saveProgress() {
        try {
            String data = index + "," + System.currentTimeMillis();
            Files.writeString(Paths.get(progressFile), data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadProgress() {
        try {
            Path path = Paths.get(progressFile);

            if (!Files.exists(path)) return;

            String data = Files.readString(path);
            String[] parts = data.split(",");

            index = Integer.parseInt(parts[0]);

            System.out.println("[ReaderPlugin] Resuming at index " + index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================================
       🛑 STOP
       ================================ */
    public void stop() {
        running = false;
    }
}
