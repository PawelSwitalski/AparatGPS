package com.main.aparatgps.photo.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.main.aparatgps.R;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.main.aparatgps.MainActivity.WriteFileToStream;


public class BluetoothActivity extends AppCompatActivity {

    Button listen, send, listDevices;
    ListView listView;
    TextView msg_box, status;
    EditText writeMsg;
    ImageView imageView;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "AparatGPS";
    //private static final UUID MY_UUID = UUID.randomUUID();
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        Intent intent = getIntent();
        photoPath = intent.getStringExtra("photoPath");

        findViewsById();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //prepare ImageView
        imageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));


        if (!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
    }

    private void implementListeners() {

        listDevices.setOnClickListener(v -> {
            Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
            String[] strings = new String[bt.size()];
            btArray = new BluetoothDevice[bt.size()];
            int index = 0;

            if (bt.size() > 0){
                for (BluetoothDevice device : bt){
                    btArray[index] = device;
                    strings[index] = device.getName();
                    index++;
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, strings);

                listView.setAdapter(arrayAdapter);
            }
        });

        listen.setOnClickListener(v -> {
            ServerClass serverClass = new ServerClass();
            serverClass.start();
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            ClientClass clientClass = new ClientClass(btArray[position]);
            clientClass.start();

            status.setText("Connecting");
        });

        send.setOnClickListener(v -> {
            /**
             * Wysyłanie pliku
             */

            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.png2);
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] imageBytes=stream.toByteArray();
            //byte[] imageBytes = bitmap.getNinePatchChunk();

            int subArraySize=400;

            sendReceive.write(String.valueOf(imageBytes.length).getBytes());

            for(int i=0;i<imageBytes.length;i+=subArraySize){
                byte[] tempArray;
                tempArray= Arrays.copyOfRange(imageBytes, i, Math.min(imageBytes.length, i + subArraySize));
                sendReceive.write(tempArray);
            }
        });
    }

    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case STATE_LISTENING:
                status.setText("Listening");
                break;
            case STATE_CONNECTING:
                status.setText("Connecting");
                break;
            case STATE_CONNECTED:
                status.setText("Connected");
                break;
            case STATE_CONNECTION_FAILED:
                status.setText("Connection Failed");
                break;
            case STATE_MESSAGE_RECEIVED:
                /**
                 * Odczytywanie wiadomości bluetooth
                 */

                byte[] readBuff = (byte[]) msg.obj;
                Bitmap bitmap = BitmapFactory.decodeByteArray(readBuff, 0, msg.arg1);
                imageView.setImageBitmap(bitmap);

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String name = mDateFormat.format(new Date()) + ".jpg";

                final File file1 = new File(getBatchDirectoryName(), name);
                try {
                    FileOutputStream fOut = new FileOutputStream(file1);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (IOException exception){
                    exception.printStackTrace();
                }


                galleryAddPic(file1, 0);
                /*
                byte[] readBuff = (byte[]) msg.obj;
                String tempMsg = new String(readBuff, 0, msg.arg1);
                msg_box.setText(tempMsg);
                 */
                //status.setText("Message Received");
                break;
            default:
                status.setText("Unknown State");
                break;
        }
        return true;
    });

    private void findViewsById() {
        listen = findViewById(R.id.buttonListen);
        send = findViewById(R.id.buttonSend);
        listDevices = findViewById(R.id.buttonListDevices);
        listView = findViewById(R.id.listView);
        //msg_box = findViewById(R.id.textViewMessage);
        status = findViewById(R.id.status);
        writeMsg = findViewById(R.id.editText);
        imageView = findViewById(R.id.imageView);
    }



    private void galleryAddPic(File originalFile, int mediaType) {
        if (!originalFile.exists()) {
            return;
        }

        int pathSeparator = String.valueOf(originalFile).lastIndexOf('/');
        int extensionSeparator = String.valueOf(originalFile).lastIndexOf('.');
        String filename = pathSeparator >= 0 ? String.valueOf(originalFile).substring(pathSeparator + 1) : String.valueOf(originalFile);
        String extension = extensionSeparator >= 0 ? String.valueOf(originalFile).substring(extensionSeparator + 1) : "";

        // Credit: https://stackoverflow.com/a/31691791/2373034
        String mimeType = extension.length() > 0 ? MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase(Locale.ENGLISH)) : null;

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.TITLE, filename);
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);

        if (mimeType != null && mimeType.length() > 0)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

        Uri externalContentUri;
        if (mediaType == 0) {
            externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        } else if (mediaType == 1) {
            externalContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        } else {
            externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }


        // Android 10 restricts our access to the raw filesystem, use MediaStore to save media in that case
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera");
            values.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.MediaColumns.IS_PENDING, true);

            Uri uri = getContentResolver().insert(externalContentUri, values);
            if (uri != null) {
                try {
                    if (WriteFileToStream(originalFile, getContentResolver().openOutputStream(uri))) {
                        values.put(MediaStore.MediaColumns.IS_PENDING, false);
                        getContentResolver().update(uri, values, null, null);
                    }
                } catch (Exception e) {
                    getContentResolver().delete(uri, null, null);
                }
            }
            originalFile.delete();
        } else {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(originalFile));
            sendBroadcast(mediaScanIntent);
        }

    } //gallery add end

    public String getBatchDirectoryName() {
        String app_folder_path;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            app_folder_path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        } else {
            app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        }

        File dir = new File(app_folder_path);
        if (!dir.exists() && !dir.mkdirs()) {
        }
        return app_folder_path;
    }




    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);

                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {

        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device) {
            this.device = device;
            try {
                this.socket = this.device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket) {
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        @Override
        public void run() {
            byte[] buffer = null;
            int numberOfBytes = 0;
            int index=0;
            boolean flag = true;

            while(true)
            {
                if(flag)
                {
                    try {
                        byte[] temp = new byte[inputStream.available()];
                        if(inputStream.read(temp)>0)
                        {
                            numberOfBytes=Integer.parseInt(new String(temp,"UTF-8"));
                            buffer=new byte[numberOfBytes];
                            flag=false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        byte[] data=new byte[inputStream.available()];
                        int numbers=inputStream.read(data);

                        System.arraycopy(data,0,buffer,index,numbers);
                        index=index+numbers;

                        if(index == numberOfBytes)
                        {
                            handler.obtainMessage(STATE_MESSAGE_RECEIVED,numberOfBytes,-1,buffer).sendToTarget();
                            flag = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}