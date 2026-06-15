package football.model;

public class PerformanceRecord {
    private final String matchId;
    private final String playerId;
    private final int goals;
    private final int assists;
    private final int yellowCards;
    private final int redCards;
    private final int minutesPlayed;

    public PerformanceRecord(String matchId, String playerId, int goals, int assists,
                             int yellowCards, int redCards, int minutesPlayed) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.goals = goals;
        this.assists = assists;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.minutesPlayed = minutesPlayed;
    }

    public String getMatchId() {
        return matchId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    public int getYellowCards() {
        return yellowCards;
    }

    public int getRedCards() {
        return redCards;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }

    public int calculatePerformancePoints() {
        int points = goals * 5 + assists * 3 - yellowCards - redCards * 3;
        return Math.max(0, points);
    }
}
