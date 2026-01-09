package main;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class AssetManager {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();

    public static BufferedImage getImage(String path) {
        if (!imageCache.containsKey(path)) {
            try {
                var resourceUrl = AssetManager.class.getResource(path);
                if (resourceUrl != null) {
                    BufferedImage img = ImageIO.read(resourceUrl);
                    imageCache.put(path, img);
                } else {
                    return null;
                }
            } catch (Exception e) {
                System.err.println("Could not load image: " + path);
                return null;
            }
        }
        return imageCache.get(path);
    }
}
