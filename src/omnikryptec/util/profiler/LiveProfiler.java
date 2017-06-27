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
        chartDatas.clear();
        chartDatas.add(new ChartData(Profiler.OVERALL_FRAME_TIME, Profiler.currentTimeByName(Profiler.OVERALL_FRAME_TIME)));
        chartDatas.add(new ChartData(Profiler.OVERALL_RENDERER_TIME, Profiler.currentTimeByName(Profiler.OVERALL_RENDERER_TIME)));
        chartDatas.add(new ChartData(Profiler.PARTICLE_RENDERER, Profiler.currentTimeByName(Profiler.PARTICLE_RENDERER)));
        chartDatas.add(new ChartData(Profiler.PARTICLE_UPDATER, Profiler.currentTimeByName(Profiler.PARTICLE_UPDATER)));
        chartDatas.add(new ChartData(Profiler.POSTPROCESSOR, Profiler.currentTimeByName(Profiler.POSTPROCESSOR)));
        chartDatas.stream().forEach((chartData) -> {
            Logger.log(chartData);
        });
        Logger.log("Added ChartData");
        return updateImage();
    }
    
    private final LiveProfiler updateImage() {
        image = PieChartGenerator.createPieChart(chartDatas, 800, 800, 0.9F, 0.5F, true);
        panel_image.revalidate();
        panel_image.repaint();
        return this;
    }
    
    public static final void main(String[] args) {
        final LiveProfiler liveProfiler = new LiveProfiler();
    }
    
}
