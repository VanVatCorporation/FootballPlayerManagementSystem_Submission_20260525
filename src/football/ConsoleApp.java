package football;

import football.manager.MatchManager;
import football.manager.PlayerManager;
import football.manager.ReportManager;
import football.manager.SalaryManager;
import football.manager.TrainingManager;
import football.model.AttendanceRecord;
import football.model.MatchRecord;
import football.model.MatchType;
import football.model.PerformanceRecord;
import football.model.Player;
import football.model.PlayerStatus;
import football.model.Position;
import football.model.TrainingSession;
import football.storage.FileStorage;
import football.util.DateUtil;
import football.util.InputValidator;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ConsoleApp {
    private final Scanner scanner = new Scanner(System.in);
    private final PlayerManager playerManager = new PlayerManager();
    private final TrainingManager trainingManager = new TrainingManager();
    private final MatchManager matchManager = new MatchManager();
    private final SalaryManager salaryManager = new SalaryManager(playerManager, matchManager);
    private final ReportManager reportManager = new ReportManager(playerManager, matchManager, salaryManager);
    private final FileStorage fileStorage;

    public ConsoleApp(Path dataDirectory) {
        this.fileStorage = new FileStorage(dataDirectory);
    }

    public void run() {
        try {
            fileStorage.loadAll(playerManager, trainingManager, matchManager);
        } catch (IOException | IllegalArgumentException ex) {
            System.out.println("Fail: Could not load data files. " + ex.getMessage());
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readMenuOption("Choose an option: ", 1, 5);
            switch (choice) {
                case 1:
                    managePlayersMenu();
                    break;
                case 2:
                    trainingAndMatchMenu();
                    break;
                case 3:
                    contractAndSalaryMenu();
                    break;
                case 4:
                    reportsMenu();
                    break;
                case 5:
                    running = exitMenu();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("======================================");
        System.out.println("FOOTBALL PLAYER MANAGEMENT SYSTEM");
        System.out.println("======================================");
        System.out.println("1. Manage Players");
        System.out.println("2. Training and Match Management");
        System.out.println("3. Contract and Salary Management");
        System.out.println("4. Reports");
        System.out.println("5. Exit");
        System.out.println("--------------------------------------");
    }

    private void managePlayersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----------- MANAGE PLAYERS -----------");
            System.out.println("1. Add Player");
            System.out.println("2. Update Player");
            System.out.println("3. Deactivate Player");
            System.out.println("4. View All Players");
            System.out.println("5. Search Players");
            System.out.println("6. View Player Details");
            System.out.println("7. Back");
            int choice = readMenuOption("Choose an option: ", 1, 7);
            switch (choice) {
                case 1:
                    addPlayer();
                    break;
                case 2:
                    updatePlayer();
                    break;
                case 3:
                    deactivatePlayer();
                    break;
                case 4:
                    viewAllPlayers();
                    break;
                case 5:
                    searchPlayers();
                    break;
                case 6:
                    viewPlayerDetails();
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void trainingAndMatchMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----------- TRAINING AND MATCH MANAGEMENT -----------");
            System.out.println("1. Create Training Session");
            System.out.println("2. Record Training Attendance");
            System.out.println("3. Create Match Record");
            System.out.println("4. Player Performance");
            System.out.println("5. View Training History");
            System.out.println("6. View Match History");
            System.out.println("7. Back");
            int choice = readMenuOption("Choose an option: ", 1, 7);
            switch (choice) {
                case 1:
                    createTrainingSession();
                    break;
                case 2:
                    recordTrainingAttendance();
                    break;
                case 3:
                    createMatchRecord();
                    break;
                case 4:
                    playerPerformanceMenu();
                    break;
                case 5:
                    viewTrainingHistory();
                    break;
                case 6:
                    viewMatchHistory();
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void contractAndSalaryMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----------- CONTRACT AND SALARY MANAGEMENT -----------");
            System.out.println("1. Display Player Type");
            System.out.println("2. Calculate Monthly Salary");
            System.out.println("3. Calculate Performance Bonus");
            System.out.println("4. Display Salary Summary");
            System.out.println("5. Back");
            int choice = readMenuOption("Choose an option: ", 1, 5);
            switch (choice) {
                case 1:
                    displayPlayerType();
                    break;
                case 2:
                    calculateMonthlySalary();
                    break;
                case 3:
                    calculatePerformanceBonus();
                    break;
                case 4:
                    displaySalarySummary();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void reportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----------- REPORTS -----------");
            System.out.println("1. Salary Summary Report");
            System.out.println("2. All-time Top Goal Scorers");
            System.out.println("3. Back");
            int choice = readMenuOption("Choose an option: ", 1, 3);
            switch (choice) {
                case 1:
                    displaySalarySummary();
                    break;
                case 2:
                    displayTopGoalScorers();
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addPlayer() {
        System.out.println();
        System.out.println("----------- ADD PLAYER -----------");
        try {
            String playerId = readId("Player ID: ", "Player ID");
            String fullName = readRequiredText("Full Name: ", "Full name");
            int age = readIntInRange("Age: ", 16, 45);
            String nationality = readRequiredText("Nationality: ", "Nationality");
            Position position = readPosition("Position: ");
            int shirtNumber = readIntInRange("Shirt Number: ", 1, 99);
            long baseSalary = readLongGreaterThan("Base Salary: ", 0);
            System.out.println("Player Type:");
            System.out.println("1. Regular Player");
            System.out.println("2. Star Player");
            String playerType = String.valueOf(readMenuOption("Choose Player Type: ", 1, 2));
            PlayerStatus status = readStatus("Status (Active/Inactive): ");
            if (!confirm("[1] Submit [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            playerManager.addPlayer(playerType, playerId, fullName, age, nationality,
                    position, shirtNumber, baseSalary, status);
            printSuccess("Player added successfully.");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void updatePlayer() {
        System.out.println();
        System.out.println("----------- UPDATE PLAYER -----------");
        try {
            String playerId = readId("Enter Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            System.out.println("Current Information:");
            System.out.printf("Name: %s | Position: %s | No.%d%n",
                    player.getFullName(), player.getPosition(), player.getShirtNumber());
            System.out.printf("Base Salary: %d | Type: %s | Status: %s%n",
                    player.getBaseSalary(), player.getPlayerType(), player.getStatus());
            Position position = readPosition("Enter new Position: ");
            int shirtNumber = readIntInRange("Enter new Shirt Number: ", 1, 99);
            long baseSalary = readLongGreaterThan("Enter new Base Salary: ", 0);
            PlayerStatus status = readStatus("Enter new Status (Active/Inactive): ");
            if (!confirm("[1] Update [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            playerManager.updatePlayer(playerId, position, shirtNumber, baseSalary, status);
            printSuccess("The player updated successfully.");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void deactivatePlayer() {
        System.out.println();
        System.out.println("----------- DEACTIVATE PLAYER -----------");
        try {
            String playerId = readId("Enter Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            System.out.println("Player: " + player.getFullName());
            if (!confirm("[1] Deactivate [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            playerManager.deactivatePlayer(playerId);
            printSuccess("Player deactivated successfully.");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void viewAllPlayers() {
        System.out.println();
        System.out.println("----------- PLAYER LIST -----------");
        System.out.println(reportManager.buildPlayerTable(playerManager.getAllPlayers()));
        pause();
    }

    private void searchPlayers() {
        System.out.println();
        System.out.println("----------- SEARCH PLAYERS -----------");
        try {
            System.out.println("Search by:");
            System.out.println("1. Name");
            System.out.println("2. Position");
            System.out.println("3. Nationality");
            System.out.println("4. Status");
            int searchType = readMenuOption("Choose search type: ", 1, 4);
            String keyword = readRequiredText("Enter keyword: ", "Keyword");
            System.out.println("Search Results:");
            System.out.println(reportManager.buildPlayerTable(playerManager.searchPlayers(searchType, keyword)));
            pause();
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void viewPlayerDetails() {
        System.out.println();
        System.out.println("----------- PLAYER DETAILS -----------");
        try {
            String playerId = readId("Enter Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            System.out.println("Player ID: " + player.getPlayerId());
            System.out.println("Full Name: " + player.getFullName());
            System.out.println("Age: " + player.getAge());
            System.out.println("Nationality: " + player.getNationality());
            System.out.println("Position: " + player.getPosition());
            System.out.println("Shirt Number: " + player.getShirtNumber());
            System.out.println("Base Salary: " + player.getBaseSalary());
            System.out.println("Player Type: " + player.getPlayerType());
            System.out.println("Status: " + player.getStatus());
            pause();
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void createTrainingSession() {
        System.out.println();
        System.out.println("----------- CREATE TRAINING SESSION -----------");
        try {
            String trainingId = readId("Training ID: ", "Training ID");
            LocalDate date = readDate("Date: ");
            String location = readRequiredText("Location: ", "Location");
            String topic = readRequiredText("Training Topic: ", "Training topic");
            if (!confirm("[1] Submit [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            trainingManager.createTrainingSession(trainingId, date, location, topic);
            printSuccess("Training session created successfully.");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void recordTrainingAttendance() {
        System.out.println();
        System.out.println("----------- RECORD TRAINING ATTENDANCE -----------");
        try {
            String trainingId = readId("Training ID: ", "Training ID");
            TrainingSession session = trainingManager.requireTrainingSession(trainingId);
            System.out.println("Date: " + DateUtil.formatDate(session.getDate()));
            Optional<AttendanceRecord> existing = trainingManager.findAttendanceRecord(trainingId);
            int totalPlayers = existing
                    .map(record -> record.getAttendanceByPlayerId().size())
                    .orElse(playerManager.getActivePlayers().size());
            System.out.println("Total Active Players: " + totalPlayers);
            if (existing.isPresent()) {
                System.out.println("Existing attendance found. The same original player snapshot will be updated.");
            }
            System.out.println("Enter absent Player IDs, separated by commas.");
            System.out.println("Leave blank if all included active players are present.");
            List<String> absentIds = readCommaSeparatedIds("Absent Player IDs: ");
            if (!confirm("[1] Submit Attendance [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            AttendanceRecord record = trainingManager.recordAttendance(trainingId, absentIds, playerManager);
            printSuccess("Training attendance was recorded successfully.");
            System.out.println("Summary:");
            System.out.println("Present: " + record.getPresentCount());
            System.out.println("Absent: " + record.getAbsentCount());
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void createMatchRecord() {
        System.out.println();
        System.out.println("----------- CREATE MATCH RECORD -----------");
        try {
            String matchId = readId("Match ID: ", "Match ID");
            LocalDate date = readDate("Date: ");
            String opponentTeam = readRequiredText("Opponent Team: ", "Opponent team");
            System.out.println("Match Type:");
            System.out.println("1. Friendly");
            System.out.println("2. League");
            System.out.println("3. Cup");
            MatchType matchType = MatchType.from(String.valueOf(readMenuOption("Choose Match Type: ", 1, 3)));
            if (!confirm("[1] Submit [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            matchManager.createMatchRecord(matchId, date, opponentTeam, matchType);
            printSuccess("Match record created successfully.");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void playerPerformanceMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("----------- PLAYER PERFORMANCE -----------");
            System.out.println("1. Add / Update");
            System.out.println("2. Show");
            System.out.println("3. Back");
            int choice = readMenuOption("Choose an option: ", 1, 3);
            switch (choice) {
                case 1:
                    addOrUpdatePlayerPerformance();
                    break;
                case 2:
                    showPlayerPerformance();
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void addOrUpdatePlayerPerformance() {
        System.out.println();
        System.out.println("----------- PLAYER PERFORMANCE MANAGEMENT -----------");
        try {
            String matchId = readId("Match ID: ", "Match ID");
            matchManager.requireMatchRecord(matchId);
            String playerId = readId("Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            System.out.println("Player Name: " + player.getFullName());
            Optional<PerformanceRecord> existing = matchManager.findPerformanceRecord(matchId, playerId);
            if (existing.isPresent()) {
                PerformanceRecord record = existing.get();
                System.out.println("Existing Performance:");
                System.out.printf("Goals: %d | Assists: %d | Yellow Cards: %d%n",
                        record.getGoals(), record.getAssists(), record.getYellowCards());
                System.out.printf("Red Cards: %d | Minutes Played: %d%n",
                        record.getRedCards(), record.getMinutesPlayed());
                if (!confirm("[1] Replace [2] Cancel", "Choose an option: ")) {
                    System.out.println("Cancelled.");
                    return;
                }
            }
            System.out.println("Enter new performance data:");
            int goals = readIntInRange("Goals: ", 0, Integer.MAX_VALUE);
            int assists = readIntInRange("Assists: ", 0, Integer.MAX_VALUE);
            int yellowCards = readIntInRange("Yellow Cards: ", 0, Integer.MAX_VALUE);
            int redCards = readIntInRange("Red Cards: ", 0, Integer.MAX_VALUE);
            int minutesPlayed = readIntInRange("Minutes Played: ", 0, 120);
            if (!confirm("[1] Submit [2] Cancel", "Choose an option: ")) {
                System.out.println("Cancelled.");
                return;
            }
            PerformanceRecord record = matchManager.addOrReplacePerformance(playerManager, matchId, playerId,
                    goals, assists, yellowCards, redCards, minutesPlayed);
            printSuccess("Player performance saved successfully.");
            System.out.println("Performance Points: " + record.calculatePerformancePoints());
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void showPlayerPerformance() {
        System.out.println("----------- PLAYER PERFORMANCE LIST -----------");
        List<PerformanceRecord> records = matchManager.getAllPerformanceRecords();
        System.out.printf("%-8s %-12s %-20s %5s %7s %6s %4s %7s %7s%n",
                "Match", "Player ID", "Player Name", "Goals", "Assists", "Yellow", "Red", "Minutes", "Points");
        System.out.println("--------------------------------------------------------------------------------------------");
        if (records.isEmpty()) {
            System.out.println("No player performance records found.");
        } else {
            for (PerformanceRecord record : records) {
                String playerName = playerManager.findPlayer(record.getPlayerId())
                        .map(Player::getFullName)
                        .orElse("Unknown");
                System.out.printf("%-8s %-12s %-20s %5d %7d %6d %4d %7d %7d%n",
                        record.getMatchId(),
                        record.getPlayerId(),
                        trimToWidth(playerName, 20),
                        record.getGoals(),
                        record.getAssists(),
                        record.getYellowCards(),
                        record.getRedCards(),
                        record.getMinutesPlayed(),
                        record.calculatePerformancePoints());
            }
        }
        System.out.println("--------------------------------------------------------------------------------------------");
        pause();
    }

    private void viewTrainingHistory() {
        System.out.println();
        System.out.println("----------- TRAINING HISTORY -----------");
        System.out.printf("%-8s %-12s %-25s %-30s%n", "ID", "Date", "Location", "Topic");
        System.out.println("----------------------------------------------------------------------------");
        if (trainingManager.getAllTrainingSessions().isEmpty()) {
            System.out.println("No training sessions found.");
        } else {
            for (TrainingSession session : trainingManager.getAllTrainingSessions()) {
                System.out.printf("%-8s %-12s %-25s %-30s%n",
                        session.getTrainingId(),
                        DateUtil.formatDate(session.getDate()),
                        trimToWidth(session.getLocation(), 25),
                        trimToWidth(session.getTopic(), 30));
            }
        }
        System.out.println("----------------------------------------------------------------------------");
        pause();
    }

    private void viewMatchHistory() {
        System.out.println();
        System.out.println("----------- MATCH HISTORY -----------");
        System.out.printf("%-8s %-12s %-25s %-12s%n", "ID", "Date", "Opponent Team", "Match Type");
        System.out.println("--------------------------------------------------------------");
        if (matchManager.getAllMatchRecords().isEmpty()) {
            System.out.println("No match records found.");
        } else {
            for (MatchRecord match : matchManager.getAllMatchRecords()) {
                System.out.printf("%-8s %-12s %-25s %-12s%n",
                        match.getMatchId(),
                        DateUtil.formatDate(match.getDate()),
                        trimToWidth(match.getOpponentTeam(), 25),
                        match.getMatchType());
            }
        }
        System.out.println("--------------------------------------------------------------");
        pause();
    }

    private void displayPlayerType() {
        System.out.println();
        System.out.println("----------- DISPLAY PLAYER TYPE -----------");
        try {
            String playerId = readId("Enter Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            System.out.println("Player: " + player.getFullName());
            System.out.println("Type: " + player.getPlayerType());
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void calculateMonthlySalary() {
        System.out.println();
        System.out.println("----------- CALCULATE PLAYER SALARY -----------");
        try {
            int month = readIntInRange("Enter Month: ", 1, 12);
            int year = readIntInRange("Enter Year: ", 2000, 2100);
            String playerId = readId("Enter Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            int points = salaryManager.calculateMonthlyPerformancePoints(playerId, month, year);
            long bonus = player.calculateBonus(points);
            long total = player.calculateMonthlySalary(points);
            System.out.println("Player: " + player.getFullName());
            System.out.println("Type: " + player.getPlayerType());
            System.out.println("Base Salary: " + player.getBaseSalary());
            System.out.println("Monthly Performance Points: " + points);
            System.out.println("Output:");
            System.out.println("Salary Summary:");
            System.out.println("Base Salary: " + player.getBaseSalary() + " VND");
            System.out.println("Performance Bonus: " + bonus + " VND");
            System.out.println("Total Salary: " + total + " VND");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void calculatePerformanceBonus() {
        System.out.println();
        System.out.println("----------- CALCULATE PERFORMANCE BONUS -----------");
        try {
            int month = readIntInRange("Enter Month: ", 1, 12);
            int year = readIntInRange("Enter Year: ", 2000, 2100);
            String playerId = readId("Enter Player ID: ", "Player ID");
            Player player = playerManager.requirePlayer(playerId);
            int points = salaryManager.calculateMonthlyPerformancePoints(playerId, month, year);
            long bonus = player.calculateBonus(points);
            System.out.println("Player: " + player.getFullName());
            System.out.println("Type: " + player.getPlayerType());
            System.out.println("Monthly Performance Points: " + points);
            System.out.println("Performance Bonus: " + bonus + " VND");
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void displaySalarySummary() {
        System.out.println();
        try {
            int month = readIntInRange("Enter Month: ", 1, 12);
            int year = readIntInRange("Enter Year: ", 2000, 2100);
            System.out.println(reportManager.buildSalarySummaryReport(month, year));
            pause();
        } catch (IllegalArgumentException ex) {
            printFail(ex.getMessage());
        }
    }

    private void displayTopGoalScorers() {
        System.out.println();
        System.out.println(reportManager.buildTopGoalScorersReport());
        pause();
    }

    private boolean exitMenu() {
        System.out.println();
        System.out.println("----------- EXIT SYSTEM -----------");
        System.out.println("Do you want to save all data before exiting?");
        System.out.println("1. Save and Exit");
        System.out.println("2. Exit without Saving");
        System.out.println("3. Cancel");
        int choice = readMenuOption("Choose an option: ", 1, 3);
        if (choice == 3) {
            return true;
        }
        if (choice == 1) {
            try {
                fileStorage.saveAll(playerManager, trainingManager, matchManager);
                printSuccess("Data saved successfully.");
            } catch (IOException ex) {
                printFail("Could not save data. " + ex.getMessage());
                return true;
            }
        }
        System.out.println("Thank you for using the Football Player Management System.");
        return false;
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private int readMenuOption(String prompt, int min, int max) {
        while (true) {
            String raw = readLine(prompt);
            try {
                int value = Integer.parseInt(raw.trim());
                if (value < min || value > max) {
                    System.out.printf("Invalid option. Please enter a number from %d to %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private String readRequiredText(String prompt, String fieldName) {
        while (true) {
            try {
                return InputValidator.requireText(readLine(prompt), fieldName);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
    }

    private String readId(String prompt, String fieldName) {
        while (true) {
            try {
                return InputValidator.normalizeId(readLine(prompt), fieldName);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
    }

    private int readIntInRange(String prompt, int min, int max) {
        while (true) {
            String raw = readLine(prompt);
            try {
                int value = Integer.parseInt(raw.trim());
                if (value < min || value > max) {
                    System.out.printf("Invalid input. Value must be from %d to %d.%n", min, max);
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private long readLongGreaterThan(String prompt, long minExclusive) {
        while (true) {
            String raw = readLine(prompt);
            try {
                long value = Long.parseLong(raw.trim());
                if (value <= minExclusive) {
                    System.out.println("Invalid input. Value must be greater than " + minExclusive + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            try {
                return DateUtil.parseDate(readLine(prompt));
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
    }

    private Position readPosition(String prompt) {
        while (true) {
            try {
                return Position.from(readLine(prompt));
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
    }

    private PlayerStatus readStatus(String prompt) {
        while (true) {
            try {
                return PlayerStatus.from(readLine(prompt));
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
    }

    private List<String> readCommaSeparatedIds(String prompt) {
        String raw = readLine(prompt).trim();
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .collect(Collectors.toList());
    }

    private boolean confirm(String menuLine, String prompt) {
        System.out.println(menuLine);
        return readMenuOption(prompt, 1, 2) == 1;
    }

    private void pause() {
        System.out.print("Press ENTER to return...");
        scanner.nextLine();
    }

    private void printSuccess(String message) {
        System.out.println("Output:");
        System.out.println(message);
    }

    private void printFail(String message) {
        System.out.println("Fail: " + message);
    }

    private String trimToWidth(String value, int width) {
        if (value.length() <= width) {
            return value;
        }
        return value.substring(0, Math.max(0, width - 3)) + "...";
    }
}
