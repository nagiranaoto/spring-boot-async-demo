package com.example.asyncdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot非同期処理デモアプリケーション
 *
 * @EnableAsyncアノテーションにより、非同期メソッド実行機能を有効化
 */
@SpringBootApplication
@EnableAsync
public class AsyncDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsyncDemoApplication.class, args);
    }
}
