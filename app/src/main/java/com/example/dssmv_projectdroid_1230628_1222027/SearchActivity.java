package com.example.dssmv_projectdroid_1230628_1222027;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ListView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dssmv_projectdroid_1230628_1222027.Adapters.SongAdapter;
import com.example.dssmv_projectdroid_1230628_1222027.Model.SharedPreferencesManager;
import com.example.dssmv_projectdroid_1230628_1222027.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText etSearchQuery;
    private ImageButton btnFilter;
    private TextView txtCurrentFilter;
    private ListView listSearchHistory;

    private SharedPreferencesManager prefsManager;
    private List<String> searchHistory;
    private ArrayAdapter<String> historyAdapter;

    private final String[] filterOptions = {"Todos (multi)", "Músicas (tracks)", "Álbuns (albums)", "Artistas (artists)"};
    private final String[] filterApiValues = {"multi", "tracks", "albums", "artists"};
    private int selectedFilterIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        prefsManager = new SharedPreferencesManager(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_search), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnFilter = findViewById(R.id.btnFilter);
        txtCurrentFilter = findViewById(R.id.txtCurrentFilter);
        listSearchHistory = findViewById(R.id.listSearchHistory);


        btnFilter.setOnClickListener(v -> showFilterDialog());

        listSearchHistory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedQuery = searchHistory.get(position);
            executeSearch(selectedQuery);
        });

        etSearchQuery.setOnEditorActionListener((v, actionId, event) -> {
            boolean isActionOk = (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_NEXT ||
                    actionId == EditorInfo.IME_ACTION_UNSPECIFIED);

            boolean isDown = (event == null || event.getAction() == KeyEvent.ACTION_DOWN);

            if (isActionOk && isDown) {
                String query = etSearchQuery.getText().toString().trim();
                if (!query.isEmpty()) {
                    executeSearch(query);
                }
                return true;
            }
            return false;
        });
    }
        private void showFilterDialog() {
            new AlertDialog.Builder(this)
                    .setTitle("Filtrar Pesquisa por")
                    .setSingleChoiceItems(filterOptions, selectedFilterIndex, (dialog, which) -> {
                        selectedFilterIndex = which;
                        txtCurrentFilter.setText("Filtro: " + filterOptions[which]);
                        dialog.dismiss();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }

    @Override
    protected void onResume() {
        super.onResume();
        searchHistory = prefsManager.loadSearchHistory();
        historyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchHistory);
        listSearchHistory.setAdapter(historyAdapter);
    }

    private void executeSearch(String query) {
        prefsManager.saveSearchQuery(query);

        Intent intent = new Intent(SearchActivity.this, SearchResultsActivity.class);
        intent.putExtra("EXTRA_QUERY", query);
        intent.putExtra("EXTRA_FILTER", filterApiValues[selectedFilterIndex]);
        startActivity(intent);
    }
}