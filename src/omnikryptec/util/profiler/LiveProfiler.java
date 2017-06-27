package omnikryptec.util.profiler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
        new ChartData(Profiler.OVERALL_RENDERER_TIME, 0),
        new ChartData(Profiler.PARTICLE_RENDERER, 0),
        new ChartData(Profiler.PARTICLE_UPDATER, 0),
        new ChartData(Profiler.POSTPROCESSOR, 0)};
    private final HashMap<ChartData, LinkedList<Float>> data = new HashMap<>();
    private Timer timer = null;
    private int lastSeconds = 30;
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
    }
    
    public final LiveProfiler startTimer() {
        return startTimer(250);
    }
    
    public final LiveProfiler startTimer(int delay) {
        if(timer == null) {
            timer = new Timer(delay, (e) -> new Thread(() -> updateData(lastSeconds)).start());
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
            data.put(chartData, new LinkedList<>());
        }
        return this;
    }
    
    private final LiveProfiler updateData(int lastSeconds) {
        final int valuesMaxSize = (timer != null ? ((lastSeconds * 1000) / timer.getInitialDelay()) : 0);
        frame.setTitle(String.format("LiveProfiler - %s: %d µs", Profiler.OVERALL_FRAME_TIME, (Profiler.currentTimeByName(Profiler.OVERALL_FRAME_TIME) * 1000)));
        for (ChartData chartData : data.keySet()) {
            final LinkedList<Float> values = data.get(chartData);
            final float addValue = Math.max(Profiler.currentTimeByName(chartData.getName()) * 1000.0F, 0.0F);
            values.addFirst(addValue);
            while(values.size() > valuesMaxSize) {
                values.removeLast();
            }
            float completeValue = 0.0F;
            Iterator<Float> i = values.iterator();
            while(i.hasNext()) {
                completeValue += i.next();
            }
            chartData.setValue(Math.max((completeValue / values.size()), 0.0F));
        }
        return updateImage();
    }
    
    private final LiveProfiler updateImage() {
        image = PieChartGenerator.createPieChart(chartDatas, size.width, size.height, 0.9F, 0.275F, true, "%s %.2f µs");
        panel_image.revalidate();
        panel_image.repaint();
        return this;
    }

    public final int getLastSeconds() {
        return lastSeconds;
    }

    public final LiveProfiler setLastSeconds(int lastSeconds) {
        this.lastSeconds = lastSeconds;
        return this;
    }
    
    public static final void main(String[] args) {
        final LiveProfiler liveProfiler = new LiveProfiler(1000, 1000);
    }
    
}
