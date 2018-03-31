package com.iqmsoft;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import redis.clients.jedis.Jedis;

@RestController
@SpringBootApplication
public class MainApp {

	private static Logger log = LoggerFactory.getLogger(MainApp.class);

	@Value("${redis.host}")
	private String redisHost;

	@Value("${redis.port}")
	private int redisPort;

	public static void main(String[] args) {
		SpringApplication.run(MainApp.class, args);
	}

	@GetMapping(value="/")
	public String pingRedis() {
		String response = null;
		Jedis jedis = null;
		log.info("In pingRedis redisHost="+redisHost+", redisPort="+redisPort);
		try {
			jedis = new Jedis(redisHost, redisPort);
			log.info("Connection to server sucessfully: " + redisHost); 
			//check whether server is running or not
			response = jedis.ping();
			log.info("Server is running: "+response); 
		} catch (Exception e) {
			response = e.getMessage();
			log.error("Error; "+e.getMessage(), e);
		} finally {
			if(jedis != null)
				jedis.close();
		}
		return response;
	}

	
	@GetMapping(value="/testCache")
	public String testCache() {
		String response = null;
		Jedis jedis = null;
		log.info("In testCache redisHost="+redisHost+", redisPort="+redisPort);
		try {
			jedis = new Jedis(redisHost, redisPort);
			log.info("Connection to server sucessfully"); 
			//check whether server is running or not 
			log.info("Server is running: "+jedis.ping()); 

			//store data in redis list 
			jedis.lpush("tutorial-list", "test1"); 
			jedis.lpush("tutorial-list", "test2"); 
			jedis.lpush("tutorial-list", "test3"); 
			// Get the stored data and print it 
			List<String> list = jedis.lrange("tutorial-list", 0 ,5); 

			StringBuilder sb = new StringBuilder();
			for(int i = 0; i<list.size(); i++) { 
				sb.append("Stored string in redis:: "+list.get(i)); 
			}
			response = sb.toString();
		} catch (Exception e) {
			response = e.getMessage();
			log.error("Error; "+e.getMessage(), e);
		} finally {
			if(jedis != null)
				jedis.close();
		}
		return response;
	}
}
