package com.example.sampledata.views;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONArray;
import org.json.JSONObject;

public class SampleDataView extends ViewPart {
    private static final Logger LOGGER = Logger.getLogger(SampleDataView.class.getName());
    private static final String API_URL = "http://localhost:8080/api/issues";

    private Text resultArea;
    private Button fetchButton;

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        fetchButton = new Button(parent, SWT.PUSH);
        fetchButton.setText("Fetch Data");

        resultArea = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
        resultArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        fetchButton.addListener(SWT.Selection, e -> fetchData());
        
        LOGGER.info("Sample Data View initialized");
    }

    private void fetchData() {
        fetchButton.setEnabled(false);
        resultArea.setText("Loading...");
        
        LOGGER.info("Starting data fetch from: " + API_URL);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        CompletableFuture<HttpResponse<String>> future = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        future.whenComplete((response, error) -> {
            getSite().getShell().getDisplay().asyncExec(() -> {
                fetchButton.setEnabled(true);
                
                if (error != null) {
                    String errorMsg = "Network error during API call to " + API_URL + ": " + error.getMessage();
                    LOGGER.log(Level.SEVERE, errorMsg, error);
                    resultArea.setText("Error: " + error.getMessage());
                    return;
                }

                // Log response details for both success and failure cases
                int statusCode = response.statusCode();
                String responseBody = response.body();
                
                LOGGER.info(String.format("HTTP Response: status=%d, response=%s", 
                        statusCode, responseBody != null ? responseBody : "No response"));

                if (statusCode != 200) {
                    // Log HTTP error with full context
                    String errorContext = String.format("HTTP request failed - Status: %d, URL: %s, Response body snippet: %s", 
                            statusCode, API_URL, responseBody);
                    LOGGER.severe(errorContext);
                    
                    resultArea.setText("Failed: HTTP " + statusCode + "\n" + responseBody);
                    return;
                }

                try {
                    JSONArray arr = new JSONArray(responseBody);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        sb.append("[#").append(obj.getInt("id")).append("] ")
                                .append(obj.getString("name"))
                                .append(" — ").append(obj.getString("severity"))
                                .append(" (").append(obj.getString("updatedAt")).append(")")
                                .append("\n");
                    }
                    resultArea.setText(sb.toString());
                    
                    // Log successful parsing
                    LOGGER.info("Successfully parsed and displayed " + arr.length() + " issues");
                    
                } catch (Exception ex) {
                    // Log JSON parsing errors with context
                    String parseErrorContext = String.format("JSON parsing failed - Status: %d, Body snippet: %s, Parse error: %s", 
                            statusCode, responseBody, ex.getMessage());
                    LOGGER.log(Level.SEVERE, parseErrorContext, ex);
                    
                    resultArea.setText("Error parsing JSON: " + ex.getMessage());
                }
            });
        });
    }

    @Override
    public void setFocus() {
        resultArea.setFocus();
    }
}