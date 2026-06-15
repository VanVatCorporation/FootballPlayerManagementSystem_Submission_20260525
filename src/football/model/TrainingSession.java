package football.model;

import java.time.LocalDate;

public class TrainingSession {
    private final String trainingId;
    private final LocalDate date;
    private final String location;
    private final String topic;

    public TrainingSession(String trainingId, LocalDate date, String location, String topic) {
        this.trainingId = trainingId;
        this.date = date;
        this.location = location;
        this.topic = topic;
    }

    public String getTrainingId() {
        return trainingId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getTopic() {
        return topic;
    }
}
