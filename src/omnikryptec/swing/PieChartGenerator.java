package omnikryptec.swing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import omnikryptec.logger.Commands;
import omnikryptec.logger.Logger;
import omnikryptec.util.AdvancedFile;
import omnikryptec.util.Color;

/**
 * PieChartGenerator
 * @author Panzer1119
 */
public class PieChartGenerator {
    
    private static final Random random = new Random();
    
    public static final void main(String[] args) {
        final ArrayList<ChartData> chartDatas = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            chartDatas.add(new ChartData("Test " + i, (float) (Math.random() * 10.0F)));
        }
        final BufferedImage image = createPieChart(chartDatas, 1600, 1600, 0.9F, 0.65F, true, "%s %.2f");
        try {
            final AdvancedFile file = new AdvancedFile("test.png").getAbsoluteAdvancedFile();
            file.createFile();
            ImageIO.write(image, "png", file.createOutputstream(false));
            Logger.log("Saved file: " + file);
            Commands.COMMANDEXIT.run("-java");
        } catch (Exception ex) {
            Logger.logErr("Error while writing Image to File: " + ex, ex);
        }
    }
    
    /**
     * Creates a PieChart based on the given ChartDatas
     * @param chartDatas ArrayList ChartData Data
     * @param width Integer Width of the Image
     * @param height Integer Height of the Image
     * @param diameterFactor Float Quotient of (diameter / (Math.min(width, height))) (1.0F = (diameter == Math.min(width, height)))
     * @param fontSizeFactor Float Factor for the Fonts size (1.0F = normal size)
     * @param withPercentage Boolean If the percentage of each ChartData should be shown beside the name
     * @return BufferedImage Created PieChart
     */
    public static final BufferedImage createPieChart(ArrayList<ChartData> chartDatas, int width, int height, float diameterFactor, float fontSizeFactor, boolean withPercentage, String format) {
        return createPieChart(chartDatas.toArray(new ChartData[chartDatas.size()]), width, height, diameterFactor, fontSizeFactor, withPercentage, format);
    }
    
    /**
     * Creates a PieChart based on the given ChartDatas
     * @param chartDatas ChartData Array Data
     * @param width Integer Width of the Image
     * @param height Integer Height of the Image
     * @param diameterFactor Float Quotient of (diameter / (Math.min(width, height))) (1.0F = (diameter == Math.min(width, height)))
     * @param fontSizeFactor Float Factor for the Fonts size (1.0F = normal size)
     * @param withPercentage Boolean If the percentage of each ChartData should be shown beside the name
     * @return BufferedImage Created PieChart
     */
    public static final BufferedImage createPieChart(ChartData[] chartDatas, int width, int height, float diameterFactor, float fontSizeFactor, boolean withPercentage, String format) {
        if(chartDatas.length == 0 || width <= 0 || height <= 0) {
            return null;
        }
        final int width_half = width / 2;
        final int height_half = height / 2;
        final int radius = (int) (Math.min(width, height) / 2.0F * diameterFactor);
        final int diameter = radius * 2;
        final int offset = ((Math.min(width, height)) - diameter) / 2;
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics graphics = image.createGraphics();
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        float max_data = 0.0F;
        for(ChartData chartData : chartDatas) {
            if(chartData.getColor() == null) {
                chartData.setColor(generateRandomColor());
            }
            max_data += Math.max(chartData.getValue(), 0.0F);
        }
        for(ChartData chartData : chartDatas) {
            chartData.setPercentage(Math.max(chartData.getValue(), 0) / max_data);
        }
        int last_startAngle = 0;
        boolean floored = false;
        for(int i = 0; i < chartDatas.length; i++) {
            final ChartData chartData = chartDatas[i];
            double angle = 360.0 * chartData.getPercentage();
            float startAngle = last_startAngle;
            if(i == chartDatas.length - 1) {
                startAngle += 0.5;
                angle = 360.0F - last_startAngle + 0.5F;
            } else {
                if(floored) {
                    angle += 0.5;
                    startAngle += 0.5;
                }
            }
            angle = (float) Math.floor(angle);
            startAngle = (float) Math.floor(startAngle);
            final int angle_int = (int) angle;
            final int startAngle_int = (int) startAngle;
            floored = !floored;
            graphics.setColor(chartData.getColor().getAWTColor());
            graphics.fillArc(offset, offset, diameter, diameter, startAngle_int, angle_int);
            last_startAngle += angle_int;
        }
        last_startAngle = 0;
        for(int i = 0; i < chartDatas.length; i++) {
            final ChartData chartData = chartDatas[i];
            double angle = 360.0 * chartData.getPercentage();
            float startAngle = last_startAngle;
            if(i == chartDatas.length - 1) {
                startAngle += 0.5;
                angle = 360.0F - last_startAngle + 0.5F;
            } else {
                if(floored) {
                    angle += 0.5;
                    startAngle += 0.5;
                }
            }
            angle = (float) Math.floor(angle);
            startAngle = (float) Math.floor(startAngle);
            final float angle_half_radians = (float) Math.toRadians(startAngle + angle / 2.0 - 90);
            floored = !floored;
            int x_middle_text = (int) (width_half - (Math.sin(angle_half_radians) * radius * 0.9F));
            int y_middle_text = (int) (height_half - (Math.cos(angle_half_radians) * radius * 0.9F));
            final Color color = chartData.getColor();
            if(color.getVector4f().lengthSquared() < 1.2F) {
                graphics.setColor(java.awt.Color.WHITE);
            } else {
                graphics.setColor(java.awt.Color.BLACK);
            }
            final String name = String.format("%s%s", String.format(format, chartData.getName(), chartData.getValue()), (withPercentage ? String.format(" (%.2f%%)", (chartData.getPercentage() * 100.0F)) : ""));
            final Font font = new Font("TimesRoman", Font.PLAIN, (int) (300 * Math.pow(chartData.getPercentage(), 0.25F) / Math.pow(name.length(), 0.25F) * fontSizeFactor));
            graphics.setFont(font);
            final int name_width = graphics.getFontMetrics().stringWidth(name);
            final int name_height = graphics.getFontMetrics().getAscent() - graphics.getFontMetrics().getDescent();
            if((x_middle_text - (name_width / 2)) < 0) {
                x_middle_text += ((name_width / 2) - x_middle_text);
            } else if((x_middle_text + (name_width / 2)) > width) {
                x_middle_text -= (x_middle_text + (name_width / 2) - width);
            }
            if((y_middle_text - name_height) < 0) {
                y_middle_text += ((name_height) - y_middle_text);
            } else if((y_middle_text + name_height) > height) {
                y_middle_text -= (y_middle_text + name_height - height);
            }
            graphics.fillRect(x_middle_text - (name_width / 2), y_middle_text - name_height - 5, name_width, name_height + 15);
            graphics.setColor(color.getAWTColor());
            graphics.drawString(name, x_middle_text - (name_width / 2), y_middle_text);
            last_startAngle += (int) angle;
        }
        return image;
    }
    
    public static final Color generateRandomColor() {
        return new Color(random.nextInt(256) / 255.0F, random.nextInt(256) / 255.0F, random.nextInt(256) / 255.0F);
    }
    
}
