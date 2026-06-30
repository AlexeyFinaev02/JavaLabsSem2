package org;

import service.DataService;

public class Main {

    public static void main(String[] args) {

        DataService service = new DataService();
        ConsoleUI ui = new ConsoleUI(service);

        if (args.length == 0) {
            ui.runInteractiveMode();
        } else {
            ui.runAutoMode(args);
        }
    }
}
