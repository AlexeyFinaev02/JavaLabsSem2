package lab1;

public class SuperVisor extends Thread {
    private final AbstractProgram program;

    public SuperVisor(AbstractProgram program) {
        this.program = program;
    }

    @Override
    public void run() {
        ProgramState fState = program.getProgramState();
        System.out.println("Supervisor: detected state: " + fState);
        while(true) {
            synchronized (program.getLock()) {
                try {
                    program.getLock().wait();
                    ProgramState state = program.getProgramState();
                    System.out.println("Supervisor: detected state: " + state);
                    switch(state) {
                        case UNKNOWN:
                            System.out.print("\n");
                            break;
                        case STOPPING:
                            System.out.println("Restarting program...");
                            startProgram();
                        case RUNNING:
                            System.out.println("Supervisor: working...");
                            break;
                        case FATAL_ERROR:
                            stopProgram();
                            return;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void stopProgram() {
        program.stopProgram();
    }

    public void startProgram() {
        program.startWork();
    }
}
