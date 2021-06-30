package ca.utoronto.ece.cimsah.logger.scales.lsas;

import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import ca.utoronto.ece.cimsah.logger.R;

public class LsasQuestionFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String ARG_QUESTION_NUM = "arg_question_num";
    private static final String ARG_ANSWER = "arg_answer";

    private int mQuestionNum;
    private LsasAnswer mAnswer;
    private Boolean mFearAnswered = false;
    private Boolean mAvoidanceAnswered = false;

    private RadioGroup mFearRadioGroup = null;
    private RadioGroup mAvoidanceRadioGroup = null;

    public LsasQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionNum Which LSAS question.
     * @return A new instance of fragment LsasQuestionFragment.
     */
    public static LsasQuestionFragment newInstance(int questionNum, LsasAnswer answer) {
        LsasQuestionFragment fragment = new LsasQuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_QUESTION_NUM, questionNum);
        args.putParcelable(ARG_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionNum = getArguments().getInt(ARG_QUESTION_NUM);
            mAnswer = getArguments().getParcelable(ARG_ANSWER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lsas_question, container, false);

        // get strings for textviews
        Resources res = getResources();
        String[] questions = res.getStringArray(R.array.lsas_questions_array);
        String[] examples = res.getStringArray(R.array.lsas_examples_array);

        TextView question = view.findViewById(R.id.question_title);
        question.setText(questions[mQuestionNum - 1]);

        TextView example = view.findViewById(R.id.question_example);
        example.setText(examples[mQuestionNum-1]);

        String pageNumberText = String.format(res.getString(R.string.lsas_page_number), mQuestionNum);
        TextView pageNumber = view.findViewById(R.id.page_number_tv);
        pageNumber.setText(pageNumberText);

        // get radiogroups and pre-populate the answer if it was already answered
        mFearRadioGroup = view.findViewById(R.id.radiogroup_fear_selection);
        mAvoidanceRadioGroup = view.findViewById(R.id.radiogroup_avoidance_selection);
        if (!mAnswer.equals(new LsasAnswer(-1,-1))){
            mAvoidanceAnswered = true;
            mFearAnswered = true;
            ((LsasActivity)getActivity()).allowNextQuestion();
            switch (mAnswer.fear) {
                case 0:
                    mFearRadioGroup.check(R.id.radio_fear_none);
                    break;
                case 1:
                    mFearRadioGroup.check(R.id.radio_fear_mild);
                    break;
                case 2:
                    mFearRadioGroup.check(R.id.radio_fear_moderate);
                    break;
                case 3:
                    mFearRadioGroup.check(R.id.radio_fear_severe);
                    break;
            }
            switch (mAnswer.avoidance) {
                case 0:
                    mAvoidanceRadioGroup.check(R.id.radio_avoidance_never);
                    break;
                case 1:
                    mAvoidanceRadioGroup.check(R.id.radio_avoidance_occasionally);
                    break;
                case 2:
                    mAvoidanceRadioGroup.check(R.id.radio_avoidance_often);
                    break;
                case 3:
                    mAvoidanceRadioGroup.check(R.id.radio_avoidance_usually);
                    break;
            }

        }

        mFearRadioGroup.setOnCheckedChangeListener(this);
        mAvoidanceRadioGroup.setOnCheckedChangeListener(this);


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        instanceState.putInt(ARG_QUESTION_NUM, mQuestionNum);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.radiogroup_fear_selection) {
            mFearAnswered = true;
        } else {
            mAvoidanceAnswered = true;
        }

        if (mFearAnswered && mAvoidanceAnswered) {
            // show FAB and allow user to move to next question
            ((LsasActivity)getActivity()).allowNextQuestion();
        }
    }

    public LsasAnswer getAnswer() {
        if (mAvoidanceRadioGroup != null && mFearRadioGroup != null) {
            int fearId = mFearRadioGroup.getCheckedRadioButtonId();
            int fearScore = -1;
            switch (fearId) {
                case R.id.radio_fear_none:
                    fearScore = 0;
                    break;
                case R.id.radio_fear_mild:
                    fearScore = 1;
                    break;
                case R.id.radio_fear_moderate:
                    fearScore = 2;
                    break;
                case R.id.radio_fear_severe:
                    fearScore = 3;
                    break;
            }

            int avoidanceId = mAvoidanceRadioGroup.getCheckedRadioButtonId();
            int avoidanceScore = -1;
            switch (avoidanceId) {
                case R.id.radio_avoidance_never:
                    avoidanceScore = 0;
                    break;
                case R.id.radio_avoidance_occasionally:
                    avoidanceScore = 1;
                    break;
                case R.id.radio_avoidance_often:
                    avoidanceScore = 2;
                    break;
                case R.id.radio_avoidance_usually:
                    avoidanceScore = 3;
                    break;
            }

            mAnswer.fear = fearScore;
            mAnswer.avoidance = avoidanceScore;
        }
        return mAnswer;
    }
}
