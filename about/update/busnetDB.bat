@echo off
echo ����ANDROID��BUSTNET�̃f�[�^�x�[�X�����܂�......
echo �p�X���[�h����͂��Ă��������B

mysql --skip-column-names -hlocalhost -uroot -p123 -Dbus <export_command.sql >busnet_insert.sql
echo ���΂炭���҂����������B

SQLITE3 update_%date:~0,4%%date:~5,2%%date:~8,2%.sqlite3 <import_command.sql >update.txt
SQLITE3 update_%date:~0,4%%date:~5,2%%date:~8,2%.sqlite3 <busnet_insert.sql

echo �f�[�^�x�[�X�����I���܂����B
pause
