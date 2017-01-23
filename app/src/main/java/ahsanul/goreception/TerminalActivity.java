package ahsanul.goreception;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ahsanul.goreception.Service.VolleyRequest;

import static android.util.Log.d;

public class TerminalActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
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
            Log.d("ResultTerminalslide",StringTerminal);
            Log.d("ResultTerminalslide",Stoken);

            Log.d("ResultTerminalslide",TerminalDesc);

            Log.d("ResultTerminalslide",FieldSigin);
        }catch(Exception e){
            e.getMessage();
        }
        Picasso.with(getBaseContext()).load(url).into(LogoI);
        terminalslider();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager = (ViewPager) findViewById(R.id.view_pager);
               myViewPagerAdapter = new MyViewPagerAdapter(getBaseContext());

                viewPager.setAdapter(myViewPagerAdapter);
                viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

                swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (viewPager.getCurrentItem() == (sliderlenght-1)) {
                                    currentPage = 1;
                                }
                                viewPager.setCurrentItem(currentPage++, true);
                            }
                        });
                    }
                }, 500, 5000);
            }
        }, 5000);
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
                    sliderjson = new JSONObject(str);
                    sliderdata = sliderjson.getJSONObject("sliders");
                    sliderlenght =sliderdata.length()-1;
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
    public class MyViewPagerAdapter extends PagerAdapter {
        Context mContext;
        LayoutInflater mLayoutInflater;
        private boolean doNotifyDataSetChangedOnce = false;
        public MyViewPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sliderlenght;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
                notifyDataSetChanged();
                View itemView = mLayoutInflater.inflate(R.layout.backgroundscreen, container, false);
                String url1 = "";
                try {
                    JSONObject sliderdata5 = sliderdata.getJSONObject(Integer.toString(position));
                    url1 = sliderdata5.getString("image");
                    ImageView imageView = (ImageView) itemView.findViewById(R.id.ImageScreen);
                    Picasso.with(getBaseContext()).load(url1).into(imageView);
                } catch (Exception e) {
                    notifyDataSetChanged();
                    e.getMessage();
                }
                container.addView(itemView);
                return itemView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {

             container.removeView((RelativeLayout) object);
        }
    }
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {


            // changing the next button text 'NEXT' / 'GOT IT'

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class ImageLogo extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
        private int position;

        public ImageLogo(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }


        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {

                // Simulate network access.
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;

            }catch (Exception e){
                System.out.println(e.getMessage());

            }
            return null;


        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

        @Override
        protected void onCancelled() {

        }

    }

}
