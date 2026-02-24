package me.egor201.panorama_screenmake;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.File;

public class PanoramaCaptureTask {
    private int frame = 0;
    private final File directory;
    private final int resolution;

    private Integer oldFov;
    private float oldYaw;
    private float oldPitch;
    private boolean oldHideGui;
    private int oldWidth;
    private int oldHeight;

    public PanoramaCaptureTask(File dir, int res) {
        this.directory = dir;
        this.resolution = res;
    }

    public boolean tick(MinecraftClient client) {
        if (frame == 0) {
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

            setAngle(0);
            frame++;
            return false;
        }

        if (frame >= 1 && frame <= 6) {
            int faceIndex = frame - 1;
            File file = new File(directory, "panorama_" + faceIndex + ".png");

            ScreenshotRecorder.takeScreenshot(client.getFramebuffer(), (NativeImage image) -> {
                Util.getIoWorkerExecutor().execute(() -> {
                    try {
                        image.writeTo(file);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        image.close();
                    }
                });
            });

            if (frame < 6) {
                setAngle(frame);
            }
            frame++;
            return false;
        }

        if (frame > 6) {
            PanoramaCraft.isCapturingPanorama = false;
            client.options.getFov().setValue(oldFov);
            client.player.setYaw(oldYaw);
            client.player.setPitch(oldPitch);
            client.options.hudHidden = oldHideGui;

            PanoramaCraft.captureResolution = 0;
            client.getFramebuffer().resize(oldWidth, oldHeight);

            client.player.sendMessage(Text.literal("Panorama successfully saved to " + directory.getName() + " in " + resolution + "x" + resolution + "!"), false);
            return true;
        }

        return false;
    }

    private void setAngle(int face) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        
        float yaw = 0;
        float pitch = 0;
        switch(face) {
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
