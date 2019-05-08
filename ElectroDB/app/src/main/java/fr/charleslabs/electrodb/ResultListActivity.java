package fr.charleslabs.electrodb;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;

import fr.charleslabs.electrodb.component.Component;
import fr.charleslabs.electrodb.componentViews.ComponentAdapter;
import fr.charleslabs.electrodb.component.ComponentsDB;

public class ResultListActivity extends AppCompatActivity {
    public static final String EXTRA_COMPONENT = "fr.charleslabs.electroDB.COMPONENT";
    private static final int NB_MAX_SEARCH_RESULT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

        // Set up listview
        final ListView resultListView = findViewById(R.id.resultList) ;
        resultListView.setEmptyView(findViewById(R.id.noResultView));

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        final String nameSearch = intent.getStringExtra(MainActivity.EXTRA_SEARCH_BY_NAME);
        if (nameSearch == null){
            Toast.makeText(getApplicationContext(), R.string.resultListActivity_toastError,Toast.LENGTH_LONG).show();
            finish();
        }

        // Set action bar title
        try{
            getSupportActionBar().setTitle(getString(R.string.resultActivity_Title,nameSearch));
        }catch (Exception ignored){}

        // Get result list
        try {
            List<Component> comps = ComponentsDB.getInstance(getApplicationContext())
                    .searchComponentByName(nameSearch,NB_MAX_SEARCH_RESULT);
            ComponentAdapter adapter = new ComponentAdapter(this,comps);
            resultListView.setAdapter(adapter);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), R.string.resultListActivity_errorDB,Toast.LENGTH_LONG).show();
            finish();
        }

        // Handle click on item
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
                Component a = (Component) parent.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), ComponentActivity.class);
                intent.putExtra(EXTRA_COMPONENT, a);
                startActivity(intent);
            }
        });

        //No result button
        this.findViewById(R.id.noResultButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY,nameSearch + " datasheet");
                startActivity(intent);
            }
        });
    }
}
