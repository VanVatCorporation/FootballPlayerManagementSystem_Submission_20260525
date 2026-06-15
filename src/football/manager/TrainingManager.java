package football.manager;

import football.model.AttendanceRecord;
import football.model.Player;
import football.model.TrainingSession;
import football.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class TrainingManager {
    private final List<TrainingSession> sessions = new ArrayList<>();
    private final Map<String, AttendanceRecord> attendanceRecords = new LinkedHashMap<>();

    public TrainingSession createTrainingSession(String trainingId, LocalDate date, String location, String topic) {
        String normalizedId = InputValidator.normalizeId(trainingId, "Training ID");
        if (date == null) {
            throw new IllegalArgumentException("Training date is required.");
        }
        String validLocation = InputValidator.requireText(location, "Location");
        String validTopic = InputValidator.requireText(topic, "Training topic");
        ensureUniqueTrainingId(normalizedId);

        TrainingSession session = new TrainingSession(normalizedId, date, validLocation, validTopic);
        sessions.add(session);
        return session;
    }

    public AttendanceRecord recordAttendance(String trainingId, Collection<String> absentPlayerIds,
                                             PlayerManager playerManager) {
        String normalizedTrainingId = InputValidator.normalizeId(trainingId, "Training ID");
        requireTrainingSession(normalizedTrainingId);
        List<String> normalizedAbsentIds = normalizeAbsentIds(absentPlayerIds);
        AttendanceRecord existingRecord = attendanceRecords.get(normalizedTrainingId);

        if (existingRecord == null) {
            List<Player> activePlayers = playerManager.getActivePlayers();
            if (activePlayers.isEmpty()) {
                throw new IllegalArgumentException("No active players are available for attendance.");
            }
            LinkedHashMap<String, Boolean> snapshot = new LinkedHashMap<>();
            for (Player player : activePlayers) {
                snapshot.put(player.getPlayerId(), true);
            }
            ensureAllIdsInSnapshot(normalizedAbsentIds, snapshot.keySet(),
                    "Absent Player IDs must belong to existing active players.");
            AttendanceRecord newRecord = new AttendanceRecord(normalizedTrainingId, snapshot);
            newRecord.overwriteAbsentPlayers(normalizedAbsentIds);
            attendanceRecords.put(normalizedTrainingId, newRecord);
            return newRecord;
        }

        ensureAllIdsInSnapshot(normalizedAbsentIds, existingRecord.getAttendanceByPlayerId().keySet(),
                "Absent Player IDs must belong to the original attendance snapshot.");
        existingRecord.overwriteAbsentPlayers(normalizedAbsentIds);
        return existingRecord;
    }

    public TrainingSession requireTrainingSession(String trainingId) {
        String normalizedId = InputValidator.normalizeId(trainingId, "Training ID");
        return findTrainingSession(normalizedId)
                .orElseThrow(() -> new IllegalArgumentException("Training ID not found: " + normalizedId));
    }

    public Optional<TrainingSession> findTrainingSession(String trainingId) {
        String normalizedId = InputValidator.normalizeId(trainingId, "Training ID");
        return sessions.stream()
                .filter(session -> session.getTrainingId().equalsIgnoreCase(normalizedId))
                .findFirst();
    }

    public List<TrainingSession> getAllTrainingSessions() {
        return Collections.unmodifiableList(sessions);
    }

    public Collection<AttendanceRecord> getAllAttendanceRecords() {
        return Collections.unmodifiableCollection(attendanceRecords.values());
    }

    public Optional<AttendanceRecord> findAttendanceRecord(String trainingId) {
        String normalizedId = InputValidator.normalizeId(trainingId, "Training ID");
        return Optional.ofNullable(attendanceRecords.get(normalizedId));
    }

    public void loadAttendanceRecord(AttendanceRecord record) {
        attendanceRecords.put(record.getTrainingId(), record);
    }

    public void clear() {
        sessions.clear();
        attendanceRecords.clear();
    }

    private void ensureUniqueTrainingId(String trainingId) {
        if (sessions.stream().anyMatch(session -> session.getTrainingId().equalsIgnoreCase(trainingId))) {
            throw new IllegalArgumentException("Training ID already exists: " + trainingId);
        }
    }

    private List<String> normalizeAbsentIds(Collection<String> absentPlayerIds) {
        List<String> normalized = new ArrayList<>();
        Set<String> unique = new LinkedHashSet<>();
        if (absentPlayerIds == null) {
            return normalized;
        }
        for (String rawId : absentPlayerIds) {
            if (rawId == null || rawId.trim().isEmpty()) {
                continue;
            }
            String id = InputValidator.normalizeId(rawId, "Absent Player ID");
            if (!unique.add(id)) {
                throw new IllegalArgumentException("Duplicate absent Player IDs are not allowed: " + id);
            }
            normalized.add(id);
        }
        return normalized;
    }

    private void ensureAllIdsInSnapshot(List<String> ids, Collection<String> snapshotIds, String message) {
        for (String id : ids) {
            boolean found = snapshotIds.stream().anyMatch(snapshotId -> snapshotId.equalsIgnoreCase(id));
            if (!found) {
                throw new IllegalArgumentException(message + " Invalid ID: " + id);
            }
        }
    }
}
