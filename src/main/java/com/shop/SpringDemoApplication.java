package com.shop;

import com.shop.crawling.ArtItemCrawling;
import com.shop.crawling.ArtistCrawling;
import com.shop.crawling.RentalCrawling;
import com.shop.crawling.RentalItemCrawling;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableAsync
public class SpringDemoApplication {

	public static void main(String[] args) {
		// 애플리케이션 컨텍스트 가져오기
		ApplicationContext context = SpringApplication.run(SpringDemoApplication.class, args);


		// ArtItemCrawling 빈을 가져와서 비동기 크롤링 실행
//		ArtItemCrawling artItemCrawling = context.getBean(ArtItemCrawling.class);
//		artItemCrawling.crawl();  // 비동기적으로 크롤링 실행
//
//		ArtistCrawling artistCrawling = context.getBean(ArtistCrawling.class);
//		artistCrawling.crawl();
//
//		RentalCrawling rentalCrawling = context.getBean(RentalCrawling.class);
//		rentalCrawling.crawl();
//
//		RentalItemCrawling rentalItemCrawling = context.getBean(RentalItemCrawling.class);
//		rentalItemCrawling.crawl();


	}
}