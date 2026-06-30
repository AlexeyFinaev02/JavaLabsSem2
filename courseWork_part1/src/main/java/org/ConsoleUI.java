package org;

import api.ApiClient;
import api.FootballApi;
import api.FrankfurterApi;
import api.WeatherApi;
import exceptions.ApiException;
import exceptions.FileProcessingException;
import format.CsvFormatter;
import format.Formatter;
import format.JsonFormatter;
import model.SaveMode;
import service.DataService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final DataService service;

    public ConsoleUI(DataService service) {
        this.service = service;
    }

    public void runInteractiveMode() {
        while (true) {
            System.out.println();
            System.out.println("Выберите действие:");
            System.out.println("1. Получить и записать данные Api");
            System.out.println("2. Вывести содержимое файла");
            System.out.println("3. Выход");

            int choice = readInt();
            switch (choice) {
                case 1:
                    ApiClient api = choiceApi();
                    if (api == null) {
                        break;
                    }
                    processApi(api);
                    break;
                case 2:
                    showFile();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Неверный пункт меню");
            }
        }
    }

    public void runAutoMode(String[] args) {
        if (args.length < 3) {
            System.out.println("Неверное количество параметров!");
            System.out.println("Api: weather, frankfurter, football");
            System.out.println("Использование:");
            System.out.println("weather result.json new");
            return;
        }
        String fileName = args[args.length - 2];
        String modeName = args[args.length - 1];
        Path path = Paths.get(fileName);
        Formatter formatter = getFormatter(path);
        SaveMode mode;
        switch (modeName.toLowerCase()) {
            case "new":
                mode = SaveMode.CREATE_NEW;
                break;
            case "append":
                mode = SaveMode.APPEND;
                break;
            default:
                System.out.println("Неизвестный режим: " + modeName);
                return;
        }
        for (int i = 0; i < args.length - 2; i++) {
            try {
                ApiClient api = parseApi(args[i]);
                service.save(api, formatter, path, mode);
                System.out.println(api.getName() + " сохранён");
                mode = SaveMode.APPEND;
            } catch (ApiException e) {
                System.out.println("Ошибка API: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Неизвестный API");
            } catch (FileProcessingException e) {
                System.out.println("Ошибка файла: " + e.getMessage());
            }
        }
    }

    private ApiClient parseApi(String apiName) {
        switch (apiName.toLowerCase()) {
            case "weather":
                return new WeatherApi();
            case "frakfurter":
                return new FrankfurterApi();
            case "football":
                return new FootballApi();
            default:
                throw new ApiException("Неизвестный API: " + apiName);
        }
    }

    private void processApi(ApiClient api) {
        SaveMode mode = choiceFileMode();
        if (mode == null) {
            return;
        }
        Path path = choiceFile();
        if (path == null) {
            return;
        }

        try {
            Formatter format = getFormatter(path);
            service.save(api, format, path, mode);
            System.out.println("Успешно сохранено.");
        }
        catch (FileProcessingException e) {
            System.out.println("Ошибка c форматированием: " + e.getMessage());
        }
        catch(ApiException e) {
            System.out.println("Ошибка Api: " + e.getMessage());
        }
    }

    private ApiClient choiceApi() {
        int choice = 0;
        while(true) {
            System.out.println("Выберите Api:");
            System.out.println("1. WeatherApi(Узнать погоду)");
            System.out.println("2. FrankfurterApi(Курс валют)");
            System.out.println("3. FootballApi(Результаты матчей)");
            System.out.println("4. Назад.");
            choice = readInt();
            switch (choice) {
                case 1:
                    return new WeatherApi();
                case 2:
                    return new FrankfurterApi();
                case 3:
                    return new FootballApi();
                case 4:
                    return null;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private Path choiceFile() {

        System.out.println();
        System.out.println("Введите имя файла:");

        String fileName = scanner.nextLine();

        if (fileName.isBlank()) {
            return null;
        }

        return Paths.get(fileName);
    }

    private SaveMode choiceFileMode() {
        int choice = 0;
        while (true) {
            System.out.println("Выберите режим:");
            System.out.println("1. Создать новый файл");
            System.out.println("2. Дозаписать в существующий");
            System.out.println("3. Назад.");
            choice = readInt();
            switch (choice) {
                case 1:
                    return SaveMode.CREATE_NEW;
                case 2:
                    return SaveMode.APPEND;
                case 3:
                    return null;
                default:
                    System.out.println("Неверный выбор!");
            }
        }
    }

    private void showFile() {
        Path path = choiceFile();
        if (path == null) {
            return;
        }
        Formatter formatter = getFormatter(path);
        while (true) {
            try {
                System.out.println();
                System.out.println("Что вывести?");
                System.out.println("1. Всё содержимое");
                System.out.println("2. Только данные конкретного API");
                System.out.println("3. Назад");
                int choice = readInt();
                switch (choice) {
                    case 1:
                        formatter.print(path);
                        return;
                    case 2:
                        ApiClient api = choiceApi();
                        if (api == null) {
                            continue;
                        }
                        formatter.printBySource(
                                path,
                                api.getName()
                        );
                        return;
                    case 3:
                        return;
                    default:
                        System.out.println("Неверный выбор");
                }
            } catch (FileProcessingException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private Formatter getFormatter(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".json")) {
            return new JsonFormatter();
        }
        if (fileName.endsWith(".csv")) {
            return new CsvFormatter();
        }
        throw new FileProcessingException(
                "Поддерживаются только .json и .csv"
        );
    }

    private int readInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (InputMismatchException e) {
                System.out.println("Введите число!");
                scanner.nextLine();
            }
        }
    }


}
