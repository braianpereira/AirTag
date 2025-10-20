# Controle de Dispositivos com Android e ESP8266 via Bluetooth

### Projeto para Tópicos Avançados - IFRS Campus Porto Alegre

Este é um projeto acadêmico desenvolvido para a disciplina de **Tópicos Avançados em Sistemas de Informação** do **IFRS - Campus Porto Alegre**.  
O sistema consiste em um aplicativo **Android nativo** que se comunica via **Bluetooth** com um microcontrolador **NodeMCU (ESP8266)** para acionar remotamente um **LED** e um **módulo de gravação e reprodução de áudio ISD1820**.

---

## 🚀 Funcionalidades

O projeto é dividido em duas partes principais: o aplicativo Android e o firmware do microcontrolador.

### 📱 Aplicativo Android Nativo

- **Conexão:** Busca por dispositivos Bluetooth pareados e estabelece uma conexão serial (SPP) com o módulo HC-05/HC-06.
- **Interface:** Uma UI simples com feedback de status (Conectado/Desconectado) e botões de controle.
- **Comandos:** Envia caracteres específicos (`'L'`, `'B'`, `'A'`) para o microcontrolador acionar as funções desejadas.

### 🔌 Firmware do NodeMCU (ESP8266)

- **Recepção:** Ouve continuamente a porta serial emulada (`SoftwareSerial`) para receber comandos do aplicativo.
- **Acionamento do LED:** Ao receber o comando `'L'` (ou `'A'`), acende o LED embutido por um segundo.
- **Acionamento de Áudio:** Ao receber o comando `'B'` (ou `'A'`), envia um pulso para o pino `PLAYE` do módulo **ISD1820**, reproduzindo uma mensagem de áudio pré-gravada.

---

## 🛠️ Tecnologias Utilizadas

### **Hardware**

- Microcontrolador **NodeMCU v3 (ESP8266)**
- Módulo **Bluetooth Clássico (HC-05 ou HC-06)**
- Módulo de gravação de voz **ISD1820**
- **LED** (embutido na placa)
- **Protoboard** e **Jumpers**

### **Software (Firmware)**

- **Linguagem:** C/C++ (Arduino Framework)
- **IDE:** Arduino IDE
- **Bibliotecas:** `SoftwareSerial.h`

### **Software (Aplicativo Android)**

- **Linguagem:** Java
- **IDE:** Android Studio
- **API:** Android Bluetooth Classic API (`BluetoothAdapter`, `BluetoothSocket`)

---

## ⚙️ Como Utilizar

1. **Montagem:**  
   Monte o circuito de hardware conectando o módulo Bluetooth e o ISD1820 ao NodeMCU conforme definido no código.

2. **Gravação de Áudio:**  
   Grave uma mensagem no módulo ISD1820 utilizando o botão **REC** do próprio módulo.

3. **Firmware:**  
   Carregue o código do arquivo `nodemu_receptor.ino` para a placa NodeMCU utilizando a **Arduino IDE**.

4. **Aplicativo:**  
   Compile e instale o aplicativo Android (código-fonte na pasta `/AndroidApp`) em um smartphone.

5. **Pareamento:**  
   Nas configurações do Android, pareie o smartphone com o módulo Bluetooth (senha padrão: `1234`).

6. **Execução:**  
   Abra o aplicativo, clique em **“Conectar”**, e utilize os botões para acionar os dispositivos remotamente.

---

**Autores:** Bráian Pereira, Leonardo Cruz, Lucas Oliveira
**Data:** 2025/2
