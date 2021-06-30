package ca.utoronto.ece.cimsah.logger.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.utoronto.ece.cimsah.logger.scales.ScalePanelActivity;
import ca.utoronto.ece.cimsah.logger.scales.lsas.LsasAnswer;
import ca.utoronto.ece.cimsah.logger.scales.sds.SdsResult;

public class ScaleResult {
    private String uid;
    private Date timestamp;
    private String intakeOrExit;
    private String scaleName;
    private List<Integer> results;

    public ScaleResult() {}

    /**
     * Create scale result entity for Firestore DB from result of a LsasActivity
     * @param timestamp Date that the LSAS was completed
     * @param intakeOrExit Was it the intake or the exit LSAS
     * @param lsasAnswers Results from the LSAS.
     */
    public ScaleResult(Date timestamp, ScalePanelActivity.Type intakeOrExit,
                       ArrayList<LsasAnswer> lsasAnswers) {
        this.timestamp = timestamp;
        if (intakeOrExit == ScalePanelActivity.Type.INTAKE) {
            this.intakeOrExit = "intake";
        } else {
            this.intakeOrExit = "exit";
        }
        this.scaleName = "LSAS";
        this.results = new ArrayList<>();

        // save lsas results into 1-D array as Q1.fear, Q1.avoidance, Q2.fear, Q2.avoidance, etc
        for (LsasAnswer answer : lsasAnswers) {
            this.results.add(answer.fear);
            this.results.add(answer.avoidance);
        }
    }

    /**
     * Create a scale result entity for FirestoreDB from result of a SdsActivity
     * @param timestamp Date that the SDS was completed
     * @param intakeOrExit Was it the intake or the exit SDS
     * @param sdsResult
     */
    public ScaleResult(Date timestamp, ScalePanelActivity.Type intakeOrExit,
                       SdsResult sdsResult) {
        this.timestamp = timestamp;
        if (intakeOrExit == ScalePanelActivity.Type.INTAKE) {
            this.intakeOrExit = "intake";
        } else {
            this.intakeOrExit = "exit";
        }
        this.scaleName = "SDS";
        this.results = new ArrayList<>();
        results.add(sdsResult.getWorkDisruption());
        results.add(sdsResult.getSocialLifeDisruption());
        results.add(sdsResult.getFamilyLifeDisruption());
        results.add(sdsResult.getDaysLost());
        results.add(sdsResult.getDaysUnproductive());
        results.add(sdsResult.getNoWorkUnrelatedToDisorder() ? 1 : 0);
    }

    /**
     * Get a scale result entity for Firestore DV from the result of a SimpleScaleActivity
     * @param timestamp Date that the scale was completed
     * @param intakeOrExit Was it an intake or exit scale
     * @param scaleName Should be "GAD-7" or "PHQ-9"
     * @param answers User's coded responses to the scale
     */
    public ScaleResult(Date timestamp, ScalePanelActivity.Type intakeOrExit,
                       String scaleName, ArrayList<Integer> answers) {
        this.timestamp = timestamp;
        if (intakeOrExit == ScalePanelActivity.Type.INTAKE) {
            this.intakeOrExit = "intake";
        } else {
            this.intakeOrExit = "exit";
        }
        this.scaleName = scaleName;
        this.results = new ArrayList<>(answers);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getIntakeOrExit() {
        return intakeOrExit;
    }

    public void setIntakeOrExit(String intakeOrExit) {
        this.intakeOrExit = intakeOrExit;
    }

    public String getScaleName() {
        return scaleName;
    }

    public void setScaleName(String scaleName) {
        this.scaleName = scaleName;
    }

    public List<Integer> getResults() {
        return results;
    }

    public void setResults(List<Integer> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "ScaleResult{" +
                "uid='" + uid + '\'' +
                ", timestamp=" + timestamp +
                ", intakeOrExit='" + intakeOrExit + '\'' +
                ", scaleName='" + scaleName + '\'' +
                ", results=" + results +
                '}';
    }
}
