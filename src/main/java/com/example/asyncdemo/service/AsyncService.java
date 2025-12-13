package com.example.asyncdemo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 非同期処理を実行するサービスクラス
 */
@Service
public class AsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    /**
     * 非同期処理（戻り値なし）
     *
     * @param message 処理するメッセージ
     */
    @Async("taskExecutor")
    public void executeAsyncTask(String message) {
        logger.info("[3] 非同期タスク開始 - メッセージ: {} - スレッド: {} ★別スレッドで実行中★", message, Thread.currentThread().getName());

        try {
            // 重い処理をシミュレート（3秒待機）
            logger.info("[4] 重い処理を実行中... (3秒待機) - スレッド: {}", Thread.currentThread().getName());
            Thread.sleep(3000);
            logger.info("[5] 非同期タスク完了 - メッセージ: {} - スレッド: {} ★3秒後に完了★", message, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error("非同期タスクが中断されました", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 非同期処理（CompletableFutureを返す）
     *
     * @param number 処理する数値
     * @return CompletableFuture<Integer> 計算結果
     */
    @Async("taskExecutor")
    public CompletableFuture<Integer> executeAsyncCalculation(int number) {
        logger.info("非同期計算を開始: {} - スレッド: {}", number, Thread.currentThread().getName());

        try {
            // 重い計算処理をシミュレート（2秒待機）
            Thread.sleep(2000);

            // 計算（例：2倍にする）
            int result = number * 2;

            logger.info("非同期計算を完了: {} -> {}", number, result);
            return CompletableFuture.completedFuture(result);

        } catch (InterruptedException e) {
            logger.error("非同期計算が中断されました", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 複数の非同期処理を実行（データ取得シミュレーション）
     *
     * @param userId ユーザーID
     * @return CompletableFuture<String> ユーザー情報
     */
    @Async("taskExecutor")
    public CompletableFuture<String> fetchUserData(String userId) {
        logger.info("ユーザーデータ取得開始: {} - スレッド: {}", userId, Thread.currentThread().getName());

        try {
            // データベースやAPIからのデータ取得をシミュレート
            Thread.sleep(1500);

            String userData = "ユーザー[" + userId + "]のデータ";
            logger.info("ユーザーデータ取得完了: {}", userId);

            return CompletableFuture.completedFuture(userData);

        } catch (InterruptedException e) {
            logger.error("ユーザーデータ取得が中断されました", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
}
