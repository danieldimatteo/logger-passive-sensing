package ca.utoronto.ece.cimsah.logger.scales.sds;

public interface OnSdsFragmentFinishedListener {
    enum QuestionType {
        WORK,
        SOCIAL_LIFE,
        FAMILY_LIFE,
        DAYS_LOST
        
    }
    void onFinished(QuestionType questionType, SdsResult sdsResults);
}
