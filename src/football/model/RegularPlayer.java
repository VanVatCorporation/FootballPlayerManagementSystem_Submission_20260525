package football.model;

public class RegularPlayer extends Player {
    public RegularPlayer(String playerId, String fullName, int age, String nationality,
                         Position position, int shirtNumber, long baseSalary, PlayerStatus status) {
        super(playerId, fullName, age, nationality, position, shirtNumber, baseSalary, status);
    }

    @Override
    public String getPlayerType() {
        return "Regular Player";
    }

    @Override
    public long calculateBonus(int monthlyPerformancePoints) {
        return 0;
    }
}
