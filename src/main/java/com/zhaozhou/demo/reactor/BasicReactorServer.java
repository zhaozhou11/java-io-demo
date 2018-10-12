package com.zhaozhou.demo.reactor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by zhaozhou on 2018/10/12.
 * 基本的reactor模式
 */
public class BasicReactorServer {

    public static void start(int port){
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().setReuseAddress(true);
            serverSocketChannel.bind(new InetSocketAddress(port), 128);

            //注册accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            //阻塞等待就绪事件
            while (selector.select() > 0){
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                //遍历就绪事件
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    try {
                        //有客户端连接？
                        if(key.isAcceptable()){
                            ServerSocketChannel acceptChannel = (ServerSocketChannel)key.channel();
                            SocketChannel socketChannel = acceptChannel.accept();
                            socketChannel.configureBlocking(false);
                            System.out.println("accept connect form " + socketChannel.getRemoteAddress().toString());
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                        //有读事件就绪？
                        else if(key.isReadable()){
                            SocketChannel socketChannel = (SocketChannel)key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int cnt = 0, total = 0;
                            String msg = "";
                            do{
                                cnt = socketChannel.read(buffer);
                                if(cnt > 0){
                                    total += cnt;
                                    msg += new String(buffer.array());
                                }
                                buffer.clear();
                            }while (cnt >= buffer.capacity());
                            System.out.println("read data num:" + total);
                            System.out.println("recv msg:" + msg);;

                            //回写数据
                            ByteBuffer sendBuf = ByteBuffer.allocate(msg.getBytes().length + 1);
                            sendBuf.put(msg.getBytes());
                            socketChannel.write(sendBuf);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        key.channel().close();
                    }

                    keys.remove(key);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void main(String[] args){
        BasicReactorServer.start(9999);
    }
}
