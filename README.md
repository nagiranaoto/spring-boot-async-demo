# Spring Boot 非同期処理デモアプリケーション

Spring Bootの`@Async`アノテーションを使用した非同期処理の実装サンプルです。

## プロジェクト構成

```
spring-boot-async-demo/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           └── asyncdemo/
│       │               ├── AsyncDemoApplication.java     # メインアプリケーション
│       │               ├── config/
│       │               │   └── AsyncConfig.java          # 非同期設定
│       │               ├── controller/
│       │               │   └── AsyncController.java      # RESTコントローラー
│       │               └── service/
│       │                   └── AsyncService.java         # 非同期サービス
│       └── resources/
│           └── application.properties                     # アプリケーション設定
```

## 主要機能

### 1. 非同期処理の基本設定

- **@EnableAsync**: `AsyncDemoApplication.java`でSpringの非同期機能を有効化
- **ThreadPoolTaskExecutor**: `AsyncConfig.java`でスレッドプールを設定
  - コアプールサイズ: 5
  - 最大プールサイズ: 10
  - キュー容量: 100

### 2. 非同期処理のパターン

#### パターン1: 戻り値なしの非同期処理
```java
@Async("taskExecutor")
public void executeAsyncTask(String message) {
    // 非同期で実行される処理
}
```

#### パターン2: CompletableFutureを返す非同期処理
```java
@Async("taskExecutor")
public CompletableFuture<Integer> executeAsyncCalculation(int number) {
    // 計算処理
    return CompletableFuture.completedFuture(result);
}
```

#### パターン3: 複数の非同期処理を並列実行
```java
CompletableFuture<String> task1 = asyncService.fetchUserData("user1");
CompletableFuture<String> task2 = asyncService.fetchUserData("user2");
CompletableFuture.allOf(task1, task2).thenApply(...);
```

## ビルドと実行

### 必要な環境
- Java 17以上
- Maven 3.6以上

### ビルド
```bash
cd spring-boot-async-demo
mvn clean install
```

### 実行
```bash
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## APIエンドポイント

### 1. 非同期タスク実行（戻り値なし）
```bash
# リクエスト
curl "http://localhost:8080/api/async/task?message=テストメッセージ"

# レスポンス（即座に返る）
非同期タスクを開始しました: テストメッセージ
```

### 2. 非同期計算（結果を返す）
```bash
# リクエスト
curl "http://localhost:8080/api/async/calculate?number=5"

# レスポンス（2秒後に返る）
計算結果: 5 × 2 = 10
```

### 3. 並列非同期処理
```bash
# リクエスト
curl "http://localhost:8080/api/async/parallel"

# レスポンス（約1.5秒後に返る - 並列実行のため）
全ての非同期処理が完了しました:
- ユーザー[user001]のデータ
- ユーザー[user002]のデータ
- ユーザー[user003]のデータ
```

### 4. 健全性チェック
```bash
# リクエスト
curl "http://localhost:8080/api/async/health"

# レスポンス
非同期処理サービスは正常に動作しています
```

## 非同期処理の仕組み

### @Asyncアノテーション
- メソッドに`@Async`を付けることで、そのメソッドは別スレッドで実行される
- 指定したExecutor（ここでは"taskExecutor"）のスレッドプールを使用

### CompletableFuture
- 非同期処理の結果を受け取るためのFutureオブジェクト
- `thenApply()`, `thenCompose()`, `allOf()`などのメソッドで処理を連結可能

### ThreadPoolTaskExecutor
- スレッドプールを管理し、非同期タスクを効率的に実行
- 設定可能なパラメータ:
  - `corePoolSize`: 常に維持するスレッド数
  - `maxPoolSize`: 最大スレッド数
  - `queueCapacity`: タスクキューの容量

## ログ出力

アプリケーション実行時、以下のようなログが出力されます:

```
2025-12-13 12:00:00 - 非同期タスクをリクエスト受信: テストメッセージ
2025-12-13 12:00:00 - 非同期タスクを開始: テストメッセージ - スレッド: async-1
2025-12-13 12:00:03 - 非同期タスクを完了: テストメッセージ
```

スレッド名に"async-"というプレフィックスが付いていることで、非同期スレッドで実行されていることが確認できます。

## 参考資料

- [Spring Boot公式ドキュメント - Creating Asynchronous Methods](https://spring.io/guides/gs/async-method/)
- [Spring Framework @Async API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Async.html)
- [@EnableAsync API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/EnableAsync.html)

## ライセンス

このプロジェクトはデモ用のサンプルコードです。
