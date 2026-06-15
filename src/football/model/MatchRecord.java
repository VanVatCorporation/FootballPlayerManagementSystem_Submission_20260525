package football.model;

import java.time.LocalDate;

public class MatchRecord {
    private final String matchId;
    private final LocalDate date;
    private final String opponentTeam;
    private final MatchType matchType;

    public MatchRecord(String matchId, LocalDate date, String opponentTeam, MatchType matchType) {
        this.matchId = matchId;
        this.date = date;
        this.opponentTeam = opponentTeam;
        this.matchType = matchType;
    }

    public String getMatchId() {
        return matchId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getOpponentTeam() {
        return opponentTeam;
    }

    public MatchType getMatchType() {
        return matchType;
    }
}
