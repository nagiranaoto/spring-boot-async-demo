package com.example.asyncdemo.controller;

import com.example.asyncdemo.service.AsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

/**
 * 非同期処理を呼び出すRESTコントローラー
 */
@RestController
@RequestMapping("/api/async")
public class AsyncController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncController.class);

    @Autowired
    private AsyncService asyncService;

    /**
     * 非同期タスクを実行（戻り値なし）
     *
     * @param message 処理するメッセージ
     * @return レスポンスメッセージ
     */
    @GetMapping("/task")
    public String executeAsyncTask(@RequestParam(defaultValue = "デフォルトメッセージ") String message) {
        logger.info("========================================");
        logger.info("[1] リクエスト受信 - メッセージ: {} - スレッド: {}", message, Thread.currentThread().getName());

        // 非同期処理を開始（すぐに制御が返る）
        asyncService.executeAsyncTask(message);

        logger.info("[2] レスポンス返却直前 - スレッド: {} ★非同期処理の完了を待たずにここに到達★", Thread.currentThread().getName());
        logger.info("========================================");

        return "非同期タスクを開始しました: " + message;
    }

    /**
     * 非同期計算を実行（CompletableFutureを返す）
     *
     * @param number 計算する数値
     * @return CompletableFuture<String> 計算結果
     */
    @GetMapping("/calculate")
    public CompletableFuture<String> executeAsyncCalculation(
            @RequestParam(defaultValue = "10") int number) {

        logger.info("非同期計算をリクエスト受信: {}", number);

        // 非同期計算を実行し、結果を返す
        return asyncService.executeAsyncCalculation(number)
                .thenApply(result -> "計算結果: " + number + " × 2 = " + result);
    }

    /**
     * 複数の非同期処理を並列実行
     *
     * @return CompletableFuture<String> 全ての結果
     */
    @GetMapping("/parallel")
    public CompletableFuture<String> executeParallelTasks() {
        logger.info("並列非同期処理をリクエスト受信");

        // 複数の非同期処理を並列実行
        CompletableFuture<String> user1 = asyncService.fetchUserData("user001");
        CompletableFuture<String> user2 = asyncService.fetchUserData("user002");
        CompletableFuture<String> user3 = asyncService.fetchUserData("user003");

        // すべての非同期処理が完了するのを待つ
        return CompletableFuture.allOf(user1, user2, user3)
                .thenApply(v -> {
                    try {
                        String result = String.format(
                                "全ての非同期処理が完了しました:\n- %s\n- %s\n- %s",
                                user1.get(),
                                user2.get(),
                                user3.get()
                        );
                        logger.info("並列非同期処理が完了");
                        return result;
                    } catch (Exception e) {
                        logger.error("並列非同期処理でエラー発生", e);
                        return "エラーが発生しました: " + e.getMessage();
                    }
                });
    }

    /**
     * 健全性チェック用エンドポイント
     *
     * @return ステータスメッセージ
     */
    @GetMapping("/health")
    public String health() {
        return "非同期処理サービスは正常に動作しています";
    }
}
