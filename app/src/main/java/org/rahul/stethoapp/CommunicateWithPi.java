package org.rahul.stethoapp;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

public class CommunicateWithPi extends AppCompatActivity {

    LineChart mChart;

    String heartbeat;
    Button btn;
    int size = 2;
    private float[] mPoints;
    private Thread thread;
    private boolean plotData = true;
    EditText portedit;
    int port = 8056;
    int bpmKaValue;
    TextView bpm_View;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_stetho);

        btn = (Button) findViewById(R.id.start);

        //portedit = (EditText)findViewById(R.id.portEnter);



        mChart = (LineChart) findViewById(R.id.chart1);

        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.GREEN);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAxisMaximum(100000);
        xl.setAxisMinimum(0);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum(500f);
        leftAxis.setAxisMinimum(-500f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(false);

        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                new CallServer().execute();
            }
        });
    }

    public class CallServer extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... arg0) {
            String modifiedSentence = "hello";
            Socket clientSocket;
            //bpm_View = (TextView)findViewById(R.id.bpm);

            //Butterworth butterworth = new Butterworth();
            //butterworth.bandPass(2,44100,45,20);
            float x, y;
            x = 0;
            int bpm = 0;
            try {
                clientSocket = new Socket("192.168.137.102", 8056);//connection
                InputStream inputStream = clientSocket.getInputStream();//inputstream for the socket
                Log.v("Audio", "Connected");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();//initilized byte array output stream from the inputstream
                byte[] content = new byte[1024];
                int bytesRead = -1;
                int bpmValue=1;
                LineData data = mChart.getData();
                int count = 1;
                //long start = System.currentTimeMillis();
                while ((bytesRead = inputStream.read(content)) != -1) {//execute if some data is being received
                    baos.write(content, 0, bytesRead);
                    //playMp3(content);
                    //Thread.sleep(50);




                    byte[] audioBytes = baos.toByteArray();//convert the stream to byte array[]

                    for (int h = 0; h < audioBytes.length; h++) {
                        Log.v("Data", Byte.toString(audioBytes[h]));
                    }

                    ILineDataSet set = data.getDataSetByIndex(0);
                    if (set == null) {
                        set = createSet();
                        data.addDataSet(set);
                    }

                    float[] fArr = new float[audioBytes.length];
                    for (int i = 0; i < audioBytes.length; i++) {
                        fArr[i] = (float) audioBytes[i];
                    }

                    //series= new LineGraphSeries<DataPoint>();
                    for (int i = 0; i < fArr.length; i += 5) {
                        //Log.v("Audio",Byte.toString(audioBytes[i]));
                        //System.out.println(audioBytes[i]);
                        //byte curByte = audioBytes[i];
                        //int curByte = graphData[0][i];
                        y = fArr[i];

                        data.addEntry(new Entry(x, y), 0);
                        //series.appendData(new DataPoint(x,y), true, audioBytes.length);
                        x = x + 5;
                        //x = x + (1 / RECORDER_SAMPLERATE);
                    }


                    data.notifyDataChanged();
                    // let the chart know it's data has changed
                    mChart.notifyDataSetChanged();
                    // limit the number of visible entries
                    mChart.setVisibleXRangeMaximum(300);
                    // mChart.setVisibleYRange(30, AxisDependency.LEFT);
                    // move to the latest entry
                    mChart.moveViewToX(data.getEntryCount());

                }

                byte[] bpmVal = baos.toByteArray();
                bpmValue = bpmVal[bpmVal.length-1];
                Log.v("MSg:","new:"+bpmVal[0]+":"+bpmVal[1]+":"+bpmVal[2]+":"+bpmVal[3]);
                bpm_View.setText("BPM:"+bpmValue);
                count++;


                //graphView.addSeries(series);
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }





            return modifiedSentence;
        }
    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Dynamic Data");
        //set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.GREEN);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }

}
