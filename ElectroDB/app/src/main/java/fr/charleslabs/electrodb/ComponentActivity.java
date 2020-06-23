package fr.charleslabs.electrodb;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import fr.charleslabs.electrodb.component.Component;
import fr.charleslabs.electrodb.componentViews.PinoutAutoPicture;
import fr.charleslabs.electrodb.componentViews.PinoutTable;

public class ComponentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component);

        TextView nameView = (TextView) findViewById(R.id.componentActivity_title);
        TextView moreInfoView = (TextView) findViewById(R.id.componentActivity_info);
        Button datasheetButton = (Button) findViewById(R.id.componentActivity_datasheetButton);
        View pinoutViewCard = findViewById(R.id.componentActivity_pinoutCard);
        PinoutTable pinoutTable = (PinoutTable) findViewById(R.id.componentActivity_pinoutTable);
        ImageView pinoutPicture = (ImageView) findViewById(R.id.componentActivity_pinoutPicture);
        View pinoutInfoTextView = findViewById(R.id.componentActivity_pinoutInfoText);
        View appCircuitViewCard = findViewById(R.id.componentActivity_applicationCircuitCard);
        ImageView appCircuitView = (ImageView) findViewById(R.id.componentActivity_applicationCircuit);

        Intent intent = getIntent();
        if (!intent.hasExtra(ResultListActivity.EXTRA_COMPONENT)) finish();
        final Component c = (Component)intent.getSerializableExtra(ResultListActivity.EXTRA_COMPONENT);

        //-----------Generate view-----------
        //Name
        nameView.setText(c.getName());

        //Info
        moreInfoView.setText("");
        if (c.hasDescription()){
            if (moreInfoView.getText().length()>0) moreInfoView.append("\n");
            moreInfoView.append(getText(R.string.componentActivity_componentDescription));
            moreInfoView.append(c.getDescription());
        }
        if (c.hasHousing()){
            if (moreInfoView.getText().length()>0) moreInfoView.append("\n");
            moreInfoView.append(getText(R.string.componentActivity_componentPackage));
            moreInfoView.append(c.getHousing());
        }
        if (c.hasCategory()){
            if (moreInfoView.getText().length()>0) moreInfoView.append("\n");
            moreInfoView.append(getText(R.string.componentActivity_componentCategory));
            moreInfoView.append(c.getCategory());
        }

        //Datasheet button
        if (c.hasDatasheet()){
            datasheetButton.setText(getString(R.string.componentActivity_datasheet));
            datasheetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(c.getDatasheet())));
                }
            });
        }else{
            datasheetButton.setText(getString(R.string.componentActivity_googleDatasheet));
            datasheetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.putExtra(SearchManager.QUERY,c.getName() + " datasheet");
                    startActivity(intent);
                }
            });
        }

        //Pinout (Picture > AutomaticPicture > Table):
        String compNameToIdentifier = c.getName().toLowerCase().replaceAll("[^a-z0-9_]","");
        Bitmap bmp = PinoutAutoPicture.getPinoutBitmap(this,c);
        if (getResources().getIdentifier("pinout_"+compNameToIdentifier,"drawable",getPackageName()) != 0){
            pinoutPicture.setImageResource(getResources()
                    .getIdentifier("pinout_"+compNameToIdentifier,"drawable",getPackageName()));
            pinoutPicture.setVisibility(View.VISIBLE);
        }else if(bmp!=null){
            pinoutPicture.setImageBitmap(bmp);
            pinoutPicture.setVisibility(View.VISIBLE);
            pinoutInfoTextView.setVisibility(View.VISIBLE);
        }else if(c.hasPinout()){
            pinoutTable.setComponent(c);
            pinoutTable.draw();
            pinoutTable.setHorizontalScrollBarEnabled(true);
            pinoutTable.setVisibility(View.VISIBLE);
            pinoutInfoTextView.setVisibility(View.VISIBLE);
        }else{
            pinoutViewCard.setVisibility(View.GONE);
        }

        //Application cicruit (if exists)
        if (getResources().getIdentifier("appcircuit_"+compNameToIdentifier,"drawable",getPackageName()) != 0){
            appCircuitView.setImageResource(getResources()
                    .getIdentifier("appcircuit_"+compNameToIdentifier,"drawable",getPackageName()));
            appCircuitViewCard.setVisibility(View.VISIBLE);
        }
    }
}
