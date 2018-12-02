package com.cyf.nettychat;

import com.cyf.nettychat.websocket.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NettyChatApplication {

//	public static void main(String[] args) {
//		SpringApplication.run(NettyChatApplication.class, args);
//	}

	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext run = SpringApplication.run(NettyChatApplication.class, args);
		NettyServer nettyServer = run.getBean(NettyServer.class);
		nettyServer.start();
	}
}
