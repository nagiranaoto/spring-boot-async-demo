package com.example.asyncdemo.controller;

import com.example.asyncdemo.service.AsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 非同期処理を呼び出すRESTコントローラー
 *
 * @Asyncの基本動作を理解するためのサンプル
 */
@RestController
@RequestMapping("/api")
public class AsyncController {

    private static final Logger logger = LoggerFactory.getLogger(AsyncController.class);

    @Autowired
    private AsyncService asyncService;

    /**
     * 非同期処理を実行するエンドポイント
     *
     * このエンドポイントは非同期処理の完了を待たずにすぐにレスポンスを返す
     *
     * @param message 処理するメッセージ
     * @return レスポンスメッセージ
     */
    @GetMapping("/async")
    public String executeAsyncTask(@RequestParam(defaultValue = "テストメッセージ") String message) {
        logger.info("========================================");
        logger.info("[1] リクエスト受信 - メッセージ: {} - スレッド: {}",
            message, Thread.currentThread().getName());

        // 非同期処理を開始（すぐに制御が返る）
        asyncService.executeAsyncTask(message);

        logger.info("[2] レスポンス返却 - スレッド: {} ★非同期処理の完了を待たない★",
            Thread.currentThread().getName());
        logger.info("========================================");

        return "非同期タスクを開始しました: " + message;
    }

    /**
     * 同期処理を実行するエンドポイント（比較用）
     *
     * このエンドポイントは処理が完了するまでレスポンスを返さない
     *
     * @param message 処理するメッセージ
     * @return レスポンスメッセージ
     */
    @GetMapping("/sync")
    public String executeSyncTask(@RequestParam(defaultValue = "テストメッセージ") String message) {
        logger.info("========================================");
        logger.info("[同期1] リクエスト受信 - メッセージ: {} - スレッド: {}",
            message, Thread.currentThread().getName());

        // 同期処理を実行（完了まで待つ）
        asyncService.executeSyncTask(message);

        logger.info("[同期2] レスポンス返却 - スレッド: {} ★処理完了後にレスポンス★",
            Thread.currentThread().getName());
        logger.info("========================================");

        return "同期タスクが完了しました: " + message;
    }

    /**
     * 健全性チェック用エンドポイント
     *
     * @return ステータスメッセージ
     */
    @GetMapping("/health")
    public String health() {
        return "Spring Boot Async Demo Application is running!";
    }
}
