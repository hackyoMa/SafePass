package cn.spicybar.safepass.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * 获取随机验证码
 *
 * @author hackyo
 * Created on 2017/12/21 10:54.
 */
public final class GetVerifyCode {

    private static Random random = new Random();

    /**
     * 生成4位验证码字符串
     *
     * @return 验证码
     */
    public static String getVerifyCodeNum() {
        String randomStr = "378acdefhjkmnprstwxyzBCEFGHJKLMNPQRTWXY";
        StringBuilder verifyCode = new StringBuilder();
        IntStream.range(0, 4).forEach(i -> verifyCode.append(randomStr.charAt(random.nextInt(randomStr.length()))));
        return verifyCode.toString();
    }

    /**
     * 通过字符串生成验证码图片
     *
     * @param verifyCode 验证码字符串
     * @return Base64图片
     */
    public static String getVerifyCodeImg(String verifyCode) {
        //设置图像画布
        int width = 228;
        int height = 76;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //设置背景色
        Color backgroundColor = getRandomColor();
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, width, height);

        //绘制干扰线，设置干扰线颜色、数量
        g2.setColor(getRandomColor());
        int lineCount = 20;
        for (int i = 0; i < lineCount; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }

        //添加噪点，设置噪声率
        int area = (int) (0.05f * width * height);
        for (int i = 0; i < area; i++) {
            image.setRGB(random.nextInt(width), random.nextInt(height), getRandomColor().getRGB());
        }

        //扭曲图片
        shearX(g2, width, height, backgroundColor);
        shearY(g2, width, height, backgroundColor);

        //设置字体
        g2.setColor(getRandomColor());
        int fontSize = height - 4;
        Font font = new Font("Helvetica", Font.PLAIN, fontSize);
        g2.setFont(font);
        for (int i = 0; i < verifyCode.length(); i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * random.nextDouble() * (random.nextBoolean() ? 1 : -1), ((double)width / (double)verifyCode.length()) * i + (double)fontSize / 2, (double)height / 2);
            g2.setTransform(affine);
            g2.drawChars(verifyCode.toCharArray(), i, 1, ((width - 10) / verifyCode.length()) * i + 5, height / 2 + fontSize / 2 - 10);
        }

        //输出
        g2.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 生成随机颜色
     *
     * @return 颜色
     */
    private static Color getRandomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 扭曲X轴
     *
     * @param g 画布
     * @param w 宽度
     * @param h 高度
     * @param c 颜色
     */
    private static void shearX(Graphics g, int w, int h, Color c) {
        int period = random.nextInt(2);
        int frames = 1;
        int phase = random.nextInt(2);
        for (int i = 0; i < h; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w, 1, (int) d, 0);
            g.setColor(c);
            g.drawLine((int) d, i, 0, i);
            g.drawLine((int) d + w, i, w, i);
        }
    }

    /**
     * 扭曲Y轴
     *
     * @param g 画布
     * @param w 宽度
     * @param h 高度
     * @param c 颜色
     */
    private static void shearY(Graphics g, int w, int h, Color c) {
        int period = random.nextInt(40) + 10;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w; i++) {
            double d = (double) (period >> 1) * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h, 0, (int) d);
            g.setColor(c);
            g.drawLine(i, (int) d, i, 0);
            g.drawLine(i, (int) d + h, i, h);
        }
    }

}
