package football.manager;

import football.model.MatchRecord;
import football.model.MatchType;
import football.model.PerformanceRecord;
import football.model.Player;
import football.util.InputValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MatchManager {
    private final List<MatchRecord> matches = new ArrayList<>();
    private final Map<String, Map<String, PerformanceRecord>> performanceByMatch = new LinkedHashMap<>();

    public MatchRecord createMatchRecord(String matchId, LocalDate date, String opponentTeam, MatchType matchType) {
        String normalizedId = InputValidator.normalizeId(matchId, "Match ID");
        if (date == null) {
            throw new IllegalArgumentException("Match date is required.");
        }
        String validOpponentTeam = InputValidator.requireText(opponentTeam, "Opponent team");
        if (matchType == null) {
            throw new IllegalArgumentException("Match type is required.");
        }
        ensureUniqueMatchId(normalizedId);

        MatchRecord match = new MatchRecord(normalizedId, date, validOpponentTeam, matchType);
        matches.add(match);
        return match;
    }

    public PerformanceRecord addOrReplacePerformance(PlayerManager playerManager, String matchId, String playerId,
                                                     int goals, int assists, int yellowCards,
                                                     int redCards, int minutesPlayed) {
        String normalizedMatchId = InputValidator.normalizeId(matchId, "Match ID");
        String normalizedPlayerId = InputValidator.normalizeId(playerId, "Player ID");
        requireMatchRecord(normalizedMatchId);
        Player player = playerManager.requirePlayer(normalizedPlayerId);
        if (!player.isActive()) {
            throw new IllegalArgumentException("Only active players can be included in match performance records.");
        }
        InputValidator.validatePerformanceNumbers(goals, assists, yellowCards, redCards, minutesPlayed);

        PerformanceRecord record = new PerformanceRecord(normalizedMatchId, normalizedPlayerId,
                goals, assists, yellowCards, redCards, minutesPlayed);
        performanceByMatch
                .computeIfAbsent(normalizedMatchId, ignored -> new LinkedHashMap<>())
                .put(normalizedPlayerId, record);
        return record;
    }

    public void loadPerformanceRecord(PerformanceRecord record) {
        performanceByMatch
                .computeIfAbsent(record.getMatchId(), ignored -> new LinkedHashMap<>())
                .put(record.getPlayerId(), record);
    }

    public MatchRecord requireMatchRecord(String matchId) {
        String normalizedId = InputValidator.normalizeId(matchId, "Match ID");
        return findMatchRecord(normalizedId)
                .orElseThrow(() -> new IllegalArgumentException("Match ID not found: " + normalizedId));
    }

    public Optional<MatchRecord> findMatchRecord(String matchId) {
        String normalizedId = InputValidator.normalizeId(matchId, "Match ID");
        return matches.stream()
                .filter(match -> match.getMatchId().equalsIgnoreCase(normalizedId))
                .findFirst();
    }

    public Optional<PerformanceRecord> findPerformanceRecord(String matchId, String playerId) {
        String normalizedMatchId = InputValidator.normalizeId(matchId, "Match ID");
        String normalizedPlayerId = InputValidator.normalizeId(playerId, "Player ID");
        Map<String, PerformanceRecord> byPlayer = performanceByMatch.get(normalizedMatchId);
        if (byPlayer == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(byPlayer.get(normalizedPlayerId));
    }

    public List<MatchRecord> getAllMatchRecords() {
        return Collections.unmodifiableList(matches);
    }

    public List<PerformanceRecord> getAllPerformanceRecords() {
        List<PerformanceRecord> records = new ArrayList<>();
        for (Map<String, PerformanceRecord> byPlayer : performanceByMatch.values()) {
            records.addAll(byPlayer.values());
        }
        return records;
    }

    public Collection<Map<String, PerformanceRecord>> getPerformanceMapsForStorage() {
        return Collections.unmodifiableCollection(performanceByMatch.values());
    }

    public void clear() {
        matches.clear();
        performanceByMatch.clear();
    }

    private void ensureUniqueMatchId(String matchId) {
        if (matches.stream().anyMatch(match -> match.getMatchId().equalsIgnoreCase(matchId))) {
            throw new IllegalArgumentException("Match ID already exists: " + matchId);
        }
    }
}
