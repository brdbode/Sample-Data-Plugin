# Sample Data Eclipse Plugin

## How to Run the Mock API

```bash
cd MockAPI

# Run the Spring Boot server (Unix/Mac)
./gradlew bootRun

# Or on Windows
gradlew.bat bootRun
```

The API will be available at: `GET http://localhost:8080/api/issues`

## How to Launch the Plugin

### Import and Setup

1. **Import the Plugin Project:**
   - File → Import → General → Existing Projects into Workspace
   - Select the plugin project directory
   - Ensure "Copy projects into workspace" is unchecked if you want to work with the original location

2. **Configure Target Platform:**
   - Window → Preferences → Plug-in Development → Target Platform
   - Ensure you have a target platform that includes the necessary Eclipse bundles

3. **Add Dependencies:**
   - The plugin requires org.json for JSON parsing
   - You may need to add org.json to your Classpath

### Launch the Plugin

**Right-click on the plugin project → Run As → Eclipse Application**

### Using the Plugin

1. In the launched Eclipse instance:
   - Window → Show View → Other...
   - Expand "Other" category
   - Select "Sample Data" view

2. Click the "Fetch Data" button to retrieve issues from the API

## Design Notes

### Project Structure

```
FetchDataButton/                             # Main Eclipse plugin project
├── src/
│   └── com.example.sampledata/
│       ├── Activator.java                   # Plugin activator
│       └── views/
│           └── SampleDataView.java          # Main view implementation
├── test/
│   └── com.example.sampledata.views/
│       └── SampleDataViewTest.java          # View tests
├── META-INF/
│   └── MANIFEST.MF                          # OSGi bundle configuration
├── schema/                                  # Extension point schemas
├── build.properties                         # Build configuration
├── plugin.xml                              # Eclipse extension points
└── lib/                                    # External dependencies

MockAPI/                                     # Spring Boot mock server
├── src/main/java/
│   └── com.example.mockapi/
│       ├── MockApiApplication.java          # Spring Boot main class
│       ├── Issue.java                       # Issue data model
│       └── IssueController.java             # REST controller
├── src/main/resources/                      # Spring Boot resources
├── src/test/java/                          # Mock API tests
├── gradle/                                 # Gradle wrapper
├── build.gradle                            # Gradle build script
├── gradlew                                 # Gradle wrapper script
├── gradlew.bat                             # Gradle wrapper (Windows)
└── settings.gradle                         # Gradle settings
```

### Architecture & Trade-offs

**Architecture & Trade-offs**: The plugin follows a streamlined architecture with all functionality consolidated in the `SampleDataView` class. This approach prioritizes simplicity and reduces complexity for this use case, while maintaining clean separation between UI creation, event handling, and data processing methods.

**Threading Strategy**: HTTP requests use Java's `CompletableFuture` with `HttpClient.sendAsync()` to perform network calls on background threads. UI updates are sent back to the SWT display thread using `Display.asyncExec()`, ensuring the UI remains responsive during data fetching.

**Error Handling**: Comprehensive error handling with detailed logging:
- Network errors are caught with full exception context and logged at SEVERE level
- HTTP errors (non-200 status codes) are logged with status code, URL, and response body snippet
- JSON parsing errors include the response body snippet for debugging
- All errors display user-friendly messages in the results panel
- Successful operations are also logged for monitoring

**Dependencies**: Minimal external dependencies approach:
- Uses Java built-in `HttpClient` for HTTP requests (no external HTTP libraries)
- Uses `org.json` library for JSON parsing (has to be added to Classpath)
- Leverages Eclipse SWT for UI components
- Standard Java logging framework for error tracking

**UI Design**: Clean SWT layout with:
- Simple grid layout with fetch button and scrollable text area
- Loading state feedback (button disabled during fetch, "Loading..." message)
- Formatted results display: `[#id] name — severity (updatedAt)`
- Scrollable results area for handling multiple issues

### Key Trade-offs Made

1. **Consolidated Architecture**: All logic in the view class rather than separate service layers - reduces complexity for this small use case
2. **CompletableFuture**: Used Java's CompletableFuture for simpler dependency management
3. **org.json**: Chose JSON library to easily parse the json response
4. **Built-in HttpClient**: Used Java HttpClient instead of external HTTP libraries like Apache HttpClient
5. **Simple Text Display**: Results shown in scrollable text area rather than implementing table/tree widgets for faster development
