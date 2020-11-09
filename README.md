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
SUBJECT=こんにちはSendGrid
# "ISO-2022-JP" or "UTF-8"
CHARSET=UTF-8
# "base64", "7bit" or "quoted-printable"
ENCODE=base64
```

- **SENDGRID_USERNAME**:変更する必要はありません
- **SENDGRID_PASSWORD**:SendGridのAPIキーを指定してください（Mail Sendパーミッションが必要）。  
- **TOS**:宛先をカンマ区切りで指定してください。  
- **NAMES**:宛先毎の宛名をカンマ区切りで指定してください。
- **FROM**:送信元アドレスを指定してください。  
- **SUBJECT**:メールの件名を指定してください
- **CHARSET**:文字セットを指定してください。指定可能なのは、"ISO-2022-JP"や"UTF-8"などです。
- **ENCODE**:エンコード方式を指定してください。指定可能なのは、"base64"、"quoted-printable"、"7bit"などです。