package dev.zenith.module;

import com.github.rfresh2.EventConsumer;
import com.zenith.event.client.ClientTickEvent;
import com.zenith.module.api.Module;
import com.zenith.util.ChatUtil;
import com.zenith.util.timer.Timer;
import com.zenith.util.timer.Timers;
import org.geysermc.mcprotocollib.protocol.packet.ingame.serverbound.ServerboundChatPacket;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.rfresh2.EventConsumer.of;

public class ReaderModule extends Module {

    /* ================================
       🔧 SETTINGS
       ================================ */
    private int minDelayMs = 3000;
    private int maxDelayMs = 4000;
    private boolean randomOrder = false;
    private boolean loop = false;
    private int maxMessages = -1;
    private int delayTicks;
    private String fileName = "FinalSmartSpam.txt";
    private String progressFile = "reader_progress.txt";

    /* ================================
       ⚙️ STATE
       ================================ */
    private final Timer timer = Timers.tickTimer();
    private List<String> messages = new ArrayList<>();
    private int index = 0;
    private int sent = 0;

    /* ================================
       📂 LOAD FILE
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

            System.out.println("[ReaderModule] Loaded " + messages.size() + " lines");

        } catch (IOException e) {
            e.printStackTrace();
            messages = List.of("Error loading file");
        }

        if (index >= messages.size()) {
            index = 0;
        }
    }

    /* ================================
       🚀 ENABLE / DISABLE
       ================================ */
    @Override
    public void onEnable() {
        delayTicks = getRandomDelayTicks();
        loadProgress();
        loadFile();
        sent = 0;
        timer.reset();
        System.out.println("[ReaderModule] Attempting to send: " + msg);
    }

    @Override
    public void onDisable() {
        saveProgress();
    }

    @Override
    public boolean enabledSetting() {
        return true;
    }

    /* ================================
       🔁 TICK LOOP
       ================================ */
    @Override
    public List<EventConsumer<?>> registerEvents() {
        return List.of(
            of(ClientTickEvent.class, this::onTick)
        );
    }

    private void onTick(ClientTickEvent event) {
        if (messages.isEmpty()) return;

        if (!timer.tick(delayTicks)) return;

        timer.reset();
        delayTicks = getRandomDelayTicks();

       

        if (index >= messages.size()) {
            if (loop) {
                index = 0;
                if (randomOrder) Collections.shuffle(messages);
            } else {
                disable();
                return;
            }
        }

        String msg = messages.get(index);

        // ✅ Send real chat packet
        var packet = new ServerboundChatPacket(
            ChatUtil.sanitizeChatMessage(msg)
        );

        sendClientPacketAsync(packet);

        index++;
        sent++;
        saveProgress();

        if (maxMessages != -1 && sent >= maxMessages) {
            disable();
        }
    }

    /* ================================
       ⏱ DELAY (ms → ticks)
       ================================ */
    private int getRandomDelayTicks() {
        int minTicks = minDelayMs / 50;
        int maxTicks = maxDelayMs / 50;
        return ThreadLocalRandom.current().nextInt(minTicks, maxTicks + 1);
    }

    /* ================================
       💾 PROGRESS
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

            System.out.println("[ReaderModule] Resuming at index " + index);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
