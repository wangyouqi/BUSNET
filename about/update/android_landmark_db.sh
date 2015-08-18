#!/bin/sh

cd /home/bus/busnet/db/android_landmark_db

mysql --skip-column-names bus4        < export_command.sql > import.sql
sqlite3 update_`date +%Y%m%d`.sqlite3 < import_command.sql > update.txt
sqlite3 update_`date +%Y%m%d`.sqlite3 < import.sql
mv update_`date +%Y%m%d`.sqlite3 /home/bus//busnet/public/android_landmark_db/
mv update.txt                    /home/bus//busnet/public/android_landmark_db/
