package football.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AttendanceRecord {
    private final String trainingId;
    private final LinkedHashMap<String, Boolean> attendanceByPlayerId;

    public AttendanceRecord(String trainingId, Map<String, Boolean> attendanceByPlayerId) {
        this.trainingId = trainingId;
        this.attendanceByPlayerId = new LinkedHashMap<>(attendanceByPlayerId);
    }

    public String getTrainingId() {
        return trainingId;
    }

    public Map<String, Boolean> getAttendanceByPlayerId() {
        return Collections.unmodifiableMap(attendanceByPlayerId);
    }

    public void overwriteAbsentPlayers(List<String> absentPlayerIds) {
        for (String playerId : attendanceByPlayerId.keySet()) {
            attendanceByPlayerId.put(playerId, true);
        }
        for (String playerId : absentPlayerIds) {
            attendanceByPlayerId.put(playerId, false);
        }
    }

    public int getPresentCount() {
        int count = 0;
        for (boolean present : attendanceByPlayerId.values()) {
            if (present) {
                count++;
            }
        }
        return count;
    }

    public int getAbsentCount() {
        return attendanceByPlayerId.size() - getPresentCount();
    }
}
