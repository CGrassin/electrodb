package fr.charleslabs.electrodb;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import fr.charleslabs.electrodb.component.ComponentsDB;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_SEARCH_BY_NAME = "fr.charleslabs.electroDB.SEARCH_BY_NAME";
    private EditText searchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init the db (once)
        ComponentsDB.getInstance(getApplicationContext());

        // Set views listeners
        searchField = findViewById(R.id.mainActivity_searchField);
        findViewById(R.id.mainActivity_searchFieldSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchByName();
            }
        });
        findViewById(R.id.mainActivity_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoDialog();
            }
        });
        //Edittext action
        ((EditText) findViewById(R.id.mainActivity_searchField)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return searchByName();
            }
        });

        //Animate keyboard
        ViewGroup layout = findViewById(R.id.mainActivity_form);
        LayoutTransition layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        layout = findViewById(R.id.mainActivity_root);
        layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        layout = findViewById(R.id.mainActivity_header);
        layoutTransition = layout.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
    }

    private boolean searchByName() {
        // Perform search
        String searchTerm = searchField.getText().toString();
        if (!searchTerm.isEmpty()){
            // Hide keyboard
            if(this.getCurrentFocus() != null)
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
            // Start result list activity
            Intent intent = new Intent(this, ResultListActivity.class);
            intent.putExtra(EXTRA_SEARCH_BY_NAME, searchTerm);
            startActivity(intent);
            return true;
        }else{
            Animation shake = new TranslateAnimation(0,10,0,0);
            shake.setDuration(500);
            shake.setInterpolator(new CycleInterpolator(5));
            searchField.startAnimation(shake);
            return false;
        }
    }

    private void logoDialog(){
        DialogFragment dialogFragment = new AboutDialog();
        dialogFragment.show(getSupportFragmentManager(),"tag");
    }
}