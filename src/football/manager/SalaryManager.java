package football.manager;

import football.model.MatchRecord;
import football.model.PerformanceRecord;
import football.model.Player;
import football.util.DateUtil;

public class SalaryManager {
    private final PlayerManager playerManager;
    private final MatchManager matchManager;

    public SalaryManager(PlayerManager playerManager, MatchManager matchManager) {
        this.playerManager = playerManager;
        this.matchManager = matchManager;
    }

    public int calculateMonthlyPerformancePoints(String playerId, int month, int year) {
        DateUtil.validateMonthYear(month, year);
        Player player = playerManager.requirePlayer(playerId);
        int totalPoints = 0;
        for (PerformanceRecord record : matchManager.getAllPerformanceRecords()) {
            if (!record.getPlayerId().equalsIgnoreCase(player.getPlayerId())) {
                continue;
            }
            MatchRecord match = matchManager.requireMatchRecord(record.getMatchId());
            if (DateUtil.matchesMonthYear(match.getDate(), month, year)) {
                totalPoints += record.calculatePerformancePoints();
            }
        }
        return totalPoints;
    }

    public long calculateMonthlyBonus(Player player, int month, int year) {
        int points = calculateMonthlyPerformancePoints(player.getPlayerId(), month, year);
        return player.calculateBonus(points);
    }

    public long calculateMonthlySalary(Player player, int month, int year) {
        int points = calculateMonthlyPerformancePoints(player.getPlayerId(), month, year);
        return player.calculateMonthlySalary(points);
    }
}
