package de.noisruker.threading;

import de.noisruker.event.EventManager;
import de.noisruker.threading.events.TickEvent;
import de.noisruker.threading.events.TickInterruptedEvent;
import de.noisruker.threading.events.TickerStoppedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadManager {

    public static ThreadManager instance = new ThreadManager();

    public static ThreadManager getInstance() {
        return instance;
    }

    private final ThreadPoolExecutor executor;
    private final List<ContinuosTask> continuosTasks;
    private volatile boolean stopTicking = true, stoppedTicking = false;

    private ThreadManager() {
        this.executor = new ThreadPoolExecutor(3, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));
        this.continuosTasks = new ArrayList<>();
    }

    public ThreadPoolExecutor getThreadsExecutor() {
        return this.executor;
    }

    public void executeAsync(Runnable r) {
        executor.execute(r);
    }

    public <T> Future<T> executeAsyncAndReturn(Callable<T> c) {
        return executor.submit(c);
    }

    public synchronized void executeContinuos(Runnable r, long everyTick, TimeUnit unit) {
        this.continuosTasks.add(new ContinuosTask(r, everyTick, unit));
    }

    public synchronized void startTicker() {
        if(!this.stopTicking)
            throw new IllegalStateException("Already ticking!");

        this.stopTicking = false;
        this.executeAsync(() -> {
            long timeout = 1000L / 30L;

            long buffer = 0L;

            long gameTick = 0L;

            while (!stopTicking) {
                long start = System.currentTimeMillis();

                for(ContinuosTask t: this.continuosTasks)
                    t.cycles(gameTick);

                if(!EventManager.getInstance().triggerEvent(new TickEvent(gameTick)))
                    break;

                gameTick = (gameTick + 1) % Long.MAX_VALUE;
                long toWait = (timeout - buffer) - (System.currentTimeMillis() - start);

                if (toWait < 0) buffer = -toWait;
                else {
                    try {
                        Thread.sleep(toWait);
                    } catch (InterruptedException e) {
                        if(!this.stopTicking && !EventManager.getInstance().triggerEvent(new TickInterruptedEvent(gameTick)))
                            break;
                    }
                    buffer = 0L;
                }
            }
            this.stopTicking = true;
            this.stoppedTicking = true;
            EventManager.getInstance().triggerEvent(new TickerStoppedEvent());
        });
    }

    public void stopTicker() {
        this.stopTicking = true;
        while (!stoppedTicking) {
            Thread.onSpinWait();
        }
        this.stoppedTicking = false;
    }

    private static class ContinuosTask {
        private final Runnable toExecute;
        private final long time;
        private final TimeUnit unit;

        public ContinuosTask(Runnable r, long time, TimeUnit unit) {
            this.toExecute = r;
            this.time = time;
            this.unit = unit;
        }

        public synchronized void cycles(long millis) {
            if(millis % this.unit.toMillis(this.time) == 0) {
                this.toExecute.run();
            }
        }
    }


}
