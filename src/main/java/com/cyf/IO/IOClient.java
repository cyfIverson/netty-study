package com.cyf.IO;

import java.net.Socket;
import java.util.Date;

/**
 * IO客户端 (客户端每两秒向服务端发送一条消息)
 * @Author cyfIverson
 */
public class IOClient {

    public static void main(String[] args) {

        new Thread(() ->{
            try {
                Socket socket = new Socket("127.0.0.1",8000);
                while (true){
                    socket.getOutputStream().write((new Date()+"- hello IO").getBytes());
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
