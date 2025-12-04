package Rooms;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DiagnoseRoom extends Room {

    private String diagnosisNotes;
    private LocalDateTime diagnosisStart;
    private LocalDateTime diagnosisEnd;

    public void startDiagnosis() {
        diagnosisStart = LocalDateTime.now();
    }

    public void recordDiagnosis(String note) {
        diagnosisNotes = note;
    }

    public void finishDiagnosis() {
        diagnosisEnd = LocalDateTime.now();
    }

    public List<String> generateReports() {
        List<String> reports = new ArrayList<>();
        reports.add("Diagnosis start at: " + diagnosisStart.format(DateTimeFormatter.ISO_DATE_TIME));
        reports.add("Diagnosis finish at: " + diagnosisEnd.format(DateTimeFormatter.ISO_DATE_TIME));
        reports.add("Diagnosis notes: " + diagnosisNotes);
        return reports;
    }

}
