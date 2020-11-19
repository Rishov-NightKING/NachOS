package nachos.threads;

import java.util.Random;

public class CommunicatorTest {

    private static class Speaker implements Runnable{

        private Communicator communicatorObject;
        int message;

        public Speaker(Communicator communicatorObject){
            this.communicatorObject  = communicatorObject;
            message = new Random().nextInt(101);
        }

        @Override
        public void run() {
            this.communicatorObject.speak(message);
        }
    }

    private static class Listener implements Runnable{

        private Communicator communicatorObject;


        public Listener(Communicator communicatorObject) {
            this.communicatorObject = communicatorObject;
        }

        @Override
        public void run() {
            this.communicatorObject.listen();
        }
    }

    public static void selfTest1() {
        System.out.println("Communicator Test Started");

        Communicator commObj = new Communicator();
        new KThread(new Speaker(commObj)).fork();
        new KThread(new Listener(commObj)).fork();
        new KThread(new Listener(commObj)).fork();
        new KThread(new Speaker(commObj)).fork();
        new KThread(new Listener(commObj)).fork();
        new KThread(new Listener(commObj)).fork();
        new KThread(new Speaker(commObj)).fork();
        new KThread(new Speaker(commObj)).fork();
        new KThread(new Speaker(commObj)).fork();
        new KThread(new Listener(commObj)).fork();
    }


    public static void selfTest2() {

        Communicator commObj = new Communicator();

        for (int i = 0; i < 2; i++) {
            new KThread(new Listener(commObj)).fork();
        }

        for (int i = 0; i < 2; i++) {
            new KThread(new Speaker(commObj)).fork();
        }

    }
}
