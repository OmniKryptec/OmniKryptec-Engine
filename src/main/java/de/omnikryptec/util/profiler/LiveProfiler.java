/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.util.profiler;

import de.omnikryptec.gameobject.particles.ParticleMaster;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.swing.ChartData;
import de.omnikryptec.swing.PieChartGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * LiveProfiler
 *
 * @author Panzer1119
 */
public class LiveProfiler {
    
    private BufferedImage image = null;
    private final JFrame frame = new JFrame("LiveProfiler");
    private final JPanel panel_image = new JPanel() {
        private static final long serialVersionUID = -7905030061618863267L;
        
        @Override
        protected void paintComponent(Graphics g) {
            g.drawImage(image, 0, 0, null);
        }
        
    };
    private JLabel vert_info = new JLabel();
    private final ChartData[] chartDatas = new ChartData[] {new ChartData(Profiler.DISPLAY_IDLE_TIME, 0), new ChartData(Profiler.DISPLAY_UPDATE_TIME, 0), new ChartData(Profiler.SCENE_RENDER_TIME_3D, 0), new ChartData(Profiler.SCENE_LOGIC_TIME_3D, 0), new ChartData(Profiler.SCENE_RENDER_TIME_2D, 0), new ChartData(Profiler.SCENE_LOGIC_TIME_2D, 0), new ChartData(Profiler.PARTICLE_RENDERER, 0), new ChartData(Profiler.PARTICLE_UPDATER, 0), new ChartData(Profiler.POSTPROCESSOR, 0), new ChartData(Profiler.OTHER_TIME, 0)};
    private final HashMap<ChartData, LinkedList<Double>> data = new HashMap<>();
    private float[] sqrts = null;
    private Timer timer = null;
    private int lastSeconds = 10;
    final Dimension size;
    private int maxValuesSize = 0;
    
    
    public LiveProfiler(int width, int height) {
        this.size = new Dimension(width, height);
        frame.setLayout(new BorderLayout());
        frame.add(vert_info, BorderLayout.NORTH);
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
        if (timer == null) {
            timer = new Timer(delay, (e) -> new Thread(() -> updateData()).start());
        }
        maxValuesSize = (timer != null ? ((lastSeconds * 1000) / timer.getInitialDelay()) : 0);
        sqrts = new float[maxValuesSize];
        for (int i = 1; i <= maxValuesSize; i++) {
            sqrts[i - 1] = (float) Math.sqrt(i * 1.0);
        }
        timer.start();
        return this;
    }
    
    public final LiveProfiler stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        return this;
    }
    
    private final LiveProfiler init() {
        for (ChartData chartData : chartDatas) {
            chartData.setColor(PieChartGenerator.generateRandomColor());
            data.put(chartData, new LinkedList<>());
        }
        return this;
    }
    
    private final LiveProfiler updateData() {
        OmniKryptecEngine omc = OmniKryptecEngine.instance();
        if (omc != null) {
            //TODO @Panzer1119 xD
            vert_info.setText("Frames: " + OmniKryptecEngine.instance().getDisplayManager().getFramecount() + " Particles updated: " + ParticleMaster.instance().getUpdatedParticlesCount() + " Particles rendered: " + ParticleMaster.instance().getRenderedParticlesCount());
        } else {
            vert_info.setText("Error: Instance is null");
        }
        double max = 0.0;
        for (ChartData chartData : data.keySet()) {
            final LinkedList<Double> values = data.get(chartData);
            if (values == null) {
                continue;
            }
            final double addValue = Math.max(Profiler.currentTimeByName(chartData.getName()), 0.0F);
            values.addFirst(addValue);
            while (values.size() > maxValuesSize) {
                values.removeLast();
            }
            float completeValue = 0.0F;
            final Iterator<Double> floats = values.iterator();
            while (floats.hasNext()) {
                completeValue += (floats.next());
            }
            chartData.setValue(Math.max((completeValue / values.size()), 0.0F));
            max += chartData.getValue();
        }
        //final double average = max / data.size();
        frame.setTitle(String.format("LiveProfiler - %s: %f ms - Max %s: %f ms", Profiler.OVERALL_FRAME_TIME, (Profiler.currentTimeByName(Profiler.OVERALL_FRAME_TIME)), Profiler.OVERALL_FRAME_TIME, max));
        return updateImage();
    }
    
    private final LiveProfiler updateImage() {
        image = PieChartGenerator.createPieChart(chartDatas, size.width, size.height, 0.9F, 0.275F, true, "%s %.2f ms");
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
        //final LiveProfiler liveProfiler = new LiveProfiler(1000, 1000);
        new LiveProfiler(1000, 1000);
    }
    
}