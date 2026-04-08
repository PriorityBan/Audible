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
    private int minDelayMs = 3000;         // ⏱ Minimum delay
    private int maxDelayMs = 4000;         // ⏱ Maximum delay
    private boolean randomOrder = false; // 🎲 Shuffle lines
    private boolean loop = false;        // 🔁 Loop when finished
    private int maxMessages = -1;       // 📊 Limit (-1 = infinite)
    private String fileName = "FinalSmartSpam.txt"; // 📂 Your file
    private String progressFile = "reader_progress.txt"; //checkpointing location

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
       if (index >= messages.size()) {
        index = 0;
        }
    }

    /* ================================
       🚀 START SPAMMING
       ================================ */

    public void start(CommandContext ctx) {
        loadProgress();
        if (running) return;

        running = true;
        

        new Thread(() -> {
            int sent = 0;

            while (running) {
                try {
                    if (messages.isEmpty()) {
                        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
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
                    
                    ctx.getSource().sendChatMessage(msg);
                    }

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

    //Saves Book Progress
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
