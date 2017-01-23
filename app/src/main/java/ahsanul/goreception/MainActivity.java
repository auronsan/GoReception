package ahsanul.goreception;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ahsanul.goreception.Service.VolleyRequest;

public class MainActivity extends AppCompatActivity {
    String Stoken;
    JSONObject resultjson;
    JSONArray terminal1;
    ArrayList<String> TerminalList = new ArrayList<String>();
    ListView listView;
    String first_name;
    TextView welcome;
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Stoken = getIntent().getStringExtra("SToken");
        setContentView(R.layout.activity_main);
        mProgressView = findViewById(R.id.progressBarMain);
        mProgressView.setVisibility(View.VISIBLE);
        listTerminal();
        listView = (ListView) findViewById(R.id.ListView5);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // Do something after 5s = 5000ms

                mProgressView.setVisibility(View.GONE);
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1,android.R.id.text1,TerminalList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener()  {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        int itemPosition     = position;
                        String  itemValue    = (String) listView.getItemAtPosition(position);
                        finish();
                        Toast.makeText(getBaseContext(), "Welcome to "+itemValue,Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getBaseContext(), TerminalActivity.class);
                        try {
                            String JsonTerminal = terminal1.getJSONObject(itemPosition).toString();
                            intent.putExtra("JsonT", JsonTerminal);
                            intent.putExtra("SToken", Stoken);
                            intent.putExtra("User_id",resultjson.getString("user_id"));

                        }catch(Exception e){
                            Log.d("error!",e.getMessage());
                        }
                        startActivity(intent);
                    }
                });
            }
        }, 5000);
    }

public void listTerminal(){
    String JSON_URL = "https://beta.goreception.co/apiv3/refresh";
    welcome = (TextView) findViewById(R.id.welcome1);
    Map<String, String> params = new HashMap<String, String>();
    params.put("securityToken", Stoken);
    RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
    VolleyRequest jsObjRequest = new VolleyRequest(Request.Method.POST, JSON_URL, params, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try{
                String str = response.toString();
                resultjson = new JSONObject(str);
                first_name = resultjson.getString("first_name");
                Log.d("test",resultjson.toString());
                welcome.setText("Welcome "+first_name+" on Go Reception Android!");
                terminal1 = resultjson.getJSONArray("businesses");
                int lengthTerminal = terminal1.length();
                for(int i=0;i<lengthTerminal;i++){
                    TerminalList.add(terminal1.getJSONObject(i).getString("name"));
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

