package ahsanul.goreception;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignInAcitvity extends AppCompatActivity {
    String FieldsI;
    ListView listView;
    ArrayList<String> TerminalList = new ArrayList<String>();
    JSONArray resultjson;
    int lengthField;
    RelativeLayout rl;
    AutoCompleteTextView FullName;
    AutoCompleteTextView mTextView;
    String induction;
    int idLastChild = 0;
    Button btnnext;
    boolean pass = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_visitor);
        FieldsI = getIntent().getStringExtra("Fields");
        rl = (RelativeLayout) findViewById(R.id.activity_sign_in_visitor);
        ImageButton btnback = (ImageButton) findViewById(R.id.backbutton);
        TerminalList.add("Full Name");
        RelativeLayout.LayoutParams params;
        mTextView = new AutoCompleteTextView(getBaseContext());
        int childCount = rl.getChildCount();
        induction = getIntent().getStringExtra("Induction");
        idLastChild = rl.getChildAt(childCount - 1).getId() + 2;
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.my_toolbar);
        mTextView = new AutoCompleteTextView(getBaseContext());
        mTextView.setHint(TerminalList.get(0));
        mTextView.setHintTextColor(Color.GRAY);
        mTextView.setHighlightColor(Color.BLACK);
        mTextView.setId(idLastChild);
        mTextView.setTextColor(Color.BLACK);
        mTextView.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        rl.addView(mTextView, params);
        btnback.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();

            }

        });
        btnnext = (Button) findViewById(R.id.next55);
        FullName = (AutoCompleteTextView) findViewById(R.id.my_toolbar + 1);
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    resultjson = new JSONArray(FieldsI);
                    lengthField = resultjson.length();
                    Intent intent = new Intent(getBaseContext(), AgreementActivity.class);
                    intent.putExtra("Induction", induction);
                    intent.putExtra("User_id", getIntent().getStringExtra("User_id"));
                    intent.putExtra("business_id", getIntent().getStringExtra("business_id"));
                    intent.putExtra("SToken", getIntent().getStringExtra("SToken"));
                    FullName = (AutoCompleteTextView) findViewById(1);

                    if(FullName.getText().toString().trim().equals("")){
                        FullName.setError("Please Fill Full Name ");
                    }
                    intent.putExtra("Fullname", FullName.getText().toString());
                    for (int i = 0; i < lengthField; i++) {
                        if (i == lengthField - 1) {
                        } else {
                            FullName = (AutoCompleteTextView) findViewById(i + 2);
                            if (FullName.getText().toString().trim().equals("")) {
                                FullName.setError("Please Fill : " + resultjson.getJSONObject(i).getString("name"));
                                pass=false;
                            }else if(FullName.getHint().equals("Email")&& !FullName.getText().toString().contains("@")) {
                               FullName.setError("Please Enter Valid Email Address");
                                pass=false;
                            }
                            else if(FullName.getHint().equals("Phone")&& FullName.getText().length() < 6) {
                                FullName.setError("Please Fill more than 6 character : " + resultjson.getJSONObject(i).getString("name"));
                                pass=false;
                            } else {
                               pass=true;
                            }}}
                       for (int i = 0; i < lengthField; i++) {
                        if (i == lengthField - 1) {
                        } else {
                            if(pass){
                                finish();
                                FullName = (AutoCompleteTextView) findViewById(i + 2);
                                intent.putExtra(resultjson.getJSONObject(i).getString("name"), FullName.getText().toString());
                                Log.d("walah", resultjson.getJSONObject(i).getString("name") + " " + FullName.getText().toString());
                                intent.putExtra("Fields", resultjson.toString());
                                startActivity(intent);
                            }
                        }
                    }
                } catch (Exception e) {
                }

            }

        });


        try {
            resultjson = new JSONArray(FieldsI);
            lengthField = resultjson.length();
            for (int i = 0; i < lengthField; i++) {
                if (resultjson.getJSONObject(i).getString("options") == "null") {
                    TerminalList.add(resultjson.getJSONObject(i).getString("name"));
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, idLastChild);
                    idLastChild = idLastChild + 1;
                    mTextView = new AutoCompleteTextView(getBaseContext());
                    mTextView.setHint(resultjson.getJSONObject(i).getString("name"));
                    mTextView.setHintTextColor(Color.GRAY);
                    mTextView.setHighlightColor(Color.BLACK);
                    mTextView.setId(idLastChild);
                    mTextView.setTextColor(Color.BLACK);
                    mTextView.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    rl.addView(mTextView, params);
                } else {
                    String ab2 = resultjson.getJSONObject(i).getString("options");
                    JSONObject abc = new JSONObject(ab2);
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, idLastChild);
                    idLastChild = idLastChild + 1;
                    TextView opt1 = new TextView((getBaseContext()));
                    opt1.setText(resultjson.getJSONObject(i).getString("name"));
                    opt1.setTextColor(Color.BLACK);
                    opt1.setId(idLastChild);
                    rl.addView(opt1, params);
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.BELOW, idLastChild);
                    idLastChild = idLastChild + 1;
                    Button option1 = new Button(getBaseContext());
                    option1.setText(abc.names().getString(0));
                    option1.setId(idLastChild);
                    option1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getBaseContext(), "Please wait while staff will assist you." ,Toast.LENGTH_LONG).show();
                            btnnext.setEnabled(true);
                        }
                    });
                    rl.addView(option1, params);
                    params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.RIGHT_OF, idLastChild);
                    params.addRule(RelativeLayout.BELOW, idLastChild - 1);
                    idLastChild = idLastChild + 1;
                    Button option2 = new Button(getBaseContext());
                    option2.setText(abc.names().getString(1));
                    option2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getBaseContext(), "Please wait while staff will assist you." ,Toast.LENGTH_LONG).show();
                            btnnext.setEnabled(false);
                        }
                    });
                    rl.addView(option2, params);

                }
            }
        } catch (Exception e) {
            Log.d("walah", e.getMessage());
        }


    }


}


