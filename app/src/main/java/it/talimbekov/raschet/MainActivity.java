package it.talimbekov.raschet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;
    private EditText editTextHeight;
    private EditText editTextWidth;
    private EditText editTextPrice;
    private EditText editTextCount;
    private TextView resultTextView;
    private TableLayout tableLayout;
    private ScrollView verticalScrollView;
    private HorizontalScrollView horizontalScrollView;

    private float totalArea = 0;
    private float totalCountArea = 0;
    private float totalPrice = 0;

    private List<String> resultList;

    private static final String PREF_NAME = "RaschetPrefs";
    private static final String RESULTS_KEY = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button resultCalculateButton = findViewById(R.id.resultCalculateButton);
        resultCalculateButton.setOnClickListener(view -> displayTotalResult());

        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(view -> openHistoryActivity());

        editTextHeight = findViewById(R.id.editTextHeight);
        editTextWidth = findViewById(R.id.editTextWidth);
        editTextCount = findViewById(R.id.editTextCount);
        editTextPrice = findViewById(R.id.editTextPrice);
        resultTextView = findViewById(R.id.resultTextView);
        tableLayout = findViewById(R.id.tableLayout);
        verticalScrollView = findViewById(R.id.verticalScrollView);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(view -> calculateSquareArea());

        resultList = new ArrayList<>();

        // Загрузка ранее сохраненных результатов
        loadSavedResults();
    }

    private void loadSavedResults() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String resultsString = sharedPreferences.getString(RESULTS_KEY, "");

        if (!resultsString.isEmpty()) {
            String[] resultsArray = resultsString.split("\n");
            resultList.addAll(Arrays.asList(resultsArray));
        }
    }

    private void saveResults() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RESULTS_KEY, TextUtils.join("\n", resultList));
        editor.apply();
    }

    private void openHistoryActivity() {
        Intent intent = new Intent(this, History.class);
        intent.putStringArrayListExtra("resultList", new ArrayList<>(resultList));
        startActivity(intent);
    }

    private void calculateSquareArea() {
        String heightText = editTextHeight.getText().toString();
        String widthText = editTextWidth.getText().toString();
        String priceText = editTextPrice.getText().toString();
        String countText = editTextCount.getText().toString();

        if (!heightText.isEmpty() && !widthText.isEmpty() && !priceText.isEmpty() && !countText.isEmpty()) {
            float height = Float.parseFloat(heightText);
            float width = Float.parseFloat(widthText);
            float price = Float.parseFloat(priceText);
            float count = Float.parseFloat(countText);

            float area = (height * width) / 1000000;
            float countArea = area * count;
            float itemPrice = countArea * price;

            totalArea += countArea;
            totalCountArea += count;
            totalPrice += itemPrice;

            addRowToTable(height, width, count, countArea, itemPrice);

            updateResultTextView();

            resultTextView.setVisibility(View.GONE);
        } else {
            resultTextView.setText("Введите значения высоты, ширины, цену и количество");
            resultTextView.setVisibility(View.VISIBLE);
        }
    }

    private void displayTotalResult() {
        String totalResultText = String.format("Общая площадь: %.2f м²\nОбщее количество: %.2f штук\nОбщая цена: %.2f тг",
                totalArea, totalCountArea, totalPrice);

        tableLayout.removeAllViews();

        addRowToTable(totalResultText);

        totalArea = 0;
        totalCountArea = 0;
        totalPrice = 0;

        resultTextView.setText("");

        verticalScrollView.post(() -> verticalScrollView.fullScroll(View.FOCUS_DOWN));
        horizontalScrollView.post(() -> horizontalScrollView.fullScroll(View.FOCUS_RIGHT));

        // Save the total result to the resultList
        String timestamp = getCurrentTimestamp();
        String resultEntry = String.format("[%s] %s", timestamp, totalResultText);
        resultList.add(resultEntry);

        // Сохранение результатов
        saveResults();
    }

    private void updateResultTextView() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String result : resultList) {
            stringBuilder.append(result).append("\n\n");
        }

        resultTextView.setText(stringBuilder.toString());

        // Сохранение результатов
        saveResults();
    }

    private void addRowToTable(float height, float width, float count, float countArea, float itemPrice) {
        TableRow tableRow = new TableRow(this);

        TextView heightTextView = createTextView(String.format("%.2f мм", height));
        tableRow.addView(heightTextView);

        TextView widthTextView = createTextView(String.format("%.2f мм", width));
        tableRow.addView(widthTextView);

        TextView countTextView = createTextView(String.format("%.2f штук", count));
        tableRow.addView(countTextView);

        TextView areaTextView = createTextView(String.format("%.2f м²", countArea));
        tableRow.addView(areaTextView);

        TextView priceTextView = createTextView(String.format("%.2f тг", itemPrice));
        tableRow.addView(priceTextView);

        tableLayout.addView(tableRow);
    }

    private void addRowToTable(String text) {
        TableRow tableRow = new TableRow(this);

        TextView textView = createTextView(text);
        tableRow.addView(textView);

        tableLayout.addView(tableRow);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        return textView;
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
        } else {
            backToast = Toast.makeText(getBaseContext(), "Нажмите еще раз, чтобы выйти", Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    public void clearResult(View view) {
        tableLayout.removeAllViews();

        totalArea = 0;
        totalCountArea = 0;
        totalPrice = 0;
    }
}
