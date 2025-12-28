package Rooms;

import java.time.LocalDateTime;
import java.util.List;

public class TreatmentRoom extends Room {

    private String treatmentPlan;
    private List<String> medications;
    private LocalDateTime treatmentStart;
    private LocalDateTime treatmentEnd;

    public void startTreatment() {
        treatmentStart = LocalDateTime.now();
    }

    public void finishTreatment() {
        treatmentEnd = LocalDateTime.now();
    }

}
