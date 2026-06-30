package lab1;

public class AbstractProgram extends Thread{
    private boolean running;
    private ProgramState state;
    private final Object lock = new Object(){};

    public ProgramState getProgramState(){
        synchronized (lock) {
            return state;
        }
    }

    public void setProgramState(ProgramState state) {
        synchronized (lock) {
            this.state = state;
            lock.notifyAll();
        }
    }

    public void startWork() {
        setProgramState(ProgramState.RUNNING);
    }

    public void stopProgram() {
        synchronized (lock) {
            this.running = false;
            lock.notifyAll();
        }
    }

    @Override
    public void run() {
        synchronized (lock) {
            setProgramState(ProgramState.UNKNOWN);
            this.running = true;
        }
        while(running) {
            synchronized (lock) {
                while(this.state == ProgramState.RUNNING) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }

                if (this.state == ProgramState.STOPPING) {
                    while (this.state == ProgramState.STOPPING)
                    {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("Program end.");
    }

    public Object getLock() {
        return this.lock;
    }

    public void startDaemon() {
        Thread daemon = new Thread(() -> {
            while(true) {
                synchronized (lock) {
                    try {
                        Thread.sleep(2000);

                        while (state == ProgramState.STOPPING) {
                            lock.wait();
                        }

                        ProgramState[] values = {
                                ProgramState.RUNNING,
                                ProgramState.STOPPING,
                                ProgramState.FATAL_ERROR
                        };

                        ProgramState randomState = values[(int) (Math.random() * values.length)];
                        this.setProgramState(randomState);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();
    }
}
