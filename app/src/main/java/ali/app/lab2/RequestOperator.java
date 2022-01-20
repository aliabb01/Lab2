package ali.app.lab2;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RequestOperator extends Thread {

    public interface RequestOperatorListener{
        void success(ModelPost publication);
        void failed(int responseCode);
    }

    private RequestOperatorListener listener;
    private int responseCode;

    public void setListener (RequestOperatorListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        try {
            ModelPost publication = request();

            if(publication != null) {
                success(publication);
            }
            else {
                failed(responseCode);
            }
        } catch(IOException e) {
            failed(-1);
        } catch(JSONException e) {
            failed(-2);
        }
    }

    private ModelPost request() throws IOException, JSONException {
        // URL address
        URL object = new URL("https://jsonplaceholder.typicode.com/posts/1");

        // Executor
        HttpsURLConnection connection = (HttpsURLConnection) object.openConnection();

        // Determine what method will be used (GET, POST, PUT or DELETE)
        connection.setRequestMethod("GET");

        // Determine the content type. In this case, it is a JSON variable.
        connection.setRequestProperty("Content-Type", "application/json");

        // Make request and receive a response
        responseCode = connection.getResponseCode();
        Log.i("Response code", String.valueOf(responseCode));

        InputStreamReader inputStreamReader;

        // If response is okay, use InputStream
        // If not, use ErrorStream
        if(responseCode == 200) {
            inputStreamReader = new InputStreamReader(connection.getInputStream());
        } else {
            inputStreamReader = new InputStreamReader(connection.getErrorStream());
        }

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String inputLine;
        StringBuffer responseStringBuffer = new StringBuffer();

        while((inputLine = bufferedReader.readLine()) != null) {
            responseStringBuffer.append(inputLine);
        }
        bufferedReader.close();

        // Print result
        Log.i("Response Result", responseStringBuffer.toString());

        if(responseCode == 200) {
            return parsingJsonObject(responseStringBuffer.toString());
        } else {
            return null;
        }
    }

    public ModelPost parsingJsonObject(String response) throws JSONException {

        // attempts to create a json object of achieving a response
        JSONObject object = new JSONObject(response);
        ModelPost post = new ModelPost();

        // because we will not need Id and user id, they do not necessarily
        // get from a server in the JSON object
        post.setId(object.optInt("id", 0));
        post.setUserId(object.optInt("userId", 0));

        // If the variables have not been found, then JSONException will be held
        post.setTitle(object.getString("title"));
        post.setBodyText(object.getString("body"));

        return post;
    }

    private void failed(int code) {
        if(listener != null) {
            listener.failed(code);
        }
    }

    private void success(ModelPost publication) {
        if(listener != null) {
            listener.success(publication);
        }
    }
}
