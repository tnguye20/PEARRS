package tnguye20.pearrs;

/**
 * Created by Thang Nguyen on 11/17/2016.
 */

public class Branch {
    public static Class getActivity(String x){
        switch (x){
            case "ThankActivity":
                return ThankActivity.class;
            case "SurveyActivity":
                return SurveyActivity.class;
            case "NegativeActivity":
                return NegativeActivity.class;
            default:
                return LoginActivity.class;
        }
    }
}
