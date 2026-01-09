package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public final class IconFactory {
    private IconFactory() {}

    private static ImageIcon makeBadge(String text, Color fill, Color stroke, Color textColor) {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(fill);
        g.fillOval(1, 1, size-2, size-2);
        g.setColor(stroke);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(1, 1, size-2, size-2);
        g.setColor(textColor);
        Font f = new Font("Arial", Font.BOLD, 11);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int tx = (size - fm.stringWidth(text)) / 2;
        int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, tx, ty);
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon money() { return makeBadge("$", new Color(163,190,140), new Color(120,150,120), new Color(40,44,52)); }
    public static ImageIcon income() { return makeBadge("+", new Color(191,144,0), new Color(160,120,0), new Color(40,44,52)); }
    public static ImageIcon population() { return makeBadge("P", new Color(200,200,200), new Color(150,150,150), new Color(40,44,52)); }
    public static ImageIcon day() { return makeBadge("D", new Color(143,188,187), new Color(110,150,150), new Color(40,44,52)); }
    public static ImageIcon happiness() { return makeBadge(":)", new Color(191,97,106), new Color(150,60,70), Color.WHITE); }
    public static ImageIcon unemployment() { return makeBadge("U", new Color(208,135,112), new Color(170,100,85), new Color(40,44,52)); }
    public static ImageIcon score() { return makeBadge("★", new Color(180,142,173), new Color(150,110,140), Color.WHITE); }

    public static ImageIcon nextDay() { return makeBadge(">>", new Color(163,190,140), new Color(120,150,120), new Color(40,44,52)); }
    public static ImageIcon playPause() { return makeBadge("▶", new Color(143,188,187), new Color(110,150,150), new Color(40,44,52)); }
    public static ImageIcon prices() { return makeBadge("$", new Color(191,144,0), new Color(160,120,0), new Color(40,44,52)); }
}
