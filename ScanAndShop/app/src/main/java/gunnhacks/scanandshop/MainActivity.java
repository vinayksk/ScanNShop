package gunnhacks.scanandshop;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.*;
import java.net.*;
import java.io.*;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    private Button scanBtn;
    private Button mQuery;
    private TextView formatTxt, contentTxt;
    public String UPC;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);
        mQuery = (Button) findViewById(R.id.query);
        scanBtn.setOnClickListener(this);
        mQuery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DataParser chapathi = new DataParser();
                chapathi.execute();
            }
        });

    }
    public void onClick(View v){
        if(v.getId()==R.id.scan_button) {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
            if(v.getId() == R.id.query)
            {
                
            }


    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            UPC = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + UPC);
        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
    public class DataParser extends AsyncTask<String, Void, String> {

        private String stream;

        public TextView mResult = (TextView) findViewById(R.id.result);
        private String url = "http://api.walmartlabs.com/v1/items?apiKey=s76z46gcjz56dmjpm3ca7qz8&upc=" + UPC + "&format=json";


        @Override
        protected String doInBackground(String[] params) {
            try {
                URLConnection walmart = new URL(url).openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(walmart.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }
                stream = sb.toString();
                return stream;

            }
            catch (IOException e){
                throw new RuntimeException(e);

            }

        }

        @Override
        protected void onPostExecute(String message) {
            try{
                JSONObject reader = new JSONObject(message);
                mResult.setText(reader.getString("salePrice"));
            }
            catch(JSONException f){
                mResult.setText("Not Found");

            }
        }

    }

}
