package omnikryptec.util.profiler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
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
    private final ChartData[] chartDatas = new ChartData[] {
        new ChartData(Profiler.DISPLAY_IDLE_TIME, 0),
        new ChartData(Profiler.DISPLAY_UPDATE_TIME, 0),
        new ChartData(Profiler.SCENE_TIME, 0),
        new ChartData(Profiler.PARTICLE_RENDERER, 0),
        new ChartData(Profiler.PARTICLE_UPDATER, 0),
        new ChartData(Profiler.POSTPROCESSOR, 0)};
    private Timer timer = null;
    final Dimension size;
    
    public LiveProfiler(int width, int height) {
        this.size = new Dimension(width, height);
        frame.setLayout(new BorderLayout());
        frame.setSize(size);
        frame.setPreferredSize(size);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel_image, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        init();
        updateData();
    }
    
    public final LiveProfiler startTimer() {
        return startTimer(250);
    }
    
    public final LiveProfiler startTimer(int delay) {
        if(timer == null) {
            timer = new Timer(delay, (e) -> new Thread(() -> updateData()).start());
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
    
    private final LiveProfiler init() {
        for(ChartData chartData : chartDatas) {
            chartData.setColor(PieChartGenerator.generateRandomColor());
        }
        return this;
    }
    
    private final LiveProfiler updateData() {
        frame.setTitle(String.format("LiveProfiler - %s: %d ms", Profiler.OVERALL_FRAME_TIME, Profiler.currentTimeByName(Profiler.OVERALL_FRAME_TIME)));
        for (ChartData chartData : chartDatas) {
            chartData.setValue(Profiler.currentTimeByName(chartData.getName()));
        }
        return updateImage();
    }
    
    private final LiveProfiler updateImage() {
        image = PieChartGenerator.createPieChart(chartDatas, size.width, size.height, 0.9F, 0.2F, true, "%s %.2f ms");
        panel_image.revalidate();
        panel_image.repaint();
        return this;
    }
    
    public static final void main(String[] args) {
        final LiveProfiler liveProfiler = new LiveProfiler(1000, 1000);
    }
    
}
