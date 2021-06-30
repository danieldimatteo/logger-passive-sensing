package ca.utoronto.ece.cimsah.logger.scales.simple;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import ca.utoronto.ece.cimsah.logger.R;


public class SimpleQuestionFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String ARG_QUESTION_NUM = "arg_question_num";
    private static final String ARG_ANSWER = "arg_answer";
    private static final String ARG_QUESTION = "arg_question";
    private static final String ARG_TOTAL_QUESTIONS = "arg_total_questions";

    private int mQuestionNum;
    private Integer mAnswer;
    private String mQuestion;
    private int mNumberOfQuestions;

    private RadioGroup mRadioGroup = null;

    public SimpleQuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param questionNum Which scale question.
     * @return A new instance of fragment SimpleQuestionFragment.
     */
    public static SimpleQuestionFragment newInstance(int questionNum, String question,
                                                     Integer answer, int numberOfQuestions) {
        SimpleQuestionFragment fragment = new SimpleQuestionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_QUESTION_NUM, questionNum);
        args.putInt(ARG_ANSWER, answer);
        args.putString(ARG_QUESTION, question);
        args.putInt(ARG_TOTAL_QUESTIONS, numberOfQuestions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionNum = getArguments().getInt(ARG_QUESTION_NUM);
            mAnswer = getArguments().getInt(ARG_ANSWER);
            mQuestion = getArguments().getString(ARG_QUESTION);
            mNumberOfQuestions = getArguments().getInt(ARG_TOTAL_QUESTIONS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scale_question, container, false);

        TextView question = view.findViewById(R.id.scale_question_title);
        question.setText(mQuestion);

        String pageNumberText = String.format(getResources().getString(R.string.scale_page_number),
                mQuestionNum, mNumberOfQuestions);
        TextView pageNumber = view.findViewById(R.id.scale_page_number_tv);
        pageNumber.setText(pageNumberText);

        // get radiogroups and pre-populate the answer if it was already answered
        mRadioGroup = view.findViewById(R.id.scale_radiogroup);
        if (!mAnswer.equals(-1)){
            switch (mAnswer) {
                case 0:
                    mRadioGroup.check(R.id.scale_radio_0);
                    break;
                case 1:
                    mRadioGroup.check(R.id.scale_radio_1);
                    break;
                case 2:
                    mRadioGroup.check(R.id.scale_radio_2);
                    break;
                case 3:
                    mRadioGroup.check(R.id.scale_radio_3);
                    break;
            }
            allowNextQuestion();
        }

        mRadioGroup.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        //instanceState.putInt(ARG_QUESTION_NUM, mQuestionNum);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group.getId() == R.id.scale_radiogroup) {
            allowNextQuestion();
        }
    }

    private void allowNextQuestion() {
        ((SimpleScaleActivity)getActivity()).allowNextQuestion();
    }

    public Integer getAnswer() {
        if (mRadioGroup != null) {
            int radioButtonIdId = mRadioGroup.getCheckedRadioButtonId();
            int score = -1;
            switch (radioButtonIdId) {
                case R.id.scale_radio_0:
                    score = 0;
                    break;
                case R.id.scale_radio_1:
                    score = 1;
                    break;
                case R.id.scale_radio_2:
                    score = 2;
                    break;
                case R.id.scale_radio_3:
                    score = 3;
                    break;
            }

            mAnswer = score;
        }
        return mAnswer;
    }
}
