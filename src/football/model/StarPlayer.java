package football.model;

public class StarPlayer extends Player {
    private static final long BONUS_PER_POINT = 500_000L;

    public StarPlayer(String playerId, String fullName, int age, String nationality,
                      Position position, int shirtNumber, long baseSalary, PlayerStatus status) {
        super(playerId, fullName, age, nationality, position, shirtNumber, baseSalary, status);
    }

    @Override
    public String getPlayerType() {
        return "Star Player";
    }

    @Override
    public long calculateBonus(int monthlyPerformancePoints) {
        return Math.max(0, monthlyPerformancePoints) * BONUS_PER_POINT;
    }
}
