package football.manager;

import football.model.PerformanceRecord;
import football.model.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportManager {
    private final PlayerManager playerManager;
    private final MatchManager matchManager;
    private final SalaryManager salaryManager;

    public ReportManager(PlayerManager playerManager, MatchManager matchManager, SalaryManager salaryManager) {
        this.playerManager = playerManager;
        this.matchManager = matchManager;
        this.salaryManager = salaryManager;
    }

    public String buildPlayerTable(List<Player> players) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%-8s %-22s %-4s %-12s %-6s %-16s %-8s%n",
                "ID", "Name", "Age", "Position", "Shirt", "Type", "Status"));
        builder.append("--------------------------------------------------------------------------------\n");
        if (players.isEmpty()) {
            builder.append("No players found.\n");
        } else {
            for (Player player : players) {
                builder.append(String.format("%-8s %-22s %-4d %-12s %-6d %-16s %-8s%n",
                        player.getPlayerId(),
                        trimToWidth(player.getFullName(), 22),
                        player.getAge(),
                        player.getPosition(),
                        player.getShirtNumber(),
                        player.getPlayerType(),
                        player.getStatus()));
            }
        }
        builder.append("--------------------------------------------------------------------------------");
        return builder.toString();
    }

    public String buildSalarySummaryReport(int month, int year) {
        StringBuilder builder = new StringBuilder();
        builder.append("----------- SALARY SUMMARY REPORT -----------\n");
        builder.append(String.format("Month: %02d/%d%n", month, year));
        builder.append(String.format("%-8s %-22s %-16s %12s %12s %12s%n",
                "ID", "Name", "Type", "Base Salary", "Bonus", "Total"));
        builder.append("--------------------------------------------------------------------------------\n");
        long totalCost = 0;
        List<Player> players = playerManager.getAllPlayers();
        if (players.isEmpty()) {
            builder.append("No players found.\n");
        } else {
            for (Player player : players) {
                int points = salaryManager.calculateMonthlyPerformancePoints(player.getPlayerId(), month, year);
                long bonus = player.calculateBonus(points);
                long total = player.calculateMonthlySalary(points);
                totalCost += total;
                builder.append(String.format("%-8s %-22s %-16s %12d %12d %12d%n",
                        player.getPlayerId(),
                        trimToWidth(player.getFullName(), 22),
                        player.getPlayerType(),
                        player.getBaseSalary(),
                        bonus,
                        total));
            }
        }
        builder.append("--------------------------------------------------------------------------------\n");
        builder.append("Total Monthly Salary Cost: ").append(totalCost).append(" VND");
        return builder.toString();
    }

    public String buildTopGoalScorersReport() {
        Map<String, Integer> goalsByPlayer = new HashMap<>();
        for (PerformanceRecord record : matchManager.getAllPerformanceRecords()) {
            goalsByPlayer.merge(record.getPlayerId(), record.getGoals(), Integer::sum);
        }

        List<Map.Entry<String, Integer>> rows = new ArrayList<>(goalsByPlayer.entrySet());
        rows.sort((left, right) -> {
            int goalComparison = Integer.compare(right.getValue(), left.getValue());
            if (goalComparison != 0) {
                return goalComparison;
            }
            String leftName = playerManager.findPlayer(left.getKey()).map(Player::getFullName).orElse(left.getKey());
            String rightName = playerManager.findPlayer(right.getKey()).map(Player::getFullName).orElse(right.getKey());
            return leftName.compareToIgnoreCase(rightName);
        });

        StringBuilder builder = new StringBuilder();
        builder.append("----------- ALL-TIME TOP GOAL SCORERS -----------\n");
        builder.append(String.format("%-6s %-10s %-22s %-12s %-6s%n",
                "Rank", "Player ID", "Name", "Position", "Goals"));
        builder.append("--------------------------------------------------------------\n");
        if (rows.isEmpty()) {
            builder.append("No performance records found.\n");
        } else {
            int rank = 1;
            for (Map.Entry<String, Integer> row : rows) {
                Player player = playerManager.findPlayer(row.getKey()).orElse(null);
                String name = player == null ? "Unknown" : player.getFullName();
                String position = player == null ? "Unknown" : player.getPosition().toString();
                builder.append(String.format("%-6d %-10s %-22s %-12s %-6d%n",
                        rank++,
                        row.getKey(),
                        trimToWidth(name, 22),
                        position,
                        row.getValue()));
            }
        }
        builder.append("--------------------------------------------------------------");
        return builder.toString();
    }

    private String trimToWidth(String value, int width) {
        if (value.length() <= width) {
            return value;
        }
        return value.substring(0, Math.max(0, width - 3)) + "...";
    }
}
