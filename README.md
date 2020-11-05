# sendgridjp-java-example

本コードはSendGridのSMTPサービスの利用サンプルです。

## 使い方

```bash
git clone http://github.com/sendgridjp/sendgrid-smtp-java-example.git
cd sendgrid-smtp-java-example
cp ./app/.env.example ./app/.env
# .envファイルを編集してください
gradle build

# テキストメールを送る
$ gradle run --args "JavaMailTextExample"

# 携帯キャリア向けにデコメを送る
$ gradle run --args "JavaMailDecoExample"

# 一般的なマルチパートメールを送る
$ gradle run --args "JavaMailMultipartExample"
```

## .envファイルの編集
.envファイルは以下のようになっています。

```bash
SENDGRID_USERNAME=apikey
SENDGRID_PASSWORD=your_apikey
TOS=hoge1@example.com,hoge2@example.com,hoge3@example.com,hoge4@example.com
NAMES=名前1,名前2,名前3,名前4
FROM=you@youremail.com
```
SENDGRID_USERNAME:変更する必要はありません
SENDGRID_PASSWORD:SendGridのAPIキーを指定してください（Mail Sendパーミッションが必要）。  
TOS:宛先をカンマ区切りで指定してください。  
NAMES:宛先毎の宛名をカンマ区切りで指定してください。
FROM:送信元アドレスを指定してください。  
