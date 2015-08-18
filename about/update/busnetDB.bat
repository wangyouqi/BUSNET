@echo off
echo いまANDROID版BUSTNETのデータベースを作ります......
echo パスワードを入力してください。

mysql --skip-column-names -hlocalhost -uroot -p123 -Dbus <export_command.sql >busnet_insert.sql
echo しばらくお待ちください。

SQLITE3 update_%date:~0,4%%date:~5,2%%date:~8,2%.sqlite3 <import_command.sql >update.txt
SQLITE3 update_%date:~0,4%%date:~5,2%%date:~8,2%.sqlite3 <busnet_insert.sql

echo データベースを作り終わりました。
pause
