package com.roncoo.generator.cli;

/**
 * 进度指示器
 * 
 * @author roncoo-generator
 */
public class ProgressIndicator {
    
    private volatile boolean running = false;
    private Thread progressThread;
    
    /**
     * 开始显示进度
     */
    public void start() {
        if (running) {
            return;
        }
        
        running = true;
        progressThread = new Thread(this::showProgress);
        progressThread.setDaemon(true);
        progressThread.start();
    }
    
    /**
     * 停止显示进度
     */
    public void stop() {
        running = false;
        if (progressThread != null) {
            try {
                progressThread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        // 清除进度显示
        System.out.print("\r                                        \r");
    }
    
    /**
     * 显示进度动画
     */
    private void showProgress() {
        String[] spinner = {"|", "/", "-", "\\"};
        int index = 0;
        
        while (running) {
            System.out.printf("\r正在生成代码... %s", spinner[index]);
            index = (index + 1) % spinner.length;
            
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    /**
     * 显示带进度百分比的进度条
     * 
     * @param current 当前进度
     * @param total 总进度
     * @param message 进度消息
     */
    public static void showProgress(int current, int total, String message) {
        int barLength = 30;
        int progress = (int) ((double) current / total * barLength);
        double percentage = (double) current / total * 100;
        
        StringBuilder bar = new StringBuilder();
        bar.append("[");
        
        for (int i = 0; i < barLength; i++) {
            if (i < progress) {
                bar.append("=");
            } else if (i == progress) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        
        bar.append("]");
        
        System.out.printf("\r%s %s %.1f%% (%d/%d) %s", 
                bar.toString(), 
                String.format("%6.1f%%", percentage),
                percentage,
                current, 
                total,
                message != null ? message : "");
    }
    
    /**
     * 完成进度显示
     */
    public static void complete() {
        System.out.println();
    }
}