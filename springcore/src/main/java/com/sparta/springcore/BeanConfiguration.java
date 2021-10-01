package com.sparta.springcore;

import com.sparta.springcore.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class BeanConfiguration {
//
//    //ProductRepository 에 직접 @Component를 넣을 수 없어서 따로 넣어줌
//    @Bean
//    public ProductRepository productRepository() {
//        String dbUrl = "jdbc:h2:mem:springcoredb";
//        String dbId = "sa";
//        String dbPassword = "";
//
//        return new ProductRepository(dbUrl, dbId, dbPassword);
//    }
//}