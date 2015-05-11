# sendgridjp-java-example

本コードはSendGridのSMTPサービスの利用サンプルです。

## 使い方

```bash
git clone http://github.com/sendgridjp/sendgridjp-smtp-java-example.git
cd sendgridjp-smtp-java-example
cp .env.example .env
# .envファイルを編集してください
gradle build
./run-text.sh
./run-multipart.sh
./run-deco.sh
```

## .envファイルの編集
.envファイルは以下のようになっています。

```bash
SENDGRID_USERNAME=your_username
SENDGRID_PASSWORD=your_password
TOS=hoge@hogehoge.com,fuga@fugafuga.com,piyo@piyopiyo.com,hogera@hogera.com
NAMES=名前1,名前2,名前3,名前4
FROM=you@youremail.com
```
SENDGRID_USERNAME:SendGridのユーザ名を指定してください。  
SENDGRID_PASSWORD:SendGridのパスワードを指定してください。  
TOS:宛先をカンマ区切りで指定してください。  
NAMES:宛先毎の宛名をカンマ区切りで指定してください。
FROM:送信元アドレスを指定してください。  
