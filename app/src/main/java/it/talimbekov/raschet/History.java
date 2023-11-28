package it.talimbekov.raschet;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class History extends AppCompatActivity {

    private WebView historyWebView;
    private static final String PREF_NAME = "RaschetPrefs";
    private static final String RESULTS_KEY = "results";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyWebView = findViewById(R.id.historyWebView);

        // Retrieve and display saved results
        displaySavedResults();
    }

    private void displaySavedResults() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String resultsString = sharedPreferences.getString(RESULTS_KEY, "");

        if (!resultsString.isEmpty()) {
            String[] resultsArray = resultsString.split("\n");

            StringBuilder htmlTable = new StringBuilder("<html><body><table style=\"border-collapse: collapse; width: 100%; font-size: 12px;\">");

            // Process results in groups of three
            for (int i = 0; i < resultsArray.length; i += 3) {
                htmlTable.append("<tr style=\"border: 1px solid #dddddd; text-align: left;\">");

                for (int j = 0; j < 3 && (i + j) < resultsArray.length; j++) {
                    htmlTable.append("<td style=\"border: 1px solid #dddddd; padding: 8px;\">")
                            .append(resultsArray[i + j]).append("<br>").append("</td>");
                }

                htmlTable.append("</tr>");
            }

            htmlTable.append("</table></body></html>");

            // Enable JavaScript to allow copying text
            historyWebView.getSettings().setJavaScriptEnabled(true);

            // Add this line to allow text selection and copying
            historyWebView.loadDataWithBaseURL(null, htmlTable.toString(), "text/html", "UTF-8", null);
            historyWebView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Show a context menu for copying text
                    final WebView.HitTestResult result = historyWebView.getHitTestResult();
                    if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE ||
                            result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE ||
                            result.getType() == WebView.HitTestResult.ANCHOR_TYPE ||
                            result.getType() == WebView.HitTestResult.IMAGE_TYPE) {

                        android.webkit.WebView webView = (android.webkit.WebView) v;
                        android.webkit.WebView.HitTestResult hr = webView.getHitTestResult();

                        int type = hr.getType();
                        if (type == android.webkit.WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE
                                || type == android.webkit.WebView.HitTestResult.IMAGE_TYPE) {
                            // For image or image link, show a context menu with an option to copy
                            showContextMenu(hr.getExtra());
                        } else if (type == android.webkit.WebView.HitTestResult.SRC_ANCHOR_TYPE
                                || type == android.webkit.WebView.HitTestResult.ANCHOR_TYPE) {
                            // For regular link, show a context menu with an option to copy
                            showContextMenu(hr.getExtra());
                        }

                        return true; // Consume the long click event
                    }
                    return false; // Allow the default action on long press
                }
            });
        } else {
            historyWebView.loadData("Результат не найден.", "text/html", "UTF-8");
        }
    }

    private void showContextMenu(final String textToCopy) {
        registerForContextMenu(historyWebView);
        openContextMenu(historyWebView);
        unregisterForContextMenu(historyWebView);

        // Here, you can handle copying textToCopy to the clipboard
        // For example, you can use ClipboardManager to copy text
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied Text", textToCopy);
        clipboardManager.setPrimaryClip(clipData);
    }

    // ... остальной код без изменений ...

    public void clearResult(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String resultsString = sharedPreferences.getString(RESULTS_KEY, "");

        if (!resultsString.isEmpty()) {
            // Split the results and remove the last three elements
            String[] resultsArray = resultsString.split("\n");
            int numberOfResultsToRemove = Math.min(resultsArray.length, 3);
            for (int i = 1; i <= numberOfResultsToRemove; i++) {
                resultsArray[resultsArray.length - i] = "";
            }

            // Rebuild the results string
            StringBuilder stringBuilder = new StringBuilder();
            for (String result : resultsArray) {
                if (!result.isEmpty()) {
                    stringBuilder.append(result).append("\n");
                }
            }

            // Save the updated results
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(RESULTS_KEY, stringBuilder.toString());
            editor.apply();

            // Display the updated results
            displaySavedResults();
        }
    }

    public void Home(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
