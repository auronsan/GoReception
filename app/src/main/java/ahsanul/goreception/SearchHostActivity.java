package ahsanul.goreception;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ahsanul.goreception.Service.VolleyRequest;

public class SearchHostActivity extends AppCompatActivity {
    String SToken;
    String BusinessID;
    String UserID;
    JSONObject resultjson;
    JSONObject resultjsonC;
    JSONArray userCheck;
    JSONArray resultjson1;
    ArrayList<String> HostListID = new ArrayList<String>();
    ArrayList<String> HostListName = new ArrayList<String>();
    ArrayList<String> temp;
    ArrayList<String> tempID;
    RelativeLayout rl;
    ListView listView;
    private View mProgressView;
    TextView notfound;
    int itemPosition;
    View view2;
    boolean success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        rl = (RelativeLayout) findViewById(R.id.activity_sign_ou);
        notfound = (TextView) findViewById(R.id.textView2);
        notfound.setVisibility(View.GONE);
        mProgressView = findViewById(R.id.progressBarSignout);
        mProgressView.setVisibility(View.VISIBLE);
        listHost();
        listView = (ListView) findViewById(R.id.listview6);

        android.support.v7.widget.SearchView abc = (android.support.v7.widget.SearchView) findViewById(R.id.menu_search);
        abc.setOnQueryTextListener(
                new android.support.v7.widget.SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        HostListName = new ArrayList<String>();
                        HostListID = new ArrayList<String>();
                        for (int i = 0; i < temp.size(); i++) {
                            if (temp.get(i).contains(newText)) {
                                HostListName.add(temp.get(i));
                                HostListID.add(tempID.get(i));
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, HostListName);


                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                itemPosition = position;
                                String itemValue = (String) listView.getItemAtPosition(position);
                                view2 = view;
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyAlertDialogStyle);
                                builder.setTitle("SearchHostActivity Checkin :" + HostListName.get(itemPosition));
                                builder.setMessage("Are you really want to checkin?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            checkin("dummyvisitor", HostListID.get(itemPosition));
                                            Log.d("tes33", resultjson.toString());
                                            finish();
                                        } catch (Exception e) {
                                            Toast.makeText(getBaseContext(), "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                builder.setNegativeButton("Cancel", null);
                                builder.show();
                                ;
                            }
                        });

                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String text) {
                        Log.d("goti", "submit :" + text);
                        return true;
                    }
                });

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                temp = HostListName;
                tempID = HostListID;
                mProgressView.setVisibility(View.GONE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, HostListName);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        itemPosition = position;
                        String itemValue = (String) listView.getItemAtPosition(position);
                        view2 = view;
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyAlertDialogStyle);
                        builder.setTitle("SearchHostActivity Checkin : " + HostListName.get(itemPosition));
                        builder.setMessage("Are you really want to checkin?");

                        RequestFuture<String> future = RequestFuture.newFuture();
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    checkin("dummyvisitor", HostListID.get(itemPosition));
                                    Log.d("tes33", resultjson.toString());
                                    finish();

                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), "Error! " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", null);
                        builder.show();
                        ;
                    }
                });

            }
        }, 5000);


    }

