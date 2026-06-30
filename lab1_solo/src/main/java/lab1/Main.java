package lab1;

public class Main {
    public static void main() {
        AbstractProgram program = new AbstractProgram(){};
        program.start();
        SuperVisor visor = new SuperVisor(program);
        visor.start();
        program.startDaemon();
    }
}
