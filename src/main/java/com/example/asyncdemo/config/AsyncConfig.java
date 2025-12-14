package com.example.asyncdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 非同期処理の設定クラス
 *
 * @Asyncで使用するスレッドプールの設定を行う
 * この設定により、非同期処理の並列実行数やスレッド管理を制御できる
 */
@Configuration
public class AsyncConfig {

    /**
     * 非同期処理用のスレッドプールExecutorを設定
     *
     * Bean名を"taskExecutor"として登録することで、
     * @Async("taskExecutor")で明示的にこのスレッドプールを指定できる
     *
     * @return Executor 設定されたスレッドプールタスクエグゼキューター
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // コアプールサイズ: 常に維持するスレッド数
        // アプリ起動時からこの数のスレッドが確保される
        executor.setCorePoolSize(5);

        // 最大プールサイズ: 負荷が高い時に増やせる最大スレッド数
        // キューが満杯になった場合、この数まで増やせる
        executor.setMaxPoolSize(10);

        // キューの容量: コアスレッドが全て使用中の場合に待機するタスク数
        // この数を超えるとmaxPoolSizeまでスレッドを増やす
        executor.setQueueCapacity(100);

        // スレッド名のプレフィックス
        // ログで "async-1", "async-2" のように表示され、デバッグしやすくなる
        executor.setThreadNamePrefix("async-");

        // 初期化
        executor.initialize();

        return executor;
    }
}
