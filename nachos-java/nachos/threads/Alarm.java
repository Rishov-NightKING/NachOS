package nachos.threads;

import nachos.machine.*;

import javax.crypto.Mac;
import java.util.PriorityQueue;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    PriorityQueue<ThreadSleeptimePair> sleepQueue;

    public Alarm() {
        sleepQueue = new PriorityQueue<>();

        Machine.timer().setInterruptHandler(new Runnable() {
            public void run() {
                timerInterrupt();
            }
        });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
        long currentTime = Machine.timer().getTime();

        boolean intStatus = Machine.interrupt().disable();

        while (!sleepQueue.isEmpty() && currentTime > sleepQueue.peek().getWakeTime()) {
            sleepQueue.poll().getThread().ready();
        }

        Machine.interrupt().restore(intStatus);

        KThread.yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param x the minimum number of clock ticks to wait.
     * @see nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
        boolean intStatus = Machine.interrupt().disable();

        long wakeTime = Machine.timer().getTime() + x;
        sleepQueue.add(new ThreadSleeptimePair(KThread.currentThread(), wakeTime));
        KThread.sleep();

        Machine.interrupt().restore(intStatus);
    }

    public static void selfTest() {
        System.out.println("Entering Alarm Test");

        new KThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(KThread.currentThread() + " is going to wait...");
                ThreadedKernel.alarm.waitUntil(4000);
                System.out.println(KThread.currentThread() + " is finishing");
            }
        }).setName("thread1").fork();

        new KThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(KThread.currentThread() + " is going to wait...");
                ThreadedKernel.alarm.waitUntil(2000);
                System.out.println(KThread.currentThread() + " is finishing");
            }
        }).setName("thread2").fork();

        new KThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(KThread.currentThread() + " is going to wait...");
                ThreadedKernel.alarm.waitUntil(1000);
                System.out.println(KThread.currentThread() + " is finishing");
            }
        }).setName("thread3").fork();

        System.out.println(KThread.currentThread() + " is yielding...");
        KThread.yield();
        int count = 1;
        while (!ThreadedKernel.alarm.sleepQueue.isEmpty()) {
            KThread.yield();
            count++;
        }

        System.out.println("Yield count of main thread " +  count);
        System.out.println("Elapsed ticks " + Machine.timer().getTime());
        System.out.println("Alarm Test Finished");
        System.out.println();
    }
}

class ThreadSleeptimePair implements Comparable<ThreadSleeptimePair> {
    private KThread thread;
    private long wakeTime;

    public ThreadSleeptimePair(KThread thread, long wakeTime) {
        this.thread = thread;
        this.wakeTime = wakeTime;
    }

    public KThread getThread() {
        return thread;
    }

    public long getWakeTime() {
        return wakeTime;
    }

    @Override
    public int compareTo(ThreadSleeptimePair pair) {
        Long thisTime = this.wakeTime;
        Long argTime = pair.getWakeTime();

        return thisTime.compareTo(argTime);
    }
}
