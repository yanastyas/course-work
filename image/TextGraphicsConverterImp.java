package ru.netology.graphics.image;

import ru.netology.graphics.image.BadImageSizeException;
import ru.netology.graphics.image.TextColorSchema;
import ru.netology.graphics.image.TextGraphicsConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class TextGraphicsConverterImp implements TextGraphicsConverter {
    private int width = 0;
    private int height = 0;
    private TextColorSchema schema;
    private double maxRatio = 0.0;

    @Override
    public void setMaxHeight(int height) {
        if (height > 0) {
            this.height = height;
        }
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void setMaxWidth(int width) {
        if (width > 0) {
            this.width = width;
        }
    }

    public int getWidth() {
        return width;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        if (maxRatio > 0.0) {
            this.maxRatio = maxRatio;
        }
    }

    public double getMaxRatio() {
        return maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }

    public TextGraphicsConverterImp() {

    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));

        double ratioImgWidth = 0;
        double ratioImgHeight = 0;
        if (getMaxRatio() != 0) {
            ratioImgWidth = ((double) img.getWidth() / img.getHeight());
            ratioImgHeight = ((double) img.getHeight() / img.getWidth());
        }
        if (ratioImgWidth > getMaxRatio()) {
            throw new BadImageSizeException(getMaxRatio(), ratioImgWidth);
        }
        if (ratioImgHeight > getMaxRatio()) {
            throw new BadImageSizeException(getMaxRatio(), ratioImgHeight);
        } else {
            System.out.println("Изображение подходит под размер");
        }

        //Если выставили макс допустимую ширину и/или высоту
        int newWidth = 0;
        int newHeight = 0;
        if (getWidth() != 0 || getHeight() != 0) {
            if (img.getHeight() > getHeight() || img.getWidth() > getWidth()) {
                int newKof = Math.max(((int) Math.ceil(img.getHeight() / height)), ((int) Math.ceil(img.getWidth() / width)));
                newWidth = img.getWidth() / newKof;
                newHeight = img.getHeight() / newKof;
            } else {
                newWidth = img.getWidth();
                newHeight = img.getHeight();
            }
        }
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);

        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);

        ImageIO.write(img, "png", new File("out.png")); // промежуточное сохранение картинки в файл через:
        WritableRaster raster = bwImg.getRaster();

        StringBuilder str = new StringBuilder();
        for (int h = 0; h < newHeight; h++) {
            for (int w = 0; w < newWidth; w++) {
                int color = raster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                str.append(c);
                str.append(c);
            }
            str.append("\n");
        }
        return str.toString();
    }
}

