package com.example.asyncdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 非同期処理を実行するサービスクラス
 *
 * @Asyncアノテーションを使った非同期処理の基本を学ぶためのサンプル
 */
@Service
public class AsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    /**
     * 基本的な非同期処理（戻り値なし）
     *
     * @Asyncを付けることで、このメソッドは別スレッドで実行される
     * "taskExecutor"という名前のスレッドプールを使用
     *
     * @param message 処理するメッセージ
     */
    @Async("taskExecutor")
    public void executeAsyncTask(String message) {
        logger.info("[3] 非同期タスク開始 - メッセージ: {} - スレッド: {}",
            message, Thread.currentThread().getName());

        try {
            // 重い処理をシミュレート（3秒待機）
            logger.info("[4] 重い処理を実行中... (3秒待機) - スレッド: {}",
                Thread.currentThread().getName());
            Thread.sleep(3000);

            logger.info("[5] 非同期タスク完了 - メッセージ: {} - スレッド: {}",
                message, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error("非同期タスクが中断されました", e);
            Thread.currentThread().interrupt();
        } 
    }

    /**
     * 同期処理（比較用）
     *
     * @Asyncを付けない通常のメソッド
     * 呼び出し元と同じスレッドで実行される
     *
     * @param message 処理するメッセージ
     */
    public void executeSyncTask(String message) {
        logger.info("[同期] タスク開始 - メッセージ: {} - スレッド: {}",
            message, Thread.currentThread().getName());

        try {
            // 重い処理をシミュレート（3秒待機）
            logger.info("[同期] 重い処理を実行中... (3秒待機) - スレッド: {}",
                Thread.currentThread().getName());
            Thread.sleep(3000);

            logger.info("[同期] タスク完了 - メッセージ: {} - スレッド: {}",
                message, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error("同期タスクが中断されました", e);
            Thread.currentThread().interrupt();
        }
    }
}
