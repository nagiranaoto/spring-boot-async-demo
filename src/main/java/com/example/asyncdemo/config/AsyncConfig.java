package com.example.asyncdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 非同期処理の設定クラス
 *
 * ThreadPoolTaskExecutorを設定し、非同期処理のスレッドプールを管理
 */
@Configuration
public class AsyncConfig {

    /**
     * 非同期処理用のExecutorを設定
     *
     * @return Executor 設定されたスレッドプールタスクエグゼキューター
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // コアプールサイズ（常に維持するスレッド数）
        executor.setCorePoolSize(5);

        // 最大プールサイズ（最大スレッド数）
        executor.setMaxPoolSize(10);

        // キューの容量
        executor.setQueueCapacity(100);

        // スレッド名のプレフィックス
        executor.setThreadNamePrefix("async-");

        // 初期化
        executor.initialize();

        return executor;
    }
}
