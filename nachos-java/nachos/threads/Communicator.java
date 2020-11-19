package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {

    private Condition2 speaker;
    private Condition2 listener;
    private Lock conditionLock;
    private int message;
    private int controlFlag;

    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        this.conditionLock = new Lock();
        this.speaker = new Condition2(this.conditionLock);
        this.listener = new Condition2(this.conditionLock);
        this.controlFlag = 0;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {

        this.conditionLock.acquire();

        /*
         * First we check to see if anyone has spoken or not, if someone has spoken
         * but the word has not been yet listened to, we wake up all the listener threads
         * and then force the current speaker thread to go to sleep
         *  */
        for(;;) {

            if  (this.controlFlag == 0) break;

            // then we go to sleep until the word has been completely listened in the listener
            this.speaker.sleep();
        }

        // we come here if no one has spoken currently\
        System.out.println("spoke: " + word);
        // we transfer the word
        this.message = word;

        /* We increase the flag, since speaker thread has spoken so other speakers will
         *  go to sleep until the word is listened to by a single listener
         */
        this.controlFlag = this.controlFlag + 1;

        // we wake up a listener if they were sleeping to listen to the word
        this.listener.wakeAll();

        // since we are done, we can go to sleep safely
        this.speaker.sleep();

        this.conditionLock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */
    public int listen() {

        this.conditionLock.acquire();

        // the integer that will be received
        int receivedMessage = 0;

        /*
         * First we check if there is any speaker that has already spoken or not,
         * Now, if the speaker has not spoken yet and the listener came here first,
         * then we need to wait until a speaker thread has spoken a word. until then the
         * listener will sleep
         * */
        for (;;) {

            if (this.controlFlag == 1) break;

            this.listener.sleep();
        }

        /* We come here since some speaker thread has spoken something, and so we
         *  receive the message first
         *  */
        receivedMessage = this.message;
        System.out.println("received: " + receivedMessage);

        /*
         * Next we wake up any speaker thread that has been sleeping for
         * this session of message transfer to end
         * */
        this.speaker.wakeAll();

        this.controlFlag = this.controlFlag - 1;

        this.conditionLock.release();

        return receivedMessage;
    }

}
