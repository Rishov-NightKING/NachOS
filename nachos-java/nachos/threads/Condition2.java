package nachos.threads;

import nachos.machine.*;

import java.util.LinkedList;
import java.util.Queue;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see    nachos.threads.Condition
 */
public class Condition2 {

    private Lock conditionLock;

    /*
     *  A Queue of KThreads is created to store the threads that are sleeping. Another
     * thread will wake them up from this
     * */
    private Queue<KThread> threadWaitQueue;


    /**
     * Allocate a new condition variable.
     *
     * @param    conditionLock    the lock associated with this condition
     * variable. The current thread must hold this
     * lock whenever it uses <tt>sleep()</tt>,
     * <tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;

        // initializing the waitQueue for storing the threads that go to sleep.
        this.threadWaitQueue = new LinkedList<>();

    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {

        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        //System.out.println("SLEEPING : " + KThread.currentThread());

        /*first we disable any machine interrupt that occurs here*/
        boolean currentInterruptStatus = Machine.interrupt().disable();

        this.conditionLock.release();

        this.threadWaitQueue.add(KThread.currentThread());

        KThread.sleep(); // making the current thread sleep so that another thread on the ThreadQ can work

        this.conditionLock.acquire();

        /*we again restore the interrupt to the specific status at which it was disabled
	        after we did all the work*/
        Machine.interrupt().restore(currentInterruptStatus);
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {

        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        /* first we disable any machine interrupt that occurs here*/
        boolean currentInterruptStatus = Machine.interrupt().disable();

        /* we remove a single thread that was sleeping on this condition and wake it up
         * (set it's state to ready())*/
        if (this.threadWaitQueue.isEmpty() == false) {
            KThread wokenUpThread = this.threadWaitQueue.remove();
            wokenUpThread.ready();
            //System.out.println("WAKING : " + wokenUpThread.toString());
        }

        /*we again restore the interrupt to the specific status at which it was disabled
	        after we did all the work*/
        Machine.interrupt().restore(currentInterruptStatus);

    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        /* first we disable any machine interrupt that occurs here*/
        boolean currentInterruptStatus = Machine.interrupt().disable();

        /* we call wake() again and again until we wake up all the sleeping threads
         * and set their state to ready()*/
        while (true) {
            if (this.threadWaitQueue.isEmpty() == true) break;
            wake();
        }

        /*we again restore the interrupt to the specific status at which it was disabled
	        after we did all the work*/
        Machine.interrupt().restore(currentInterruptStatus);

    }

}
