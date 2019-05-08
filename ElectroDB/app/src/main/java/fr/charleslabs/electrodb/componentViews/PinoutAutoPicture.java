package fr.charleslabs.electrodb.componentViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import java.util.TreeMap;
import fr.charleslabs.electrodb.R;
import fr.charleslabs.electrodb.component.Component;

public class PinoutAutoPicture {

    public static Bitmap getPinoutBitmap(Context context, Component component){
        // Sort pins and cast (return if pins is not numeric)
        TreeMap<Integer,String> pins = component.getSortedPins();
        if(pins == null) return null;

        // Get pin count
        if(!component.hasPinout()) return null;
        int nbPins = component.getPinCount();

        // Compute bitmap to generate
        if (component.getHousingType().equals(Component.packageDIP))
            return generateDipSoChip(context,pins,nbPins,true);
        else if (component.getHousingType().equals(Component.packageSO))
            return generateDipSoChip(context,pins,nbPins,false);
        else if (component.getHousingType().equals(Component.packageTO220) && nbPins <= 3)
            return generateTOChip(context,pins,true);
        else if ((component.getHousingType().equals(Component.packageTO92) ||
                component.getHousingType().equals(Component.packageTO)) && nbPins <= 3)
            return generateTOChip(context,pins,false);
        else if (component.getHousingType().equals(Component.packageSOT) && nbPins <=3)
            return generateSOTChip(context,pins);
        else
            return null;
    }

