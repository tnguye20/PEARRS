package tnguye20.pearrs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PositiveActivity extends AppCompatActivity {
    private TextView mPositive;
    private int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positive);

        /* Get all the Views */
        mPositive = (TextView)findViewById(R.id.positiveTextView);

        /* Get the intent from the survey */
        Intent intent = getIntent();
        int total = intent.getIntExtra("total", 0);

        if(total < 8){
            String text = getResources().getString(R.string.negative);
            mPositive.setText(text);
        }else{
            String text = getResources().getString(R.string.positive);
            mPositive.setText(text);
        }
    }
}
