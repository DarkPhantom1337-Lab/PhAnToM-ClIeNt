package ua.darkphantom1337.phantom.client;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;

public class PhantomMain {

    /**
     * @Author DarkPhantom1337
     */

    public static HashMap<Long, InetAddress> messages = new HashMap<>();

    private static Integer nextMessageID = 1;

    public static String serverHOST = "localhost";

    public static Integer serverPORT = 1337,clientPort = 11111;

    public static void main(String[] args) throws SocketException {
        Scanner sc = new Scanner(System.in);
        info("Enabling PhAnToM-ClIeNt...");
        System.out.print("[PhAnToM-ClIeNt] " + new SimpleDateFormat("[dd/MM/yyyy] [HH:mm:ss]").format(new Date()) + " [INFO] -> Please enter server IP -> ");
        serverHOST = sc.nextLine();
        System.out.print("[PhAnToM-ClIeNt] " + new SimpleDateFormat("[dd/MM/yyyy] [HH:mm:ss]").format(new Date()) + " [INFO] -> Please enter server port -> ");
        serverPORT = sc.nextInt();
        System.out.print("[PhAnToM-ClIeNt] " + new SimpleDateFormat("[dd/MM/yyyy] [HH:mm:ss]").format(new Date()) + " [INFO] -> Please enter client port -> ");
        clientPort = sc.nextInt();
        new ClientPacketReceiver(clientPort).start();
        info("Server: " + serverHOST + ":" + serverPORT);
        info("Client(YOUR): " + getLocalHostLANAddress() + ":" + clientPort);
        while (true){
            info("Write message in format: ip/port/yourmessage");
            String line = sc.nextLine();
            if (!line.equals("") && !line.equals(" "))
                sendMessageToClient("RESEND",getMessageID(serverPORT),serverHOST,serverPORT,line);
        }
    }

    private static void printMessage(MessageType msgtype, String message) {
        System.out.println("[PhAnToM-ClIeNt] " + new SimpleDateFormat("[dd/MM/yyyy] [HH:mm:ss]").format(new Date()) + " [" + msgtype.name() + "] -> " + message);
    }

    public static void info(String message) {
        printMessage(MessageType.INFO, message);
    }

    public static void error(String message) {
        printMessage(MessageType.ERROR, message);
    }

    public static void sendMessageToClient(String messageTYPE, Long messageID, String host, Integer port, String message) {
        try {
            //Пример сообщения: MESSAGE/ID/СООБЩЕНИЕ
            message =  messageTYPE+"/"+messageID + "/" + message;
            byte[] data = message.getBytes("UTF-8");
            InetAddress addr = InetAddress.getByName(host);
            DatagramPacket pack =
                    new DatagramPacket(data, data.length, addr, port);
            DatagramSocket ds = new DatagramSocket();
            ds.send(pack);
            ds.close();
            info("Successfully send message to " + host + ":" + port + " Message: " + message);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static Integer getNextMessageID(){
        nextMessageID++;
        return nextMessageID--;
    }

    public static Long getMessageID(Integer sendingPort){
        return Long.parseLong(getLocalHostLANAddress().getHostAddress().replace(".","") + sendingPort + nextMessageID);
    }

    private static InetAddress getLocalHostLANAddress() {
        try {
            InetAddress candidateAddress = null;
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress())
                            return inetAddr;
                        else if (candidateAddress == null)
                            candidateAddress = inetAddr;
                    }
                }
            }
            if (candidateAddress != null)
                return candidateAddress;
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null)
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            return null;
        }
    }

}