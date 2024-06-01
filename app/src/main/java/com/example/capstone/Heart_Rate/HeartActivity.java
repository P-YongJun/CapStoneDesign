package com.example.capstone.Heart_Rate;

import android.app.AlertDialog;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.capstone.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

public class HeartActivity extends AppCompatActivity {

    // GUI Components
    private TextView mBluetoothStatus;  // 블루투스 상태 표시
    private TextView mReadBuffer;   // 데이터 읽어 들이는 버퍼 표시
    private Button mOnBtn;  // 블루투스 On 버튼
    private Button mOffBtn; // 블루투스 Off 버튼
    private Button mListPairedDevicesBtn;   // 페어링된 장치 목록 버튼
    private Button mDiscoverBtn;    // 블루투스 장치 탐색 버튼
    private Button mResetBtn;   // 재시작 버튼
    private Button mHomeBtn;
    private BluetoothAdapter mBTAdapter;    // 블루투스 어댑터
    private Set<BluetoothDevice> mPairedDevices;    // 페어링된 블루투스 장치 목록
    private ArrayAdapter<String> mBTArrayAdapter;    // 블루투스 장치 목록 어댑터
    private ListView mDevicesListView;  // 블루투스 장치 목록 보여 줌

    private Handler mHandler;   // 콜백 알림 받는 주 핸들러
    private ConnectedThread mConnectedThread;   // 데이터 송 수신을 위한 백그라운드 작업 스레드
    private BluetoothSocket mBTSocket = null;   // 양방향 클라이언트 간 데이터 경로

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");   // 고유 식별자

    private final static int REQUEST_ENABLE_BT = 1; // 블루투스 활성화 요청 식별
    private final static int MESSAGE_READ = 2;  // 메시지 업데이트 식별하는 블루투스 핸들러에서 사용
    private final static int CONNECTING_STATUS = 3; // 메시지 상태 식별하는 블루투스 핸들러에서 사용
    private static final String WARNING_MESSAGE = "WARNING";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_heart_rate);

        mBluetoothStatus = (TextView)findViewById(R.id.BluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.ReadBuffer);
        mOnBtn = (Button)findViewById(R.id.on);
        mOffBtn = (Button)findViewById(R.id.off);
        mDiscoverBtn = (Button)findViewById(R.id.DiscoverBtn);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        mResetBtn = (Button)findViewById(R.id.ResetBtn);
        mHomeBtn = (Button)findViewById(R.id.HomeBtn);

        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter);   // 모델을 뷰에 할당
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        mHandler = new Handler(){
            public void handleMessage(Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    }
                    catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1) {
                        mBluetoothStatus.setText("Connected to Device : " + (String)(msg.obj));
                        // 블루투스 연결 성공 시 "START" 메시지 전송
                    } else {
                        mBluetoothStatus.setText("Connection Failed");
                    }
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // 기기가 블루투스 지원 X
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {
            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });

            mResetBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    reset(v);
                }
            });

            mHomeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    goToHome(v);
                }
            });
        }
    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                mBluetoothStatus.setText("Enabled");
            } else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable();
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear();    // 항목 지우기
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void reset(View view){
        // 앱 재 시작을 위해 인텐트 생성
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void goToHome(View view) {
        Intent intent = new Intent(this, com.example.capstone.MainActivity.class);
        startActivity(intent);
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // 어댑터에 연결된 장치 추가
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // 입력 및 출력 스트림 가져오기, 멤버 스트림은 final이므로 임시 객체 사용
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024]; // 스트림을 위한 버퍼
            int bytes; // read()에서 반환된 바이트 수
            // 예외가 발생할 때까지 InputStream 유지
            while (true) {
                try {
                    // InputStream에서 읽기
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); // 나머지 데이터 기다림. 송신 속도에 따라 조정
                        bytes = mmInStream.available(); // 읽을 준비가 된 바이트 수
                        bytes = mmInStream.read(buffer, 0, bytes);  // 실제로 읽은 바이트 수 기록
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();    // 가져온 바이트 -> UI 액티비티에 보냄

                        String receivedMessage = new String(buffer, 0, bytes, "UTF-8");

                        if (receivedMessage.contains(WARNING_MESSAGE)) {
                            // WARNING이 포함 되어 있으면 경고 창
                            showAlert();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        private void showAlert() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HeartActivity.this);
                    builder.setTitle("WARNING")
                            .setMessage("이상 심박수가 감지되었습니다.")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
        }

        // 주 스레드에서 원격 장치로 데이터를 보내기 위해 호출
        public void write(String input) {
            byte[] bytes = input.getBytes();    // 입력 문자열을 바이트로 변환
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        // 연결 종료 -> 주 스레드에서 호출
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}