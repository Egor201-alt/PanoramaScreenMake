package me.egor201.panorama_screenmake;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.io.File;

public class PanoramaCaptureTask {

    private enum Phase { SETUP, CAPTURE, FINISH }

    private Phase phase = Phase.SETUP;
    private int face = 0;
    private int wait = 0;

    private final File directory;
    private final int resolution;
    private final int warmupTicks;
    private final int faceDelayTicks;

    private Integer oldFov;
    private float oldYaw;
    private float oldPitch;
    private boolean oldHideGui;
    private int oldWidth;
    private int oldHeight;

    public PanoramaCaptureTask(File dir, int res, int warmupTicks, int faceDelayTicks) {
        this.directory = dir;
        this.resolution = res;
        this.warmupTicks = Math.max(0, warmupTicks);
        this.faceDelayTicks = Math.max(1, faceDelayTicks);
    }

    public boolean tick(MinecraftClient client) {
        if (phase == Phase.SETUP) {
            oldFov = client.options.getFov().getValue();
            oldYaw = client.player.getYaw();
            oldPitch = client.player.getPitch();
            oldHideGui = client.options.hudHidden;

            Window window = client.getWindow();
            oldWidth = window.getFramebufferWidth();
            oldHeight = window.getFramebufferHeight();

            client.options.getFov().setValue(90);
            client.options.hudHidden = true;

            PanoramaCraft.isCapturingPanorama = true;
            PanoramaCraft.captureResolution = resolution;

            client.getFramebuffer().resize(resolution, resolution);

            setAngle(client, 0);
            wait = Math.max(1, warmupTicks);
            phase = Phase.CAPTURE;
            return false;
        }

        if (client.player == null || client.world == null) {
            restore(client);
            return true;
        }

        if (client.currentScreen != null) {
            restore(client);
            client.player.sendMessage(Text.literal("Panorama cancelled (Menu opened)").formatted(Formatting.RED), true);
            return true;
        }

        if (face < 6) {
            setAngle(client, face);
        }

        if (--wait > 0) {
            return false;
        }

        if (phase == Phase.CAPTURE) {
            File file = new File(directory, "panorama_" + face + ".png");

            ScreenshotRecorder.takeScreenshot(client.getFramebuffer(), (NativeImage image) -> {
                Util.getIoWorkerExecutor().execute(() -> {
                    try {
                        image.writeTo(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                        MinecraftClient.getInstance().execute(() -> {
                            MinecraftClient.getInstance().inGameHud.setTitle(Text.literal("Error!").formatted(Formatting.RED));
                            MinecraftClient.getInstance().inGameHud.setTitleTicks(10, 40, 20);
                        });
                    } finally {
                        image.close();
                    }
                });
            });

            face++;
            if (face < 6) {
                setAngle(client, face);
                wait = faceDelayTicks;
            } else {
                phase = Phase.FINISH;
                wait = 1;
            }
            return false;
        }

        restore(client);

        Text link = Text.literal(directory.getName())
                .styled(style -> style
                        .withClickEvent(new ClickEvent.OpenFile(directory.getAbsolutePath()))
                        .withFormatting(Formatting.UNDERLINE)
                        .withFormatting(Formatting.AQUA)
                );

        client.player.sendMessage(Text.literal("Panorama successfully saved to ").append(link), false);

        client.inGameHud.setTitle(Text.literal("Panorama Saved!").formatted(Formatting.GREEN));
        client.inGameHud.setTitleTicks(10, 40, 20);

        client.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

        return true;
    }

    private void restore(MinecraftClient client) {
        PanoramaCraft.isCapturingPanorama = false;
        PanoramaCraft.captureResolution = 0;

        if (oldFov != null) {
            client.options.getFov().setValue(oldFov);
        }
        client.options.hudHidden = oldHideGui;
        client.getFramebuffer().resize(oldWidth, oldHeight);

        if (client.player != null) {
            client.player.setYaw(oldYaw);
            client.player.setPitch(oldPitch);
        }
    }

    private void setAngle(MinecraftClient client, int face) {
        if (client.player == null) return;

        float yaw = 0;
        float pitch = 0;
        switch (face) {
            case 0: yaw = 0;   pitch = 0;   break;
            case 1: yaw = 90;  pitch = 0;   break;
            case 2: yaw = 180; pitch = 0;   break;
            case 3: yaw = 270; pitch = 0;   break;
            case 4: yaw = 0;   pitch = -90; break;
            case 5: yaw = 0;   pitch = 90;  break;
        }
        client.player.setYaw(yaw);
        client.player.setPitch(pitch);
    }
}
