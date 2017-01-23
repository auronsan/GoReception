package ahsanul.goreception;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;


public class AgreementActivity extends AppCompatActivity{
    String Fullname;

    DrawingView dv ;
    private Paint mPaint;
    RelativeLayout rl;
    JSONArray resultjson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        rl = (RelativeLayout) findViewById(R.id.activity_agreement);
        String induction = getIntent().getStringExtra("Induction");
        Fullname =getIntent().getStringExtra("Fullname");
        induction = induction.replace("(<strong>##FULL_NAME##</strong>)","<strong>"+Fullname+"</strong>");
        TextView Induction = (TextView) findViewById(R.id.AgreementText);
        Induction.setText(Html.fromHtml(induction));
        TextView InductionName= (TextView) findViewById(R.id.textView4);
        InductionName.setText(getIntent().getStringExtra("Fullname").toString()+" / "+getIntent().getStringExtra("Email").toString());
        Button btnback1 = (Button) findViewById(R.id.Decline);
        btnback1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();

            }

        });
        Button btnnext1 = (Button) findViewById(R.id.Next1);
        btnnext1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), TakePictureActivity.class);
                intent.putExtra("User_id",getIntent().getStringExtra("User_id"));
                intent.putExtra("business_id",getIntent().getStringExtra("business_id"));
                intent.putExtra("SToken",getIntent().getStringExtra("SToken"));
                intent.putExtra("Fullname",getIntent().getStringExtra("Fullname"));
                try{
                    resultjson = new JSONArray(getIntent().getStringExtra("Fields"));
                    int lengthField = resultjson.length();
                    for (int i = 0; i < lengthField; i++) {
                       intent.putExtra(resultjson.getJSONObject(i).getString("name"),getIntent().getStringExtra(resultjson.getJSONObject(i).getString("name")));
                        Log.d("walah",resultjson.getJSONObject(i).getString("name")+" "+getIntent().getStringExtra(resultjson.getJSONObject(i).getString("name")));}
                }catch(Exception e){
                    Log.d("walah",e.getMessage());
                }
                intent.putExtra("Fields",resultjson.toString());
                finish();
                startActivity(intent);

            }

        });
        ImageButton signbutton1 = (ImageButton) findViewById(R.id.signbutton);
        signbutton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                dv = new DrawingView(getBaseContext());
                rl.addView(dv, params);
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setDither(true);
                mPaint.setColor(Color.BLACK);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeWidth(12); }

        });

    }


    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath,  mPaint);
            canvas.drawPath( circlePath,  circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
}
