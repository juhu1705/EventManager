package de.noisruker.threading;

import de.noisruker.event.EventManager;
import de.noisruker.threading.events.TickInterruptedEvent;
import de.noisruker.threading.events.TickerStoppedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The thread manager handles the parallel running of threads and the main loop thread ticking 30 times per second.
 */
public class ThreadManager {

    /**
     * The instance of the thread manager
     */
    private static final ThreadManager instance = new ThreadManager();

    /**
     * @return The actual instance of this thread manager
     */
    public static ThreadManager getInstance() {
        return instance;
    }

    /**
     * The execution pool to handle the running threads
     */
    private final ThreadPoolExecutor executor;
    /**
     * All tasks to be updated while ticking
     */
    private final List<ContinuosTask> continuosTasks;
    /**
     * Control parameter for the ticking
     */
    private volatile boolean stopTicking = true, stoppedTicking = false;

    /**
     * Creates an instance of the thread manager and initialize the thread pool and the continuos tasks.
     */
    private ThreadManager() {
        this.executor = new ThreadPoolExecutor(3, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50));
        this.continuosTasks = new ArrayList<>();
    }

    /**
     * @return The actually used thread pool
     */
    @SuppressWarnings("unused")
    public ThreadPoolExecutor getThreadsExecutor() {
        return this.executor;
    }

    /**
     * Calls {@link ThreadPoolExecutor#execute(Runnable)} on the current thread pool
     */
    public void executeAsync(Runnable r) {
        executor.execute(r);
    }

    /**
     * Calls {@link ThreadPoolExecutor#submit(Callable)} on the current thread pool
     */
    @SuppressWarnings("unused")
    public <T> Future<T> executeAsyncAndReturn(Callable<T> c) {
        return executor.submit(c);
    }

    /**
     * Adds a task to the ticking tasks. This task will be executed at every tick it wants to. The ticker ticks 30 times a second
     * @param r The task to be executed when ticking
     * @param everyTick How often this task will be executed. 1 means every tick, when unit is set to milliseconds.
     * @param unit The unit the tick is calculated with.
     */
    @SuppressWarnings("unused")
    public synchronized void executeContinuously(Runnable r, long everyTick, TimeUnit unit) {
        this.continuosTasks.add(new ContinuosTask(r, everyTick, unit));
    }

    /**
     * Adds a task to the ticking tasks. This task will be executed at every {@code everyTick}s tick. The ticker ticks 30 times a second
     * @param r The task to be executed when ticking
     * @param everyTick How often this task will be executed.
     */
    @SuppressWarnings("unused")
    public synchronized void executeContinuously(Runnable r, long everyTick) {
        this.continuosTasks.add(new ContinuosTask(r, everyTick, TimeUnit.MILLISECONDS));
    }

    /**
     * Starts the ticker in a new thread, if it is not running.
     * @throws IllegalStateException - If the ticker is already running
     */
    @SuppressWarnings("unused")
    public synchronized void startTicker() {
        if(!this.stopTicking)
            throw new IllegalStateException("Already ticking!");

        this.stopTicking = false;
        this.executeAsync(() -> {
            long timeout = 1000L / 30L;

            long buffer = 0L;

            long tick = 0L;


            while (!stopTicking) {
                try {
                    long start = System.currentTimeMillis();

                    this.tick(tick);

                    tick = (tick + 1) % Long.MAX_VALUE;
                    long toWait = (timeout - buffer) - (System.currentTimeMillis() - start);

                    if (toWait < 0) buffer = -toWait;
                    else {

                            Thread.sleep(toWait);

                        buffer = 0L;
                    }
                } catch (Exception e) {
                    if (!this.stopTicking && !EventManager.getInstance().triggerEvent(new TickInterruptedEvent(tick, e)))
                        break;
                }
            }

            this.stopTicking = true;
            this.stoppedTicking = true;
            EventManager.getInstance().triggerEvent(new TickerStoppedEvent());

        });
    }

    /**
     * Ticks all tasks
     * @param tick The current tick
     */
    private synchronized void tick(final long tick) {
        this.continuosTasks.forEach(t -> t.tick(tick));
    }

    /**
     * Stops the ticker and wait for it to shut down.
     */
    @SuppressWarnings("unused")
    public void stopTicker() {
        this.stopTicking = true;
        while (!stoppedTicking) {
            Thread.onSpinWait();
        }
        this.stoppedTicking = false;
    }

    /**
     * A task that will be executed on every tick and calling its runnable when its time is ready
     */
    private static class ContinuosTask {
        /**
         * The runnable to call
         */
        private final Runnable toExecute;
        /**
         * When to call the runnable
         */
        private final long time;
        /**
         * Which time format the time param is following
         */
        private final TimeUnit unit;

        /**
         * Creates a new Continuously executed task
         * @param r The runnable to execute
         * @param time When to execute
         * @param unit The time unit time follows
         */
        public ContinuosTask(Runnable r, long time, TimeUnit unit) {
            this.toExecute = r;
            this.time = time;
            this.unit = unit;
        }

        /**
         * Checks if this executable is ready for ticking in this tick and ticks if it is.
         * @param millis The current ticks time.
         */
        public synchronized void tick(long millis) {
            if(millis % this.unit.toMillis(this.time) == 0) {
                this.toExecute.run();
            }
        }
    }


}
