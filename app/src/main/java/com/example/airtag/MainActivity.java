package com.example.airtag;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // --- VARIÁVEIS ---
    // Mude o nome para o do seu módulo Bluetooth
    private static final String DEVICE_NAME = "HC-06"; // ou "HC-06", etc.

    // UUID padrão para comunicação SPP (Serial Port Profile)
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    private TextView tvStatus;
    private Button btnConnect;
    private LinearLayout controlsLayout;
    private Button btnBuzzer, btnLed, btnAmbos;

    private boolean isConnected = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa os componentes da UI
        tvStatus = findViewById(R.id.tvStatus);
        btnConnect = findViewById(R.id.btnConnect);
        controlsLayout = findViewById(R.id.controlsLayout);
        btnBuzzer = findViewById(R.id.btnBuzzer);
        btnLed = findViewById(R.id.btnLed);
        btnAmbos = findViewById(R.id.btnAmbos);

        // Configura os botões
        btnConnect.setOnClickListener(v -> toggleConnection());
        btnBuzzer.setOnClickListener(v -> sendCommand("B"));
        btnLed.setOnClickListener(v -> sendCommand("L"));
        btnAmbos.setOnClickListener(v -> sendCommand("A"));

        // Pede permissões de Bluetooth ao iniciar
        requestBluetoothPermissions();
    }

    private void toggleConnection() {
        if (!isConnected) {
            connectToDevice();
        } else {
            disconnect();
        }
    }

    private void connectToDevice() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            showToast("Bluetooth não é suportado neste dispositivo.");
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            showToast("Por favor, ative o Bluetooth.");
            return;
        }

        // Checa permissões de novo antes de escanear
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            showToast("Permissão BLUETOOTH_CONNECT não concedida.");
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice targetDevice = pairedDevices.stream().filter(device -> device.getName().equals(DEVICE_NAME)).findFirst().orElse(null);

        if (targetDevice == null) {
            showToast("Dispositivo '" + DEVICE_NAME + "' não encontrado. Por favor, pareie primeiro.");
            return;
        }

        tvStatus.setText("Conectando...");

        // Conexão em uma thread separada para não travar a UI
        new Thread(() -> {
            try {
                bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(SPP_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                isConnected = true;
                handler.post(() -> {
                    tvStatus.setText("Conectado a " + DEVICE_NAME);
                    btnConnect.setText("Desconectar");
                    controlsLayout.setVisibility(View.VISIBLE);
                });
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> {
                    showToast("Falha ao conectar.");
                    tvStatus.setText("Desconectado");
                });
            }
        }).start();
    }

    private void disconnect() {
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) { e.printStackTrace(); }
        }
        isConnected = false;
        tvStatus.setText("Desconectado");
        btnConnect.setText("Conectar ao ESP");
        controlsLayout.setVisibility(View.GONE);
        showToast("Desconectado.");
    }

    private void sendCommand(String command) {
        if (outputStream != null && isConnected) {
            // Envia o comando em uma thread separada
            new Thread(() -> {
                try {
                    outputStream.write(command.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        showToast("Erro ao enviar comando.");
                        disconnect();
                    });
                }
            }).start();
        } else {
            showToast("Não está conectado.");
        }
    }

    // Função para mostrar mensagens Toast na UI
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Pede as permissões necessárias em tempo de execução
    private void requestBluetoothPermissions() {
        String[] permissions = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN};
        ActivityCompat.requestPermissions(this, permissions, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }
}