    private static Bitmap generateTOChip(Context context, TreeMap<Integer,String> pins,boolean isTO220notTO92) {
        //Load resource
        Bitmap bmp;
        if (isTO220notTO92)
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_to220);
        else
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_to92);
        int bmpWidth = bmp.getWidth(), bmpHeight = bmp.getHeight();

        //Create the paint
        int maxFontSize = (int)(20*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 10;
        Paint paint=new Paint();
        int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);
        int fontHeight;

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp,0,0,paint);

        //Write pin names
        for (int i=1;i<=3;i++){
            if (pins.containsKey(i)){
                //Adapt text size (add one char to account for padding)
                fontHeight = adaptTextSize(paint,pins.get(i)+" ",maxTextWidth,maxFontSize);

                canvas.drawText(pins.get(i), bmpWidth+paint.measureText(" ")*0.5f,
                        bmpHeight-i*bmpHeight*0.25f+fontHeight*0.5f, paint);
            }
        }
        return result;
    }

    private static Bitmap generateSOTChip(Context context, TreeMap<Integer,String> pins) {
        //Load resource
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sot23);
        int bmpWidth = bmp.getWidth(), bmpHeight = bmp.getHeight();

        //Create the paint
        int maxFontSize = (int)(30*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 5;
        Paint paint=new Paint();
        int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth*2, (int)(bmpHeight+maxFontSize*2+maxFontSize*0.1f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp,maxTextWidth,maxFontSize,paint);

        //Write pin names
        if (pins.containsKey(1)){
            paint.setTextAlign(Paint.Align.RIGHT);
            adaptTextSize(paint,pins.get(1),(int)(maxTextWidth+bmpWidth*0.25f),maxFontSize);
            canvas.drawText(pins.get(1), maxTextWidth+bmpWidth*0.25f, result.getHeight(), paint);
        }
        if (pins.containsKey(2)) {
            paint.setTextAlign(Paint.Align.LEFT);
            adaptTextSize(paint,pins.get(2),(int)(maxTextWidth+bmpWidth*0.25f),maxFontSize);
            canvas.drawText(pins.get(2), maxTextWidth+bmpWidth*0.75f, result.getHeight(), paint);
        }
        if (pins.containsKey(3)) {
            paint.setTextAlign(Paint.Align.CENTER);
            adaptTextSize(paint,pins.get(3),bmpWidth,maxFontSize);
            canvas.drawText(pins.get(3), maxTextWidth+bmpWidth*0.5f, maxFontSize-maxFontSize*0.1f, paint);
        }

        return result;
    }

    private static Bitmap generateDipSoChip(Context context, TreeMap<Integer,String> pins, int nbPins,boolean isDIPnotSO) {
        //---------Initialize resources----------
        // Round up to even number
        nbPins += nbPins%2;
        if(nbPins<4) return null;

        // Create the paint
        int maxFontSize = (int)(20*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 19;
        Paint paint=new Paint();
        int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);
        int fontHeight;

        //Load resources
        Bitmap bmpTop,bmpMid,bmpBot;
        if (isDIPnotSO){
            bmpTop= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_diptop);
            bmpMid= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_dipmid);
            bmpBot= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_dipbot);
        }else{
            bmpTop= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sotop);
            bmpMid= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_somid);
            bmpBot= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sobot);
        }
        int bmpWidth = bmpMid.getWidth(), bmpHeight = bmpMid.getHeight();

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth*2, bmpHeight*nbPins/2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        //---------Draw chip----------
        //Generate chip
        canvas.drawBitmap(bmpTop,maxTextWidth,0,paint);
        for (int i=0;i<(nbPins-4)/2;i++)
            canvas.drawBitmap(bmpMid,maxTextWidth,bmpHeight*(i+1),paint);
        canvas.drawBitmap(bmpBot,maxTextWidth,canvas.getHeight()-bmpHeight,paint);

        //Draw pins numbers
        Rect bounds = new Rect();
        paint.getTextBounds("C",0,1,bounds);
        paint.setColor(Color.WHITE);
        for (int i=1;i<=nbPins/2;i++)
            canvas.drawText(Integer.toString(i), maxTextWidth, (bmpHeight*(i - 0.5f))+ bounds.height()*0.5f, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        for (int i=nbPins/2+1;i<=nbPins;i++)
            canvas.drawText(Integer.toString(i), bmpWidth+maxTextWidth,
                    nbPins*bmpHeight*0.5f+bounds.height()*0.5f-(bmpHeight*(i - 0.5f)) % (nbPins/2*bmpHeight), paint);

        //Write pin names
        paint.setColor(Color.BLACK);
        float textX,textY;
        for(int i=1;i<=nbPins;i++){
            if(pins.containsKey(i)){
                //Adapt text size (add one char to account for padding)
                fontHeight = adaptTextSize(paint,pins.get(i)+" ",maxTextWidth,maxFontSize);

                //Calculate text coordinates
                textY = (bmpHeight*(i - 0.5f)) % (nbPins/2*bmpHeight);
                if (i>nbPins/2) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    textX = maxTextWidth + bmpWidth + paint.measureText(" ")*0.5f;
                    textY = nbPins*bmpHeight*0.5f - textY + fontHeight*0.5f;
                } else {
                    paint.setTextAlign(Paint.Align.RIGHT);
                    textX = maxTextWidth - paint.measureText(" ")*0.5f;
                    textY += fontHeight * 0.5f;
                }

                //Write text
                canvas.drawText(pins.get(i), textX, textY, paint);
            }
        }
        return result;
    }

    private static float setPaint(Paint paint,int maxFontSize,int nbChar){
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setAntiAlias(true);
        paint.setTextSize(maxFontSize);
        paint.setTypeface(Typeface.MONOSPACE);

        return paint.measureText("(") * nbChar;
    }

    //Return font height
    private static int adaptTextSize(Paint paint, String text ,int maxTextWidth, int maxFontSize){
        paint.setTextSize(maxFontSize);
        while (paint.measureText(text) >= maxTextWidth)
            paint.setTextSize(paint.getTextSize() - 1);

        return getTextHeight(paint,text);
    }

    private static int getTextHeight(Paint paint,String text){
        Rect bounds = new Rect();
        paint.getTextBounds(text,0,1,bounds);
        return bounds.height();
    }
}
