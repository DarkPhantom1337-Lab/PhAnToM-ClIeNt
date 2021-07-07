package ua.darkphantom1337.phantom.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ClientPacketReceiver {

    private Integer receivePort;
    private Thread receiveThread;
    private DatagramSocket serverSocket;

    public ClientPacketReceiver(Integer receivePort) {
        PhantomMain.info("Starting port " + receivePort + " listener...");
        setReceivePort(receivePort);
        initPacketReceiver();
    }

    private void initPacketReceiver() {
        try {
            setServerSocket(new DatagramSocket(receivePort));
            setReceiveThread(new Thread(() -> {
                while (true) {
                    try {
                        DatagramPacket pack =
                                new DatagramPacket(new byte[1024], 1024);
                        serverSocket.receive(pack);
                        String data = new String(pack.getData()).substring(0,pack.getLength());
                        PhantomMain.info("[CLIENT-PACKET-RECEIVER-" + receivePort + "] IP: " + pack.getAddress() + ":" + pack.getPort() + " Data: " + data + " Length: " + pack.getLength());
                        if (data.contains("/")){
                            String[] spl = data.split("/");
                            if (spl.length >= 1) {
                                String messageType = spl[0];
                                if (messageType.equals("RESEND")) {
                                    if (spl.length >= 5) {
                                        Long messageId = Long.parseLong(spl[1]);
                                        PhantomMain.sendMessageToClient("MESSAGE", messageId, spl[2], Integer.parseInt(spl[3]), spl[4]);
                                    }
                                }
                                if (messageType.equals("MESSAGE")) {
                                    Long messageId = Long.parseLong(spl[1]);
                                    PhantomMain.info(spl[2]);
                                    PhantomMain.sendMessageToClient("DELIVERED", messageId, PhantomMain.serverHOST, PhantomMain.serverPORT, "");
                                }
                                if (messageType.equals("DELIVERED")) {
                                    Long messageId = Long.parseLong(spl[1]);
                                    PhantomMain.info("[CLIENT-PACKET-RECEIVER-" + receivePort + "] MSGID: " +messageId + " DELIVERED");
                                }
                            }
                            }
                    } catch (Exception e) {
                        PhantomMain.error("Error in listen packet....");
                        e.printStackTrace();
                    }
                }
            }));
            PhantomMain.info("ClientPacketReceiver for port " + receivePort + " successfully registered.");
        } catch (SocketException e) {
            PhantomMain.error("Port " + receivePort + " is already listening.");
            System.exit(1337);
        } catch (Exception e) {
            PhantomMain.error("Error in creating CLIENTPacketReceiver for port " + receivePort);
            System.exit(1337);
        }
    }

    public void start() {
        receiveThread.start();
        PhantomMain.info("ClientPacketReceiver for port " + receivePort + " successfully started.");
    }

    public Integer getReceivePort() {
        return receivePort;
    }

    public Thread getReceiveThread() {
        return receiveThread;
    }

    public DatagramSocket getServerSocket() {
        return serverSocket;
    }

    public void setReceivePort(Integer receivePort) {
        this.receivePort = receivePort;
    }

    public void setReceiveThread(Thread receiveThread) {
        this.receiveThread = receiveThread;
    }

    public void setServerSocket(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
}
