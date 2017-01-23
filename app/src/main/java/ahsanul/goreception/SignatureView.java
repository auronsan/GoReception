package ahsanul.goreception;

/**
 * Created by Anshor on 1/10/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class SignatureView extends View {
    Paint p;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p=new Paint();
        Bitmap b=BitmapFactory.decodeResource(getResources(), R.drawable.splash);
        p.setColor(Color.RED);
        canvas.drawBitmap(b, 0, 0, p);
    }

    public SignatureView(Context context) {
        super(context);
    }
}