package fr.charleslabs.electrodb.componentViews;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;
import java.util.TreeMap;

import fr.charleslabs.electrodb.component.Component;

public class PinoutTable extends TableLayout {

    private Component component;

    public PinoutTable(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setComponent(Component conponent){this.component = conponent;}

    public void draw(){
        TableRow row;
        TextView colNb,colPinName;
        TreeMap<?,String> pins;

        if (component.isPinoutNumeric()) pins = component.getSortedPins();
        else pins = component.getPins();

        for(Map.Entry<?,String> entry:pins.entrySet()) {
            row = new TableRow(getContext());
            row.setGravity(Gravity.CENTER);

            colNb = createCell(entry.getKey().toString());
            colPinName = createCell(entry.getValue());

            row.addView(colNb);
            row.addView(colPinName);

            this.addView(row);
        }
    }

    private TextView createCell(String text){
        int hPad = (int)(getContext().getResources().getDisplayMetrics().density * 3.0);
        int vPad = (int)(getContext().getResources().getDisplayMetrics().density * 1.0);

        TextView cell = new TextView(getContext());
        cell.setGravity(Gravity.CENTER);
        cell.setText(text);
        cell.setPadding(hPad,vPad,hPad,vPad);
        cell.setBackgroundColor(Color.WHITE);
        cell.setBackgroundResource(getContext().getResources()
                .getIdentifier("cell_shape","drawable",getContext().getPackageName()));
        return cell;
    }
}
