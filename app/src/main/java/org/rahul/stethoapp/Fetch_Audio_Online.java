package org.rahul.stethoapp;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Fetch_Audio_Online extends AppCompatActivity {
    LineChart mChart;
    Button download,visualize;
    String hashkey_to_receive="";

    String url="http://10.14.79.58/download.php?nama=";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_stetho);

        download = (Button)findViewById(R.id.download);
        visualize = (Button)findViewById(R.id.visualize);

        mChart = (LineChart) findViewById(R.id.chart1);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            hashkey_to_receive = extras.getString("Hashkey");
        }

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

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });
        visualize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    visualizeFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void downloadFile()
    {
        url = url +hashkey_to_receive+".wav";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Somedescription");
        request.setTitle("Heartbeat"+"hashkey");
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, hashkey_to_receive+".wav");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public void visualizeFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/"+hashkey_to_receive+".wav"));
        LineData data = mChart.getData();
        int read;
        float x,y;
        x=0;
        y=0;
        byte[] buff = new byte[1024];
        while ((read = in.read(buff)) > 0)
        {
            out.write(buff, 0, read);
        }
        out.flush();
        byte[] audioBytes = out.toByteArray();

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
