package fr.charleslabs.electrodb.componentViews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.Objects;
import java.util.TreeMap;
import fr.charleslabs.electrodb.R;
import fr.charleslabs.electrodb.component.Component;

public class PinoutAutoPicture {

    public static Bitmap getPinoutBitmap(Context context, Component component){
        // Sort pins and cast (return if pins is not numeric)
        final TreeMap<Integer,String> pins = component.getSortedPins();
        if(pins == null) return null;

        // Get pin count
        if(!component.hasPinout()) return null;
        final int nbPins = component.getPinCount();

        // Compute bitmap to generate
        try {
            switch (component.getHousingType()) {
                case Component.packageDIP:
                case Component.packageSO:
                case Component.packageDFN:
                    return generateDipSoChip(context, pins, nbPins, component.getHousingType());
                case Component.packageTO220:
                    return generateTO220(context, pins, nbPins);
                case Component.packageSOT223:
                case Component.packageSOT89:
                case Component.packageTO92:
                case Component.packageTO:
                case Component.packageTO252:
                case Component.packageTO263:
                    return generate3PinsChip(context, pins, nbPins, component.getHousingType());
                case Component.packageSOT23:
                    return generateSOTChip(context, pins, nbPins);
                case Component.packageMELF:
                case Component.packageSMA:
                case Component.packageSMB:
                case Component.packageSMC:
                case Component.packageSOD:
                    return generate2PinsChip(context, pins, nbPins, component.getHousingType());
                case Component.packageQFP:
                case Component.packageQFN:
                    return generateQFP(context, pins, nbPins, component.getHousingType());
                default:
                    return null;
            }
        }
        catch (Exception e){
            return null;
        }
    }

    private static Bitmap generateQFP(Context context, TreeMap<Integer,String> pins, int nbPins, String pkg) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        if(nbPins % 4 != 0)
            nbPins --;
        if(nbPins%4 != 0 || nbPins < 8)
            return null;

        final int pinX = nbPins / 4, pinY = nbPins/4;

        // Create the paint
        final int maxFontSize = (int)(25*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 12;
        Paint paint = new Paint();
        final int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);

