package cz.sparko.boxitory.test.e2e;

public class ConcurrentTester {
    private final Thread thread;
    private volatile Error error;
    private volatile RuntimeException runtimeException;

    public ConcurrentTester(Runnable runnable) {
        this.thread = new Thread(() -> {
            try {
                runnable.run();
            } catch (Error e) {
                error = e;
            } catch (RuntimeException re) {
                runtimeException = re;
            }
        });
    }

    public void start() {
        this.thread.start();
    }

    public void test() {
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (error != null) {
            throw error;
        }
        if (runtimeException != null) {
            throw runtimeException;
        }
    }
}
