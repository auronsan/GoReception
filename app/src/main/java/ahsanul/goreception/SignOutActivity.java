package ahsanul.goreception;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ahsanul.goreception.Service.VolleyRequest;


public class SignOutActivity extends AppCompatActivity {

    String SToken;
    String BusinessID;
    String UserID;
    JSONObject resultjson;
    JSONObject userCheck;
    ArrayList<String> VisitorListID = new ArrayList<String>();
    ArrayList<String> VisitorListName = new ArrayList<String>();
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
        setContentView(R.layout.activity_sign_ou);
        rl = (RelativeLayout) findViewById(R.id.activity_sign_ou);
        notfound = (TextView) findViewById(R.id.textView2);
        notfound.setVisibility(View.GONE);
        mProgressView = findViewById(R.id.progressBarSignout);
        mProgressView.setVisibility(View.VISIBLE);
        getListVisitor();
        listView = (ListView) findViewById(R.id.listview6);
        android.support.v7.widget.SearchView abc = (android.support.v7.widget.SearchView) findViewById(R.id.menu_search);
        abc.setOnQueryTextListener(
                new android.support.v7.widget.SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        VisitorListName = new ArrayList<String>();
                        VisitorListID = new ArrayList<String>();
                        for (int i = 0; i < temp.size(); i++) {
                            if (temp.get(i).contains(newText)) {
                                VisitorListName.add(temp.get(i));
                                VisitorListID.add(tempID.get(i));
                            }
                        }

                        toContinue();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String text) {
                        Log.d("goti", "submit :" + text);
                        return true;
                    }
                });
    }

    public void getListVisitor() {


        BusinessID = getIntent().getStringExtra("business_id");
        UserID = getIntent().getStringExtra("User_id");
        SToken = getIntent().getStringExtra("SToken");
        String JSON_URL = "https://beta.goreception.co/apiv3/checkinsearch?business_id=" + BusinessID + "&user_id=" + UserID + "&securityToken=" + SToken;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest jsObjRequest = new StringRequest(Request.Method.GET, JSON_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    resultjson = new JSONObject(response);
                    userCheck = resultjson.getJSONObject("data");
                    for (int i = 0; i < userCheck.names().length(); i++) {
                        VisitorListName.add(userCheck.getJSONObject(userCheck.names().getString(i)).getString("full_name"));
                        VisitorListID.add(userCheck.getJSONObject(userCheck.names().getString(i)).getString("checkin_id"));
                    }
                    temp = VisitorListName;
                    tempID = VisitorListID;
                    toContinue();
                } catch (Exception e) {
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

    public void checkout(String sToken, String checkin_id, String terminal_id) {
        String JSON_URL = "https://beta.goreception.co/apiv3/officeCheckout";
        Map<String, String> params = new HashMap<String, String>();
        params.put("securityToken", sToken);
        params.put("id", checkin_id);
        params.put("place_id", terminal_id);
        success = false;
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        VolleyRequest jsObjRequest = new VolleyRequest(Request.Method.POST, JSON_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String str = response.toString();
                    resultjson = new JSONObject(str);
                    success = true;
                } catch (Exception e) {

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


    public void toContinue() {
        mProgressView.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, VisitorListName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemPosition = position;
                String itemValue = (String) listView.getItemAtPosition(position);
                view2 = view;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.MyAlertDialogStyle);
                builder.setTitle("Checkout " + VisitorListName.get(itemPosition));
                builder.setMessage("Are you really want to checkout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkout(SToken, VisitorListID.get(itemPosition), BusinessID);
                        Toast.makeText(getBaseContext(), "Success checkout for " + VisitorListID.get(itemPosition), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }
        });
    }

}