        //Load resources
        final Bitmap autopinout_qfp_corner1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_corner1,options);
        final Bitmap autopinout_qfp_corner2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_corner2,options);
        final Bitmap autopinout_qfp_corner3 = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_corner3,options);
        final Bitmap autopinout_qfp_corner4 = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_corner4,options);

        final Bitmap autopinout_qfp_edge_bot = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_edge_bot,options);
        final Bitmap autopinout_qfp_edge_top = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_edge_top,options);
        final Bitmap autopinout_qfp_edge_left = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_edge_left,options);
        final Bitmap autopinout_qfp_edge_right = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_edge_right,options);

        final Bitmap autopinout_qfp_mid = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_mid,options);

        final Bitmap autopinout_qfp_pin_h = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_pin_h,options);
        final Bitmap autopinout_qfp_pin_v = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_qfp_pin_v,options);

        final int bmpWidth = autopinout_qfp_mid.getWidth(), bmpHeight = autopinout_qfp_mid.getHeight();

        // Init. bitmap
        final Bitmap result = Bitmap.createBitmap((pinX+2)*bmpWidth+maxTextWidth*2, (pinY+2)*bmpHeight+maxTextWidth*2, Bitmap.Config.ARGB_8888);
        System.out.println(result.getWidth());
        Canvas canvas = new Canvas(result);

        //---------Draw chip----------
        //Generate chip
        for (int i = 0; i<(pinX+2);i++){
            for (int j = 0; j<(pinY+2);j++){
                int posX = maxTextWidth + i*bmpWidth;
                int posY = maxTextWidth + j*bmpHeight;
                Bitmap bmp;

                if ((i==0||i==pinX+1) && (j==0||j==pinY+1))
                    continue;
                else if (i==1 && j==1)
                    bmp = autopinout_qfp_corner1;
                else if (i==pinX && j==1)
                    bmp = autopinout_qfp_corner2;
                else if (i==pinX && j==pinY)
                    bmp = autopinout_qfp_corner3;
                else if (i==1 && j==pinY)
                    bmp = autopinout_qfp_corner4;
                else if (j==0 || j==pinY+1)
                    bmp = autopinout_qfp_pin_v;
                else if (i==0 || i==pinX+1)
                    bmp = autopinout_qfp_pin_h;
                else if (j==1)
                    bmp = autopinout_qfp_edge_top;
                else if (j==pinY)
                    bmp = autopinout_qfp_edge_bot;
                else if (i==1)
                    bmp = autopinout_qfp_edge_left;
                else if (i==pinX)
                    bmp = autopinout_qfp_edge_right;
                else
                    bmp = autopinout_qfp_mid;

                canvas.drawBitmap(bmp, null, new Rect(posX, posY,  posX+bmpWidth, posY+bmpHeight), paint);
            }
        }

        //Draw pins numbers
        Rect bounds = new Rect();
        paint.getTextBounds("C",0,1,bounds);
        paint.setColor(Color.WHITE);

        // TOP & BOT
        paint.setTextAlign(Paint.Align.CENTER);
        for (int i=1;i<=pinX;i++)
            canvas.drawText(Integer.toString(i), maxTextWidth+(i+0.5f)*bmpWidth, maxTextWidth+bmpHeight*0.5f, paint);
        for (int i=1;i<=pinX;i++)
            canvas.drawText(Integer.toString(i+pinY+pinX), maxTextWidth+(0.5f+1+pinX-i)*bmpWidth, maxTextWidth + (pinY+1+0.5f)*bmpHeight, paint);
        // RIGHT
        paint.setTextAlign(Paint.Align.RIGHT);
        for (int i=1;i<=pinY;i++)
            canvas.drawText(Integer.toString(i+pinX), maxTextWidth+(pinX+1+0.9f)*bmpWidth, maxTextWidth+bmpHeight*(i + 0.5f)+bounds.height()*0.5f, paint);
        // LEFT
        paint.setTextAlign(Paint.Align.LEFT);
        for (int i=1;i<=pinY;i++)
            canvas.drawText(Integer.toString(i+pinY+pinX*2), maxTextWidth+(0.1f)*bmpWidth, maxTextWidth+bmpHeight*(1+pinY-i + 0.5f)+bounds.height()*0.5f, paint);

        //Write pin names
        paint.setColor(Color.BLACK);
        float textX,textY,textRot;
        int fontHeight;
        for(int i=1;i<=nbPins;i++){
            if(pins.containsKey(i)){
                //Adapt text size (add one char to account for padding)
                fontHeight = adaptTextSize(paint,pins.get(i)+" ",maxTextWidth,maxFontSize);

                //Calculate text coordinates
                if (i>pinX && i<=pinX+pinY) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    textX = maxTextWidth + (2+pinX)*bmpWidth + paint.measureText(" ")*0.5f;
                    textY = maxTextWidth + bmpHeight*(i-pinX + 0.5f) + fontHeight*0.5f;
                    textRot=0;
                } else if (i>pinY+pinX*2) {
                    paint.setTextAlign(Paint.Align.RIGHT);
                    textX = maxTextWidth - paint.measureText(" ")*0.5f;
                    textY = maxTextWidth + bmpHeight*(1+pinX-(i-pinY-pinX*2) + 0.5f) + fontHeight*0.5f;
                    textRot=0;
                } else if (i<=pinX) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    textRot=-90;
                    textX = maxTextWidth + (i+0.5f)*bmpWidth + fontHeight*0.5f;
                    textY = maxTextWidth - paint.measureText(" ")*0.5f;
                } else {
                    paint.setTextAlign(Paint.Align.RIGHT);
                    textX = maxTextWidth+(0.5f+1+pinX-(i-pinY-pinX))*bmpWidth+ fontHeight*0.5f;
                    textY = maxTextWidth + (pinY+2)*bmpHeight + paint.measureText(" ")*0.5f;
                    textRot=-90;
                }

                //Write text
                if (textRot !=0){
                    canvas.save();
                    canvas.rotate(textRot, textX, textY);
                    canvas.drawText(pins.get(i), textX, textY, paint);
                    canvas.restore();
                }
                else
                    canvas.drawText(pins.get(i), textX, textY, paint);

            }
        }
        return result;
    }

    private static Bitmap generate2PinsChip(Context context, TreeMap<Integer,String> pins,int nbPins, String pkg) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        if (nbPins > 2)
            return null;

        Bitmap bmp;
        switch (pkg){
            case Component.packageMELF:
            case Component.packageSMA:
            case Component.packageSMB:
            case Component.packageSMC:
            case Component.packageSOD:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_diode, options); break;
            default:
                return null;
        }
        int bmpWidth = bmp.getWidth(), bmpHeight = bmp.getHeight();

        //Create the paint
        int maxFontSize = (int) (20 * context.getResources().getDisplayMetrics().scaledDensity), nbChar = 10;
        Paint paint = new Paint();
        int maxTextWidth = (int) setPaint(paint, maxFontSize, nbChar);
        int fontHeight;

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth + maxTextWidth*2, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, null, new Rect(maxTextWidth, 0, maxTextWidth + bmpWidth, bmpHeight), paint);

        //Write pin names
        float textX,textY;
        if (pins.containsKey(1)){
            //Adapt text size (add one char to account for padding)
            fontHeight = adaptTextSize(paint,pins.get(1)+" ",maxTextWidth,maxFontSize);
            paint.setTextAlign(Paint.Align.RIGHT);
            textX = maxTextWidth - paint.measureText(" ")*0.5f;
            textY = bmpHeight/2 + fontHeight/2;
            canvas.drawText(pins.get(1), textX, textY, paint);
        }
        if (pins.containsKey(2)){
            //Adapt text size (add one char to account for padding)
            fontHeight = adaptTextSize(paint,pins.get(2)+" ",maxTextWidth,maxFontSize);
            paint.setTextAlign(Paint.Align.LEFT);
            textX = maxTextWidth + bmpWidth + paint.measureText(" ")*0.5f;
            textY = bmpHeight/2 + fontHeight/2;
            canvas.drawText(pins.get(2), textX, textY, paint);
        }

        return result;
    }

    private static Bitmap generate3PinsChip(Context context, TreeMap<Integer,String> pins,int nbPins, String pkg) {
        if (nbPins > 3)
            return null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bmp;
        switch (pkg){
            case Component.packageTO92:
            case Component.packageTO:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_to92, options); break;
            case Component.packageSOT223:
            case Component.packageSOT89:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sot223, options); break;
            case Component.packageTO252:
            case Component.packageTO263:
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_dpak, options); break;
            default:
                return null;
        }
        int bmpWidth = bmp.getWidth(), bmpHeight = bmp.getHeight();

        //Create the paint
        int maxFontSize = (int) (15 * context.getResources().getDisplayMetrics().scaledDensity), nbChar = 10;
        Paint paint = new Paint();
        int maxTextWidth = (int) setPaint(paint, maxFontSize, nbChar);
        int fontHeight;

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth + maxTextWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, null, new Rect(0, 0,  bmpWidth, bmpHeight), paint);

        //Write pin names
        for (int i = 1; i <= 3; i++) {
            if (pins.containsKey(i)) {
                //Adapt text size (add one char to account for padding)
                fontHeight = adaptTextSize(paint, pins.get(i) + " ", maxTextWidth, maxFontSize);

                canvas.drawText(pins.get(i), bmpWidth + paint.measureText(" ") * 0.5f,
                        bmpHeight - i * bmpHeight * 0.25f + fontHeight * 0.5f, paint);
            }
        }
        return result;
    }

    private static Bitmap generateSOTChip(Context context, TreeMap<Integer,String> pins,int nbPins) {
        if (nbPins <= 3)
            return generateSOT233Chip(context,pins);
        else if (nbPins > 6)
            return null;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap bmp;
        if (nbPins <= 5)
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sot23_5, options);
        else
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sot23_6, options);
        int bmpWidth = bmp.getWidth(), bmpHeight = bmp.getHeight();

        // Create the paint
        int maxFontSize = (int)(15*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 10;
        Paint paint = new Paint();
        int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth*2, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        //---------Draw chip----------
        //Generate chip
        canvas.drawBitmap(bmp, null, new Rect(maxTextWidth, 0,  maxTextWidth+bmpWidth, bmpHeight), paint);

        int fontHeight;

        //Write pin names
        paint.setColor(Color.BLACK);
        float textX,textY;
        for(int i=1;i<=nbPins;i++){
            if(pins.containsKey(i)){
                //Adapt text size (add one char to account for padding)
                fontHeight = adaptTextSize(paint,pins.get(i)+" ",maxTextWidth,maxFontSize);

                //Calculate text coordinates
                if (i>3) {
                    paint.setTextAlign(Paint.Align.LEFT);
                    textX = maxTextWidth + bmpWidth + paint.measureText(" ")*0.5f;
                    textY = bmpHeight - (i-3) * bmpHeight * 0.25f + fontHeight * 0.5f;
                    if (i == 5 && nbPins <= 5)
                        textY -= bmpHeight * 0.25f;
                } else {
                    paint.setTextAlign(Paint.Align.RIGHT);
                    textX = maxTextWidth - paint.measureText(" ")*0.5f;
                    textY = i * bmpHeight * 0.25f + fontHeight * 0.5f;
                }

                //Write text
                canvas.drawText(pins.get(i), textX, textY, paint);
            }
        }
        return result;
    }

    private static Bitmap generateSOT233Chip(Context context, TreeMap<Integer,String> pins) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        //Load resource
        final Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sot23, options);
        final int bmpWidth = bmp.getWidth(), bmpHeight = bmp.getHeight();

        //Create the paint
        final int maxFontSize = (int)(15*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 10;
        final Paint paint = new Paint();
        int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth*2, (int)(bmpHeight+maxFontSize*2+maxFontSize*0.1f), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bmp, null, new Rect(maxTextWidth, maxFontSize,  maxTextWidth+bmpWidth, maxFontSize+bmpHeight), paint);

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

    private static Bitmap generateDipSoChip(Context context, TreeMap<Integer,String> pins, int nbPins, String pkg) {
        //---------Initialize resources----------
        if (nbPins%2 != 0)
            nbPins --;
        // Round up to even number
        nbPins += nbPins%2;
        if(nbPins<4) return null;

        // Create the paint
        int maxFontSize = (int)(14*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 12;
        Paint paint=new Paint();
        int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);
        int fontHeight;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        //Load resources
        Bitmap bmpTop,bmpMid,bmpBot;
        if (pkg.equals(Component.packageDIP)){
            bmpTop= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_diptop, options);
            bmpMid= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_dipmid, options);
            bmpBot= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_dipbot, options);
        }else{
            bmpTop= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sotop, options);
            bmpMid= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_somid, options);
            bmpBot= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_sobot, options);
        }
        int bmpWidth = bmpMid.getWidth(), bmpHeight = bmpMid.getHeight();

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth*2, bmpHeight*nbPins/2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        //---------Draw chip----------
        //Generate chip
        canvas.drawBitmap(bmpTop, null, new Rect(maxTextWidth, 0,  maxTextWidth+bmpWidth, bmpHeight), paint);
        for (int i=0;i<(nbPins-4)/2;i++)
            canvas.drawBitmap(bmpMid, null, new Rect(maxTextWidth, bmpHeight*(i+1),  maxTextWidth+bmpWidth, bmpHeight*(i+2)), paint);
        canvas.drawBitmap(bmpBot, null, new Rect(maxTextWidth, canvas.getHeight()-bmpHeight,  maxTextWidth+bmpWidth, canvas.getHeight()), paint);

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

    private static Bitmap generateTO220(Context context, TreeMap<Integer,String> pins, int nbPins) {
        //---------Initialize resources----------
        // Round up to *odd* number
        nbPins += 1 - nbPins%2;
        if(nbPins<3) return null;

        // Create the paint
        final int maxFontSize = (int)(12*context.getResources().getDisplayMetrics().scaledDensity), nbChar = 12;
        Paint paint = new Paint();
        final int maxTextWidth = (int)setPaint(paint,maxFontSize,nbChar);
        int fontHeight;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        //Load resources
        Bitmap bmpTop= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_to220top, options);
        Bitmap bmpMid= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_to220mid, options);
        Bitmap bmpHole= BitmapFactory.decodeResource(context.getResources(),R.drawable.autopinout_to220hole, options);
        Bitmap bmpBot= BitmapFactory.decodeResource(context.getResources(), R.drawable.autopinout_to220bot, options);
        int bmpWidth = bmpMid.getWidth(), bmpHeight = bmpMid.getHeight();

        // Init. bitmap
        Bitmap result = Bitmap.createBitmap(bmpWidth+maxTextWidth, bmpHeight*nbPins, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        //---------Draw chip----------
        //Generate chip
        canvas.drawBitmap(bmpTop, null, new Rect(0, 0,  bmpWidth, bmpHeight), paint);
        for (int i=0;i<(nbPins-2);i++)
            if (i+1 == (nbPins-1)/2)
                canvas.drawBitmap(bmpHole, null, new Rect(0, bmpHeight*(i+1),  bmpWidth, bmpHeight*(i+2)), paint);
            else
                canvas.drawBitmap(bmpMid, null, new Rect(0, bmpHeight*(i+1),  bmpWidth, bmpHeight*(i+2)), paint);
        canvas.drawBitmap(bmpBot, null, new Rect(0, canvas.getHeight()-bmpHeight,  bmpWidth, canvas.getHeight()), paint);

        //Draw pins numbers
        final Rect bounds = new Rect();
        paint.getTextBounds("C",0,1,bounds);
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.RIGHT);
        for (int i=0;i<=nbPins;i++)
            canvas.drawText(Integer.toString(i), bmpWidth-bounds.width(),
                    nbPins*bmpHeight+bounds.height()*0.5f-(bmpHeight*(i - 0.5f)), paint);

        //Write pin names
        paint.setColor(Color.BLACK);
        float textX,textY;
        for(int i=1;i<=nbPins;i++){
            if(pins.containsKey(i)){
                //Adapt text size (add one char to account for padding)
                fontHeight = adaptTextSize(paint,pins.get(i)+" ",maxTextWidth,maxFontSize);

                //Calculate text coordinates
                paint.setTextAlign(Paint.Align.LEFT);
                textX = bmpWidth + paint.measureText(" ")*0.5f;
                textY = nbPins*bmpHeight - bmpHeight*(i - 0.5f) + fontHeight*0.5f;

                //Write text
                canvas.drawText(pins.get(i), textX, textY, paint);
            }
        }
        return result;
    }

    // Helper Functions:
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
