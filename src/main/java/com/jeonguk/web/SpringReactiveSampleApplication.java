package com.jeonguk.web;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@SpringBootApplication
public class SpringReactiveSampleApplication {

    List<Stock> stockList = new ArrayList<>();
    List<String> stockNames = Arrays.asList("mango, banana, guava, infinity".split(",")); 
    
	public static void main(String[] args) {
		SpringApplication.run(SpringReactiveSampleApplication.class, args);
	}

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            createRandomStock();
            stockList.forEach(System.out::println);
        };
    }
    
	@RestController
	@RequestMapping("/stock/transaction")
	class StockTransactionController {
	    
	    @Autowired
	    StockTransactionService stockTransactionService;
	    
	    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	    public Flux<StockTransaction> stockTransactionEvents() {
	        return stockTransactionService.getStockTransactions();
	    }
	
	}
	
	@Service
	class StockTransactionService {
	    Flux<StockTransaction> getStockTransactions() {
	        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
	        interval.subscribe((i) -> stockList.forEach(stock -> stock.setPrice(changePrice(stock.getPrice()))));
	        
	        Flux<StockTransaction> stockTransactionFlux = Flux.fromStream(Stream.generate(() -> new StockTransaction(getRandomUser(), getRandomStock(), new Date())));
	        return Flux.zip(interval, stockTransactionFlux).map(Tuple2::getT2);
	    }
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	class Stock {
	    String name;
	    float price;
	}
	
    @Data	
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    class StockTransaction {
        String user;
        Stock stock;
        Date when;
    }
    
    void createRandomStock() {
        stockNames.forEach(stockName -> {
            stockList.add(new Stock(stockName, generateRandomStockPrice()));
        });
    }
    
    float generateRandomStockPrice() {
        float min = 30;
        float max = 50;
        return min + roundFloat(new Random().nextFloat() * (max - min));
    }
    
    String getRandomUser() {
        String users[] = "adam, tom, john, mike, bill, tony".split(",");
        return users[new Random().nextInt(users.length)];
    }
    
    Stock getRandomStock() {
        return stockList.get(new Random().nextInt(stockList.size()));
    }
    
    float changePrice(float price) {
        return roundFloat(Math.random() >= 0.5 ? price * 1.05f : price * 0.95f);
    }
	
    float roundFloat(float number) {
        return Math.round(number * 100.0) / 100.0f;
    }
    
}