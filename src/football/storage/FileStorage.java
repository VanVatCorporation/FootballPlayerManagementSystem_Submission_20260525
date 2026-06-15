package football.storage;

import football.manager.MatchManager;
import football.manager.PlayerManager;
import football.manager.TrainingManager;
import football.model.AttendanceRecord;
import football.model.MatchRecord;
import football.model.MatchType;
import football.model.PerformanceRecord;
import football.model.Player;
import football.model.PlayerStatus;
import football.model.Position;
import football.model.TrainingSession;
import football.util.DateUtil;
import football.util.InputValidator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileStorage {
    private final Path dataDirectory;
    private final Path playersFile;
    private final Path trainingFile;
    private final Path attendanceFile;
    private final Path matchesFile;
    private final Path performanceFile;

    public FileStorage(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.playersFile = dataDirectory.resolve("players.txt");
        this.trainingFile = dataDirectory.resolve("training_sessions.txt");
        this.attendanceFile = dataDirectory.resolve("attendance_records.txt");
        this.matchesFile = dataDirectory.resolve("match_records.txt");
        this.performanceFile = dataDirectory.resolve("performance_records.txt");
    }

    public void loadAll(PlayerManager playerManager, TrainingManager trainingManager,
                        MatchManager matchManager) throws IOException {
        ensureDataFilesExist();
        playerManager.clear();
        trainingManager.clear();
        matchManager.clear();

        loadPlayers(playerManager);
        loadTrainingSessions(trainingManager);
        loadAttendanceRecords(trainingManager, playerManager);
        loadMatches(matchManager);
        loadPerformanceRecords(matchManager, playerManager);
    }

    public void saveAll(PlayerManager playerManager, TrainingManager trainingManager,
                        MatchManager matchManager) throws IOException {
        Files.createDirectories(dataDirectory);
        savePlayers(playerManager);
        saveTrainingSessions(trainingManager);
        saveAttendanceRecords(trainingManager);
        saveMatches(matchManager);
        savePerformanceRecords(matchManager);
    }

    private void ensureDataFilesExist() throws IOException {
        Files.createDirectories(dataDirectory);
        createFileWithHeader(playersFile,
                "# type|playerId|fullName|age|nationality|position|shirtNumber|baseSalary|status");
        createFileWithHeader(trainingFile,
                "# trainingId|date(dd/MM/yyyy)|location|topic");
        createFileWithHeader(attendanceFile,
                "# trainingId|playerId=present/absent;playerId=present/absent");
        createFileWithHeader(matchesFile,
                "# matchId|date(dd/MM/yyyy)|opponentTeam|matchType");
        createFileWithHeader(performanceFile,
                "# matchId|playerId|goals|assists|yellowCards|redCards|minutesPlayed");
    }

    private void createFileWithHeader(Path file, String header) throws IOException {
        if (!Files.exists(file)) {
            Files.write(file, Collections.singletonList(header));
        }
    }

    private void loadPlayers(PlayerManager playerManager) throws IOException {
        int lineNumber = 0;
        for (String line : readDataLines(playersFile)) {
            lineNumber++;
            String[] parts = split(line, 9, playersFile, lineNumber);
            playerManager.addPlayer(
                    parts[0],
                    parts[1],
                    parts[2],
                    parseInt(parts[3], "age", playersFile, lineNumber),
                    parts[4],
                    Position.from(parts[5]),
                    parseInt(parts[6], "shirt number", playersFile, lineNumber),
                    parseLong(parts[7], "base salary", playersFile, lineNumber),
                    PlayerStatus.from(parts[8])
            );
        }
    }

    private void loadTrainingSessions(TrainingManager trainingManager) throws IOException {
        int lineNumber = 0;
        for (String line : readDataLines(trainingFile)) {
            lineNumber++;
            String[] parts = split(line, 4, trainingFile, lineNumber);
            trainingManager.createTrainingSession(
                    parts[0],
                    DateUtil.parseDate(parts[1]),
                    parts[2],
                    parts[3]
            );
        }
    }

    private void loadAttendanceRecords(TrainingManager trainingManager, PlayerManager playerManager)
            throws IOException {
        int lineNumber = 0;
        for (String line : readDataLines(attendanceFile)) {
            lineNumber++;
            String[] parts = split(line, 2, attendanceFile, lineNumber);
            String trainingId = InputValidator.normalizeId(parts[0], "Training ID");
            trainingManager.requireTrainingSession(trainingId);
            Map<String, Boolean> snapshot = parseAttendanceSnapshot(parts[1], playerManager,
                    attendanceFile, lineNumber);
            trainingManager.loadAttendanceRecord(new AttendanceRecord(trainingId, snapshot));
        }
    }

    private void loadMatches(MatchManager matchManager) throws IOException {
        int lineNumber = 0;
        for (String line : readDataLines(matchesFile)) {
            lineNumber++;
            String[] parts = split(line, 4, matchesFile, lineNumber);
            matchManager.createMatchRecord(
                    parts[0],
                    DateUtil.parseDate(parts[1]),
                    parts[2],
                    MatchType.from(parts[3])
            );
        }
    }

    private void loadPerformanceRecords(MatchManager matchManager, PlayerManager playerManager)
            throws IOException {
        int lineNumber = 0;
        for (String line : readDataLines(performanceFile)) {
            lineNumber++;
            String[] parts = split(line, 7, performanceFile, lineNumber);
            String matchId = InputValidator.normalizeId(parts[0], "Match ID");
            String playerId = InputValidator.normalizeId(parts[1], "Player ID");
            matchManager.requireMatchRecord(matchId);
            playerManager.requirePlayer(playerId);
            int goals = parseInt(parts[2], "goals", performanceFile, lineNumber);
            int assists = parseInt(parts[3], "assists", performanceFile, lineNumber);
            int yellowCards = parseInt(parts[4], "yellow cards", performanceFile, lineNumber);
            int redCards = parseInt(parts[5], "red cards", performanceFile, lineNumber);
            int minutesPlayed = parseInt(parts[6], "minutes played", performanceFile, lineNumber);
            InputValidator.validatePerformanceNumbers(goals, assists, yellowCards, redCards, minutesPlayed);
            matchManager.loadPerformanceRecord(new PerformanceRecord(matchId, playerId,
                    goals, assists, yellowCards, redCards, minutesPlayed));
        }
    }

    private List<String> readDataLines(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        for (String rawLine : Files.readAllLines(file)) {
            String line = rawLine.trim();
            if (!line.isEmpty() && !line.startsWith("#")) {
                lines.add(line);
            }
        }
        return lines;
    }

    private String[] split(String line, int expectedParts, Path file, int lineNumber) throws IOException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != expectedParts) {
            throw new IOException("Invalid data format in " + file + " at line " + lineNumber + ".");
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }

    private Map<String, Boolean> parseAttendanceSnapshot(String value, PlayerManager playerManager,
                                                         Path file, int lineNumber) throws IOException {
        LinkedHashMap<String, Boolean> snapshot = new LinkedHashMap<>();
        if (value.trim().isEmpty()) {
            return snapshot;
        }
        String[] entries = value.split(";");
        for (String entry : entries) {
            String[] pair = entry.split("=", -1);
            if (pair.length != 2) {
                throw new IOException("Invalid attendance entry in " + file + " at line " + lineNumber + ".");
            }
            String playerId = InputValidator.normalizeId(pair[0], "Player ID");
            playerManager.requirePlayer(playerId);
            String status = pair[1].trim().toLowerCase();
            if (!status.equals("present") && !status.equals("absent")) {
                throw new IOException("Attendance status must be present or absent in " + file
                        + " at line " + lineNumber + ".");
            }
            snapshot.put(playerId, status.equals("present"));
        }
        return snapshot;
    }

    private int parseInt(String value, String fieldName, Path file, int lineNumber) throws IOException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid " + fieldName + " in " + file + " at line " + lineNumber + ".", ex);
        }
    }

    private long parseLong(String value, String fieldName, Path file, int lineNumber) throws IOException {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid " + fieldName + " in " + file + " at line " + lineNumber + ".", ex);
        }
    }

    private void savePlayers(PlayerManager playerManager) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# type|playerId|fullName|age|nationality|position|shirtNumber|baseSalary|status");
        for (Player player : playerManager.getAllPlayers()) {
            lines.add(String.join("|",
                    player.getPlayerType(),
                    player.getPlayerId(),
                    player.getFullName(),
                    String.valueOf(player.getAge()),
                    player.getNationality(),
                    player.getPosition().getDisplayName(),
                    String.valueOf(player.getShirtNumber()),
                    String.valueOf(player.getBaseSalary()),
                    player.getStatus().getDisplayName()));
        }
        Files.write(playersFile, lines);
    }

    private void saveTrainingSessions(TrainingManager trainingManager) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# trainingId|date(dd/MM/yyyy)|location|topic");
        for (TrainingSession session : trainingManager.getAllTrainingSessions()) {
            lines.add(String.join("|",
                    session.getTrainingId(),
                    DateUtil.formatDate(session.getDate()),
                    session.getLocation(),
                    session.getTopic()));
        }
        Files.write(trainingFile, lines);
    }

    private void saveAttendanceRecords(TrainingManager trainingManager) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# trainingId|playerId=present/absent;playerId=present/absent");
        for (AttendanceRecord record : trainingManager.getAllAttendanceRecords()) {
            List<String> entries = new ArrayList<>();
            for (Map.Entry<String, Boolean> entry : record.getAttendanceByPlayerId().entrySet()) {
                entries.add(entry.getKey() + "=" + (entry.getValue() ? "present" : "absent"));
            }
            lines.add(record.getTrainingId() + "|" + String.join(";", entries));
        }
        Files.write(attendanceFile, lines);
    }

    private void saveMatches(MatchManager matchManager) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# matchId|date(dd/MM/yyyy)|opponentTeam|matchType");
        for (MatchRecord match : matchManager.getAllMatchRecords()) {
            lines.add(String.join("|",
                    match.getMatchId(),
                    DateUtil.formatDate(match.getDate()),
                    match.getOpponentTeam(),
                    match.getMatchType().getDisplayName()));
        }
        Files.write(matchesFile, lines);
    }

    private void savePerformanceRecords(MatchManager matchManager) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("# matchId|playerId|goals|assists|yellowCards|redCards|minutesPlayed");
        for (PerformanceRecord record : matchManager.getAllPerformanceRecords()) {
            lines.add(String.join("|",
                    record.getMatchId(),
                    record.getPlayerId(),
                    String.valueOf(record.getGoals()),
                    String.valueOf(record.getAssists()),
                    String.valueOf(record.getYellowCards()),
                    String.valueOf(record.getRedCards()),
                    String.valueOf(record.getMinutesPlayed())));
        }
        Files.write(performanceFile, lines);
    }
}
