package com.zhaozhou.demo.bio.socket;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by zhaozhou on 2018/9/28.
 */
public class SocketOptionTest {

    public static void socketOption(){
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setReuseAddress(true);
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            socket.setSendBufferSize(1000);
            SocketAddress address = new InetSocketAddress("127.0.0.1", 9999);
            socket.connect(address);
            socket.getOutputStream().write("this is a test!".getBytes());

            byte[] buf = new byte[1024];
            socket.getInputStream().read(buf);
            System.out.println("recv msg: " + new String(buf));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(socket != null && socket.isConnected()){
                try {
                    socket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }


    public static void main(String[]  args){
        SocketOptionTest.socketOption();
    }

}
