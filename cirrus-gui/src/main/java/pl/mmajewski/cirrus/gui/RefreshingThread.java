package pl.mmajewski.cirrus.gui;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class RefreshingThread implements Runnable {
    private Set<RefreshablePanel> refreshablePanelSet = new HashSet<>();
    private volatile int refreshRateMs = 400;

    public void register(RefreshablePanel refreshablePanel){
        synchronized (refreshablePanelSet) {
            refreshablePanelSet.add(refreshablePanel);
        }
    }

    public void unregister(RefreshablePanel refreshablePanel){
        synchronized (refreshablePanelSet) {
            refreshablePanelSet.remove(refreshablePanel);
        }
    }

    public void setRefreshRate(int refreshRateMs) {
        this.refreshRateMs = refreshRateMs;
    }

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (refreshablePanelSet) {
                    for (RefreshablePanel refreshablePanel : refreshablePanelSet) {
                        refreshablePanel.refresh();
                    }
                }
                Thread.sleep(refreshRateMs);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
