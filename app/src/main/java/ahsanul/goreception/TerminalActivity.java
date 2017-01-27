package ahsanul.goreception;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import ahsanul.goreception.Service.VolleyRequest;

import static ahsanul.goreception.R.id.slider;
import static android.util.Log.d;

public class TerminalActivity extends AppCompatActivity {
    private int currentPage = 0 ;
    private Timer swipeTimer;
    String Stoken;
    String idTerminal;
    JSONObject sliderjson = null;
    String url = null;
    JSONObject sliderdata;
    JSONObject JsonTerminal = null;
    String TerminalDesc ="";
    String FieldSigin="";
    String Induction="";
    String BusinessID="";
    String urlback;
    int sliderlenght;
    ImageView[] Background1;
    SliderLayout sliderShow;
    int clicked;
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        ImageView LogoI = (ImageView)findViewById(R.id.LogoImage);
        TextView TextDesc = (TextView)findViewById(R.id.Desc);
        String StringTerminal = getIntent().getStringExtra("JsonT");
        Stoken = getIntent().getStringExtra("SToken");

        try{
            JsonTerminal = new JSONObject(StringTerminal);
            url = JsonTerminal.getString("image");
            idTerminal = JsonTerminal.getString("id");
            TerminalDesc = JsonTerminal.getString("description");
            Induction = JsonTerminal.getString("induction");
            FieldSigin = JsonTerminal.getString("fields");
            BusinessID=JsonTerminal.getString("id");
            TextDesc.setText(TerminalDesc);
            terminalslider();
        }catch(Exception e){
            e.getMessage();
        }
        Picasso.with(getBaseContext()).load(url).into(LogoI);
        clicked=1;
        prefManager = new PrefManager(getBaseContext());
        LogoI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clicked==3){
                    prefManager.setToken("DEFAULT");
                    finish();
                    startActivity(new Intent(TerminalActivity.this, LoginActivity.class));
                }else{
                    clicked=clicked+1;

                }
            }
        });

    }

    public void onClick(View v) {


        if(v.getId()==R.id.Checkin){
            d("test5 success",String.valueOf(v.getId()));
            Intent intent = new Intent(getBaseContext(), QRScanActivity.class);
            intent.putExtra("business_id",BusinessID);
            intent.putExtra("SToken",Stoken);
            startActivity(intent);
        }else if(v.getId()==R.id.SignIn){
            Intent intent = new Intent(getBaseContext(), SignInAcitvity.class);
            intent.putExtra("User_id",getIntent().getStringExtra("User_id"));
            intent.putExtra("business_id",BusinessID);
            intent.putExtra("SToken",Stoken);
            intent.putExtra("Induction",Induction);
            intent.putExtra("Fields",FieldSigin);
            startActivity(intent);
        }
        else if(v.getId()==R.id.SignOut){

            Intent intent = new Intent(getBaseContext(), SignOutActivity.class);
            intent.putExtra("User_id",getIntent().getStringExtra("User_id"));
            intent.putExtra("business_id",BusinessID);
            intent.putExtra("SToken",Stoken);
            startActivity(intent);
        }
        else{
            d("test5 fail",String.valueOf(v.getId()));
        }


    }
    public void terminalslider(){
        String JSON_URL = "https://beta.goreception.co/apiv3/getsliders";
        Map<String, String> params = new HashMap<String, String>();
        params.put("securityToken", Stoken);
        params.put("terminal_id", idTerminal);
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        VolleyRequest jsObjRequest = new VolleyRequest(Request.Method.POST, JSON_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String str = response.toString();
                    sliderShow = (SliderLayout) findViewById(slider);
                    sliderjson = new JSONObject(str);
                    sliderdata = sliderjson.getJSONObject("sliders");

                    for(int i = 1;i<=sliderdata.length();i++){
                        JSONObject jsonobject = sliderdata.getJSONObject(String.valueOf(i));
                        String tempurl =jsonobject.getString("image");
                      //  tempurl =tempurl.replace("//","/");
                        DefaultSliderView textSliderView = new DefaultSliderView(getBaseContext());
                        textSliderView
                                .image(String.valueOf(tempurl));
                        sliderShow.addSlider(textSliderView);
                    }



                }catch(Exception e){e.getMessage();}
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("RESPONSE ERROR", "Error: " + error.getMessage());

            }
        });
        requestQueue.add(jsObjRequest);
    }


}
