package football.model;

public abstract class Player {
    private final String playerId;
    private final String fullName;
    private final int age;
    private final String nationality;
    private Position position;
    private int shirtNumber;
    private long baseSalary;
    private PlayerStatus status;

    protected Player(String playerId, String fullName, int age, String nationality,
                     Position position, int shirtNumber, long baseSalary, PlayerStatus status) {
        this.playerId = playerId;
        this.fullName = fullName;
        this.age = age;
        this.nationality = nationality;
        this.position = position;
        this.shirtNumber = shirtNumber;
        this.baseSalary = baseSalary;
        this.status = status;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public String getNationality() {
        return nationality;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getShirtNumber() {
        return shirtNumber;
    }

    public void setShirtNumber(int shirtNumber) {
        this.shirtNumber = shirtNumber;
    }

    public long getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(long baseSalary) {
        this.baseSalary = baseSalary;
    }

    public PlayerStatus getStatus() {
        return status;
    }

    public void setStatus(PlayerStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return status == PlayerStatus.ACTIVE;
    }

    public abstract String getPlayerType();

    public abstract long calculateBonus(int monthlyPerformancePoints);

    public long calculateMonthlySalary(int monthlyPerformancePoints) {
        return baseSalary + calculateBonus(monthlyPerformancePoints);
    }
}
