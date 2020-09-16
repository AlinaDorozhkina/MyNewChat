public class ABC {
    private static final String A = "A";
    private static final String B = "B";
    private static final String C = "C";
    private static final Object MONITOR = new Object();
    private static String NEXT = "A";


    public static void main(String[] args) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (MONITOR) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            while (!NEXT.equals(A)) {
                                MONITOR.wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.print(A);
                        NEXT = B;
                        MONITOR.notifyAll();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (MONITOR) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            while (!NEXT.equals(B)) {
                                MONITOR.wait();

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.print(B);
                        NEXT = C;
                        MONITOR.notifyAll();
                    }
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (MONITOR) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            while (!NEXT.equals(C)) {
                                MONITOR.wait();
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.print(C);
                        NEXT = A;
                        MONITOR.notifyAll();
                    }
                }
            }
        }).start();


    }
}

