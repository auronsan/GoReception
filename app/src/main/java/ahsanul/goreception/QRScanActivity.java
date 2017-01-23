package ahsanul.goreception;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.scandit.barcodepicker.BarcodePicker;
import com.scandit.barcodepicker.OnScanListener;
import com.scandit.barcodepicker.ScanSession;
import com.scandit.barcodepicker.ScanSettings;
import com.scandit.barcodepicker.ScanditLicense;
import com.scandit.recognition.Barcode;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ahsanul.goreception.Service.VolleyRequest;

/**
 * Created by Anshor on 1/10/2017.
 */
public class QRScanActivity extends Activity implements OnScanListener {
    private BarcodePicker mPicker;
    Toast mToast = null;
    JSONObject resultjson;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanditLicense.setAppKey("kZ2cIZfWleNui2jxSctoYW1VQjWc+5NPurss5oDJ2RY");
        ScanSettings settings = ScanSettings.create();
        settings.setSymbologyEnabled(Barcode.SYMBOLOGY_QR, true);
        settings.setCameraFacingPreference(ScanSettings.CAMERA_FACING_FRONT);
                // Instantiate the barcode picker by using the settings defined above.
        mPicker = new BarcodePicker(this, settings);
// Set the on scan listener to receive barcode scan events.
        mPicker.setOnScanListener(this);
        setContentView(mPicker);
    }
    @Override
    public void didScan(ScanSession session) {
        String message = "";
        for (Barcode code : session.getNewlyRecognizedCodes()) {
            String data = code.getData();
            message = data;
            Log.d("goti",message);
            scanQR(message);
            finish();
        }
        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    protected void onResume() {
        mPicker.startScanning();
        super.onResume();
    }
    @Override
    protected void onPause() {
        mPicker.stopScanning();
        super.onPause();
    }

    public void scanQR(String data) {
        String JSON_URL = "https://beta.goreception.co/apiv3/validateScan";
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", data);
        Log.d("goti1",data);
        params.put("securityToken",getIntent().getStringExtra("SToken"));
        params.put("terminal_id", getIntent().getStringExtra("business_id"));
        params.put("business_id",getIntent().getStringExtra("business_id"));
        Log.d("goti1",params.toString());
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        VolleyRequest jsObjRequest = new VolleyRequest(Request.Method.POST, JSON_URL, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    String str = response.toString();
                    resultjson = new JSONObject(str);
                    if(resultjson.getString("status").equals("success")){
                        String action = resultjson.getString("action");
                        resultjson = resultjson.getJSONObject("user");
                    Toast.makeText(getBaseContext(), "Success to "+action+" for " + resultjson.getString("full_name").toString(), Toast.LENGTH_LONG).show();}
                    else{
                        Toast.makeText(getBaseContext(), "failed to sign in" + resultjson.toString(), Toast.LENGTH_LONG).show();
                }
                    finish();
                }catch(Exception e){
                    Log.d("goti",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("goti", "Error: " + error.getMessage());

            }
        });
        requestQueue.add(jsObjRequest);
    }
}
