package omnikryptec.util.profiler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import omnikryptec.logger.Logger;
import omnikryptec.swing.ChartData;
import omnikryptec.swing.PieChartGenerator;
import omnikryptec.util.Color;

/**
 * LiveProfiler
 * @author Panzer1119
 */
public class LiveProfiler {
    
    private BufferedImage image = null;
    private final JFrame frame = new JFrame("LiveProfiler");
    private final JPanel panel_image = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }
        
    };
    private final ArrayList<Color> colors = new ArrayList<>();
    private final ArrayList<ChartData> chartDatas = new ArrayList<>();
    private Timer timer = null;
    
    public LiveProfiler() {
        frame.setLayout(new BorderLayout());
        final Dimension size = new Dimension(1000, 1000);
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel_image, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        updateData();
    }
    
    public final LiveProfiler startTimer() {
        return startTimer(250);
    }
    
    public final LiveProfiler startTimer(int delay) {
        if(timer == null) {
            timer = new Timer(delay, (e) -> updateData());
        }
        timer.start();
        return this;
    }
    
    public final LiveProfiler stopTimer() {
        if(timer != null) {
            timer.stop();
            timer = null;
        }
        return this;
    }
    
    private final LiveProfiler updateData() {
        while(colors.size() < chartDatas.size()) {
            colors.add(PieChartGenerator.generateRandomColor());
        }
        chartDatas.clear();
        //chartDatas.add(new ChartData(Profiler.OVERALL_FRAME_TIME, Profiler.currentTimeByName(Profiler.OVERALL_FRAME_TIME)).setColor((colors.size() > 0 ? colors.get(0) : null)));
        chartDatas.add(new ChartData(Profiler.OVERALL_RENDERER_TIME, Profiler.currentTimeByName(Profiler.OVERALL_RENDERER_TIME)).setColor((colors.size() > 1 ? colors.get(1) : null)));
        chartDatas.add(new ChartData(Profiler.PARTICLE_RENDERER, Profiler.currentTimeByName(Profiler.PARTICLE_RENDERER)).setColor((colors.size() > 2 ? colors.get(2) : null)));
        chartDatas.add(new ChartData(Profiler.PARTICLE_UPDATER, Profiler.currentTimeByName(Profiler.PARTICLE_UPDATER)).setColor((colors.size() > 3 ? colors.get(3) : null)));
        chartDatas.add(new ChartData(Profiler.POSTPROCESSOR, Profiler.currentTimeByName(Profiler.POSTPROCESSOR)).setColor((colors.size() > 4 ? colors.get(4) : null)));
        return updateImage();
    }
    
    private final LiveProfiler updateImage() {
        image = PieChartGenerator.createPieChart(chartDatas, 800, 800, 0.9F, 0.2F, true);
        panel_image.revalidate();
        panel_image.repaint();
        return this;
    }
    
    public static final void main(String[] args) {
        final LiveProfiler liveProfiler = new LiveProfiler();
    }
    
}
