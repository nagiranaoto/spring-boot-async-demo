# Spring Boot @Async 基本サンプル

Spring Bootの`@Async`アノテーションを使った非同期処理の基本を学ぶためのシンプルなサンプルアプリケーションです。

## このサンプルで学べること

- `@Async`アノテーションの基本的な使い方
- 非同期処理と同期処理の違い
- スレッドプールの設定方法
- ログで非同期実行を確認する方法

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
│       │               ├── AsyncDemoApplication.java     # メインクラス（@EnableAsync）
│       │               ├── config/
│       │               │   └── AsyncConfig.java          # スレッドプール設定
│       │               ├── controller/
│       │               │   └── AsyncController.java      # RESTエンドポイント
│       │               └── service/
│       │                   └── AsyncService.java         # 非同期処理の実装
│       └── resources/
│           └── application.properties                     # ログ設定
```

## @Asyncの基本設定

### 1. @EnableAsyncで非同期機能を有効化

```java
@SpringBootApplication
@EnableAsync  // これを追加
public class AsyncDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AsyncDemoApplication.class, args);
    }
}
```

### 2. スレッドプールを設定（任意だが推奨）

```java
@Configuration
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);      // 常に維持するスレッド数
        executor.setMaxPoolSize(10);      // 最大スレッド数
        executor.setQueueCapacity(100);   // キュー容量
        executor.setThreadNamePrefix("async-");  // スレッド名
        executor.initialize();
        return executor;
    }
}
```

### 3. @Asyncでメソッドを非同期化

```java
@Service
public class AsyncService {

    // 非同期処理（別スレッドで実行される）
    @Async("taskExecutor")
    public void executeAsyncTask(String message) {
        // 重い処理をここに書く
        Thread.sleep(3000);
    }

    // 同期処理（通常のメソッド）
    public void executeSyncTask(String message) {
        // 重い処理をここに書く
        Thread.sleep(3000);
    }
}
```

## 実行方法

### 必要な環境
- Java 17以上
- Maven 3.6以上

### アプリケーションの起動

```bash
cd spring-boot-async-demo
mvn spring-boot:run
```

アプリケーションは `http://localhost:8080` で起動します。

## 動作確認

### 1. 非同期処理のテスト

```bash
curl "http://localhost:8080/api/async?message=テスト"
```

**期待される動作**:
- レスポンスが**即座に**返る（3秒待たない）
- バックグラウンドで処理が実行される

**ログ出力例**:
```
========================================
[1] リクエスト受信 - メッセージ: テスト - スレッド: http-nio-8080-exec-1
[2] レスポンス返却 - スレッド: http-nio-8080-exec-1 ★非同期処理の完了を待たない★
========================================
[3] 非同期タスク開始 - メッセージ: テスト - スレッド: async-1
[4] 重い処理を実行中... (3秒待機) - スレッド: async-1
[5] 非同期タスク完了 - メッセージ: テスト - スレッド: async-1
```

**ポイント**:
- `[1]→[2]` が即座に実行される（レスポンスがすぐ返る）
- `[3]→[5]` は別スレッド（`async-1`）で実行される

### 2. 同期処理のテスト（比較用）

```bash
curl "http://localhost:8080/api/sync?message=テスト"
```

**期待される動作**:
- レスポンスが**3秒後に**返る（処理完了まで待つ）

**ログ出力例**:
```
========================================
[同期1] リクエスト受信 - メッセージ: テスト - スレッド: http-nio-8080-exec-1
[同期] タスク開始 - メッセージ: テスト - スレッド: http-nio-8080-exec-1
[同期] 重い処理を実行中... (3秒待機) - スレッド: http-nio-8080-exec-1
[同期] タスク完了 - メッセージ: テスト - スレッド: http-nio-8080-exec-1
[同期2] レスポンス返却 - スレッド: http-nio-8080-exec-1 ★処理完了後にレスポンス★
========================================
```

**ポイント**:
- すべて同じスレッド（`http-nio-8080-exec-1`）で実行される
- 処理が完了してからレスポンスが返る

## 非同期処理と同期処理の違い

| 項目 | 非同期処理（@Async） | 同期処理（通常） |
|------|---------------------|----------------|
| **レスポンス時間** | 即座に返る | 処理完了後に返る |
| **実行スレッド** | 別スレッド（async-1など） | 同じスレッド |
| **使用例** | メール送信、ログ記録、重い計算 | 通常のAPI処理 |

## @Asyncの注意点

### ✅ 動作する場合
```java
// Springが管理するBeanを経由して呼び出す
@Autowired
private AsyncService asyncService;

asyncService.executeAsyncTask("OK");  // ✅ 非同期で実行される
```

### ❌ 動作しない場合
```java
// 同じクラス内から呼び出す
@Service
public class MyService {
    @Async
    public void asyncMethod() { }

    public void normalMethod() {
        this.asyncMethod();  // ❌ 非同期にならない
    }
}

// new で直接インスタンス化
AsyncService service = new AsyncService();
service.executeAsyncTask("NG");  // ❌ 非同期にならない
```

## よくある質問

### Q1: スレッドプール名を指定しないとどうなる？
```java
@Async  // スレッドプール名を省略
public void method() { }
```
→ デフォルトのスレッドプールが使われます。本番環境では明示的に指定することを推奨します。

### Q2: @Asyncメソッドの戻り値は？
- `void`: 結果を返さない（このサンプルで使用）
- `CompletableFuture<T>`: 結果を非同期で受け取る（応用編）

### Q3: スレッド数はいくつにすべき？
- CPU集約型: CPUコア数程度
- I/O集約型（DB、外部API呼び出し）: CPUコア数 × 2〜4程度

## 参考資料

- [Spring Boot公式ドキュメント - Creating Asynchronous Methods](https://spring.io/guides/gs/async-method/)
- [Spring Framework @Async API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/scheduling/annotation/Async.html)

## ライセンス

このプロジェクトは学習用のサンプルコードです。自由にご利用ください。
