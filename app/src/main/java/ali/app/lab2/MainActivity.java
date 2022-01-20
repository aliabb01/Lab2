package ali.app.lab2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements RequestOperator.RequestOperatorListener {

    Button sendRequestBtn;
    TextView title;
    TextView bodyText;

    private ModelPost publication;

    private IndicatingView indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        indicator = (IndicatingView) findViewById(R.id.generated_graphic);

        sendRequestBtn = (Button) findViewById(R.id.sendRequestBtn);
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });

        title = (TextView) findViewById(R.id.name);
        bodyText = (TextView) findViewById(R.id.body);
    }

    private void sendRequest() {
        RequestOperator ro = new RequestOperator();
        ro.setListener(this);
        ro.start();
    }

    public void updatePublication() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(publication != null) {
                    title.setText(publication.getTitle());
                    bodyText.setText(publication.getBodyText());
                }
                else {
                    title.setText("err");
                    bodyText.setText("err");
                }
            }
        });
    }

    public void setIndicatorStatus(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                indicator.setState(status);
                indicator.invalidate();
            }
        });
    }

    @Override
    public void success(ModelPost publication) {
        this.publication = publication;
        updatePublication();
        setIndicatorStatus(IndicatingView.SUCCESS);
    }

    @Override
    public void failed(int responseCode) {
        this.publication = null;
        updatePublication();
        setIndicatorStatus(IndicatingView.FAILED);
    }
}