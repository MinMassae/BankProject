🏦 BankProject — 個人銀行システム
📘 プロジェクト概要
Spring Bootをベースに開発したオンライン銀行システムです。
会員登録、ログイン、口座開設、振込、OTP認証、バッチ処理、そしてリアルタイム為替レート表示までを含む総合的なプロジェクトです。
サーバーはAPIサーバー（ポート8080）とVIEWサーバー（ポート8081）に分離し、疎結合構成を実現しました。

⚙️ 使用技術スタック
分類	技術
開発言語	Java 17
フレームワーク	Spring Boot、Spring Security
DB連携	MyBatis
Viewテンプレート	Thymeleaf
認証方式	JWT（API）、Spring Security セッション（VIEW）
DB	MySQL
バッチ処理	Spring Batch
為替	NAVER金融の為替レートスクレイピング / REST API
その他	OTP、BCrypt、fetchベースのAPI通信

🌐 サーバー構成
サーバー	ポート	説明
APIサーバー	8080	REST API提供およびJWT認証処理
VIEWサーバー	8081	Thymeleafビューのレンダリングおよびセッション管理

🚀 主要機能フロー
1. 会員登録（POST /api/members）

ユーザー入力：ID、パスワード、氏名

パスワードはBCryptで暗号化後DB保存

主なコンポーネント：

MemberController → HTTPリクエスト処理

MemberService → ビジネスロジック

MemberMapper.xml → MyBatis SQLマッピング

2. ログイン（POST /api/auth/login）

ID/パスワード認証 → JWTトークン生成 (JwtUtil)

クライアントはlocalStorageにJWT保存

APIリクエスト時はAuthorization: Bearer <token>ヘッダー付与

トークン検証はJwtAuthenticationFilterで実施

3. リアルタイム為替レート取得

ログイン後、口座残高に加え、通貨別換算額を表示

NAVER金融API/スクレイピングで最新のドル・円為替取得

主なコンポーネント：

ExchangeRateService → HTTP GET & JSONパース

ExchangeRateController → APIレスポンス

当初は韓国輸出入銀行APIを利用したが、安定性の問題によりNAVER APIに変更

4. 口座開設（POST /api/accounts）

ログイン済みユーザーが口座申請

必須項目：職業、利用目的、PINコード

ユニークな口座番号生成 → DB保存

5. 口座照会（GET /api/me）

JWT検証後、ユーザー情報 + 口座一覧を返却

VIEW側では「口座なし」表示またはリスト表示

6. 振込準備（VIEW: /transfer）

入金口座、振込金額、PIN入力

OTP認証未完了の場合はメイン画面へ戻る

7. OTP認証（POST /api/otp/verify）

OTP入力とサーバー側のOTP照合

成功時、セッションにOTP認証状態を保持

8. 振込実行（POST /api/transfer）

OTP認証確認後、ビジネスロジック実行

残高確認 → transfer_history保存 → account更新

9. 振込履歴照会（GET /api/transfer/history）

入出金を含む履歴を取得、時間順で表示

10. OTPバッチ処理（Scheduled）

OTPは6桁ランダム生成、10秒ごとに更新

ログアウト時はOTP認証情報削除

⛏ 動作フロー（OTP）
OtpService：OTP生成と検証

OtpController：APIエンドポイント処理

@Scheduled：OTPの自動更新

🛡️ 認証とセキュリティ
ログイン時JWT発行 → 以降のAPIリクエストで認証

VIEWサーバーはSpring Securityセッションベース

振込などの重要操作はPIN + OTP認証必須

🗂️ DBスキーマ概要
テーブル名	説明
members	ユーザーID、パスワード、PINなど
account	口座番号、残高、職業、目的
transfer_history	振込履歴
interest_history	利息履歴
report_history	分析レポート履歴
account_history	入出金履歴

🎯 実行方法（簡易）
bash
복사
편집
# 1. DB設定

# 2. APIサーバー（8080）起動
./gradlew build
java -jar build/libs/bank-api.jar

# 3. VIEWサーバー（8081）起動
java -jar build/libs/bank-view.jar
