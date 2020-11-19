package nachos.threads;

public class Condition2Test {

    /**
     * This test will test the condition variable by creating 2 threads that will sleep
     * on the same condition variable and will be woken up by the main thread by yielding
     * the main thread
     */
    public static void selfTest1(){

        // we take a single lock variable for the condition variable
        final Lock conditionLock = new Lock();

        // we declare a single condition variable to work with
        final Condition2 conditionVariable = new Condition2(conditionLock);

        System.out.println("Condition2 Test Started ");

        // we create the first thread to work with
        new KThread(
                () -> {
                    conditionLock.acquire();
                    System.out.println("THREAD 1 GOING TO SLEEP");
                    conditionVariable.sleep();
                    System.out.println("THREAD 1 WAKING UP!!");
                    conditionLock.release();
                }
        ).fork();

         new KThread(
                 () -> {
                     conditionLock.acquire();
                     System.out.println("THREAD 2 GOING TO SLEEP");
                     conditionVariable.sleep();
                     System.out.println("THREAD 2 WAKING UP!!");
                     conditionLock.release();
                 }
         ).fork();


        // we yield the main thread so that the other thread can start working
        System.out.println("YIELDING THE MAIN THREAD TO RUN OTHER THREADS!!");
        KThread.yield();

        // we try to wake up all the thread that's been sleeping on the same condition variable
        System.out.println("WAKING UP THREADS 1,2 BY MAIN");
        conditionLock.acquire();
        conditionVariable.wakeAll();
        conditionLock.release();

        new KThread(
                () -> {
                    conditionLock.acquire();
                    System.out.println("THREAD 3 GOING TO SLEEP");
                    conditionVariable.sleep();
                    System.out.println("THREAD 3 WAKING UP!!");
                    conditionLock.release();
                }
        ).fork();

        new KThread(
                () -> {
                    conditionLock.acquire();
                    System.out.println("THREAD 4 GOING TO SLEEP");
                    conditionVariable.sleep();
                    System.out.println("THREAD 4 WAKING UP!!");
                    System.out.println("THREADS 3,4 THREADS ARE WOKEN UP!");
                    conditionLock.release();
                }
        ).fork();


        new KThread(
                () -> {
                    conditionLock.acquire();
                    System.out.println("THREAD 5 WAKING UP THREADS 3, 4");
                    conditionVariable.wakeAll();
                    conditionLock.release();
                }
        ).fork();
    }

}