public void listHost(){
    BusinessID = getIntent().getStringExtra("business_id");
    UserID = getIntent().getStringExtra("User_id");
    SToken = getIntent().getStringExtra("SToken");
    String JSON_URL = "https://beta.goreception.co/apiv3/searchhostbyterminal??user_id=" + UserID + "&securityToken=" + SToken + "&business_id=" + BusinessID + "&type=Staff";
    Log.d("goti", UserID + " " + SToken + " " + BusinessID);
    RequestQueue requestQueue = Volley.newRequestQueue(this);
    StringRequest jsObjRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                resultjson = new JSONObject(response);
                userCheck = resultjson.getJSONArray("data");
                for (int i = 0; i < userCheck.length(); i++) {
                    resultjson = userCheck.getJSONObject(i);
                    HostListID.add(resultjson.getString("id"));
                    HostListName.add(resultjson.getString("first_name"));
                    Log.d("goti", HostListName.get(i));
                }
                temp = HostListName;
                tempID = HostListID;
            } catch (Exception e) {
                Log.d("goti", e.getMessage());
                mProgressView.setVisibility(View.GONE);
                notfound.setVisibility(View.VISIBLE);
            }


        }
    }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.d("RESPONSE ERROR", "Error: " + error.getMessage());

        }
    });
    requestQueue.add(jsObjRequest);
}
    public void checkin(String visitors, String meetings) {
        String JSON_URL = "https://beta.goreception.co/apiv3/officeCheckin";
        Map<String, String> params = new HashMap<String, String>();
        String visitorsdum = "[{\"full_name\":\"" + getIntent().getStringExtra("Fullname") + "\",\"";
        try {
            resultjson1 = new JSONArray(getIntent().getStringExtra("Fields"));
            int lengthField = resultjson1.length();
            for (int i = 0; i < lengthField; i++) {
                if (i == 1) {
                    visitorsdum += "\"mobile\":\"" + getIntent().getStringExtra(resultjson1.getJSONObject(i).getString("name")) + "\",";
                    Log.d("walah host", resultjson1.getJSONObject(i).getString("name") + " " + getIntent().getStringExtra(resultjson1.getJSONObject(i).getString("name")));
                } else if (i == lengthField - 1) {
                    //visitorsdum +="\""+ resultjson1.getJSONObject(i).getString("name").toLowerCase()+"\":\""+getIntent().getStringExtra(resultjson1.getJSONObject(i).getString("name"))+"\"";
                    // Log.d("walah host",resultjson1.getJSONObject(i).getString("name")+" "+getIntent().getStringExtra(resultjson1.getJSONObject(i).getString("name")));
                } else {
                    visitorsdum += "\"" + resultjson1.getJSONObject(i).getString("name").toLowerCase() + "\":\"" + getIntent().getStringExtra(resultjson1.getJSONObject(i).getString("name")) + "\",";
                    Log.d("walah host", resultjson1.getJSONObject(i).getString("name") + " " + getIntent().getStringExtra(resultjson1.getJSONObject(i).getString("name")));
                }
            }
        } catch (Exception e) {
            Log.d("walah", e.getMessage());
        }
        visitorsdum += "\"custom_fields\":{\"cus\":0, \"cus1\":1}}]";
        Log.d("walah dadi", visitorsdum);
        //String visitorsdum1 = "[{\"email\":\"auronsanjr@gmail.com\",\"full_name\":\""+getIntent().getStringExtra("Fullname")+"\",\"mobile\":\"+841204439008\",\"company\":\"SPS\",\"custom_fields\":{\"cus\":0, \"cus1\":1}}]";
        visitorsdum = "[{\"email\":\"" + getIntent().getStringExtra("Email") + "\",\"full_name\":\"" + getIntent().getStringExtra("Fullname") + "\",\"mobile\":\"" + getIntent().getStringExtra("Phone") + "\",\"company\":\"" + getIntent().getStringExtra("Company") + "\",\"custom_fields\":{\"cus\":0, \"cus1\":1}}]";
        Log.d("walah dadi2", visitorsdum);
        params.put("business_id", BusinessID);
        params.put("meetings", meetings);
        params.put("securityToken", SToken);
        params.put("terminal_id", BusinessID);
        params.put("type", "signin");
        params.put("visitors", visitorsdum);
        Log.d("goti", SToken + " " + BusinessID);
        JSONObject abc = new JSONObject(params);
        Log.d("goti", abc.toString());
        success = false;
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        VolleyRequest jsObjRequest = new VolleyRequest(Request.Method.POST, JSON_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String str = response.toString();
                    resultjson = new JSONObject(str);
                    Log.d("goti", resultjson.toString());
                    if (resultjson.getString("status").equals("fail")) {
                        Toast.makeText(getBaseContext(), "failed to sign in" + resultjson.getString("message").toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Success checkin for " + getIntent().getStringExtra("Fullname"), Toast.LENGTH_LONG).show();
                        success = true;
                    }
                } catch (Exception e) {
                    Log.d("goti", e.getMessage());


                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("test", error.getMessage());

            }
        });
        requestQueue.add(jsObjRequest);


    }
}
