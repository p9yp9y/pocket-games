package pocketgames.p9yp9y.pocketgames;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SensorListener {

    private static final float SHAKE_THRESHOLD = 800;
    private DatagramSocket socket;
    private InetAddress address;
    private Context ctx;
    private Vibrator vibrator;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private long lastUpdate;
    private float last_x, last_y, last_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = getApplicationContext();
        toast("Hello");

        ((Button) findViewById(R.id.test)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendSpace();
            }
        });

        startSensor();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void startSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void sendSpace() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("sendSpace");
                vibrator.vibrate(500);

                String msg = "test";
                byte[] buf = msg.getBytes();
                try {
                    if (socket == null) {
                        socket = new DatagramSocket();
                    }

                    address = InetAddress.getByName(((EditText) findViewById(R.id.host)).getText().toString());

                    DatagramPacket packet
                            = new DatagramPacket(buf, buf.length, address, 4445);

                    socket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void toast(String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(int sensor, float[] values) {
        if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = values[SensorManager.DATA_X];
                float y = values[SensorManager.DATA_Y];
                float z = values[SensorManager.DATA_Z];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    sendSpace();
                    Log.d("sensor", "shake detected w/ speed: " + speed);
                    Toast.makeText(this, "shake detected w/ speed: " + speed, Toast.LENGTH_SHORT).show();
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(int sensor, int accuracy) {

    }
}
