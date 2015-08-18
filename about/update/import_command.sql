 --;
PRAGMA foreign_keys=OFF;

-- ----------------------------
-- Table structure for "landmark"
-- ----------------------------
CREATE TABLE "landmark" (
"id"  integer PRIMARY KEY AUTOINCREMENT,
"word"  varchar,
"int_landmark_id"  integer NOT NULL DEFAULT 0,
"vc_landmark_name"  varchar(250) NOT NULL,
"vc_landmark_yomi"  varchar(250) NOT NULL DEFAULT "",
"vc_landmark_addr"  varchar(250) NOT NULL DEFAULT "",
"vc_landmark_type"  varchar(50) NOT NULL DEFAULT "",
"int_landmark_class"  integer NOT NULL DEFAULT 0,
"int_landmark_lat"  integer NOT NULL DEFAULT 0,
"int_landmark_lng"  integer NOT NULL DEFAULT 0,
"f_landmark_latitude"  float(50) NOT NULL DEFAULT 0,
"f_landmark_longitude"  float(50) NOT NULL DEFAULT 0,
"tx_landmark_comment"  varchar,
"vc_landmark_figure"  varchar,
"vc_landmark_link"  varchar,
"f_landmark_fix"  float NOT NULL DEFAULT 1,
"dt_create_date"  datetime NOT NULL DEFAULT (datetime('now', 'localtime')),
"dt_create_dts"  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------
-- Table structure for "classcode"
-- ----------------------------
CREATE TABLE "classcode" (
"ID"  INTEGER NOT NULL,
"sector_ID"  INTEGER NOT NULL,
"subsector_ID"  INTEGER NOT NULL,
"group_ID"  INTEGER NOT NULL,
"name"  TEXT(50) NOT NULL,
"detail"  TEXT(250) NOT NULL
);
DELETE FROM "classcode";

-- ----------------------------
-- Table structure for "main"."regdevice"
-- ----------------------------
CREATE TABLE "regdevice" (
"vc_device_serial"  TEXT(50) NOT NULL,
"vc_device_comment"  TEXT,
"int_start_id"  INTEGER NOT NULL DEFAULT 0,
"vc_start_name"  TEXT(250) NOT NULL DEFAULT ""
);
DELETE FROM "regdevice";

-- ----------------------------
-- Records of classcode
-- ----------------------------
BEGIN TRANSACTION;
INSERT INTO "classcode" VALUES (10101, 1, 2, 0, '駅・交通機関', '駅');
INSERT INTO "classcode" VALUES (10102, 1, 3, 0, '駅・交通機関', '空港');
INSERT INTO "classcode" VALUES (10103, 1, 4, 0, '駅・交通機関', 'フェリー・旅客船');
INSERT INTO "classcode" VALUES (10104, 1, 5, 0, '駅・交通機関', '交差点');
INSERT INTO "classcode" VALUES (10105, 1, 6, 0, '駅・交通機関', '港・漁港・港湾');
INSERT INTO "classcode" VALUES (10106, 1, 7, 0, '駅・交通機関', 'バス停');
INSERT INTO "classcode" VALUES (10199, 1, 8, 0, '駅・交通機関', 'その他(駅・交通機関)');
INSERT INTO "classcode" VALUES (10201, 9, 10, 0, '公共施設・暮らし', '官公庁');
INSERT INTO "classcode" VALUES (10202, 9, 11, 0, '公共施設・暮らし', '都道府県庁');
INSERT INTO "classcode" VALUES (10203, 9, 12, 0, '公共施設・暮らし', '市役所・区役所・町村役場');
INSERT INTO "classcode" VALUES (10204, 9, 13, 0, '公共施設・暮らし', '警察署');
INSERT INTO "classcode" VALUES (10205, 9, 14, 0, '公共施設・暮らし', '保健所');
INSERT INTO "classcode" VALUES (10206, 9, 15, 0, '公共施設・暮らし', '消防署');
INSERT INTO "classcode" VALUES (10207, 9, 16, 0, '公共施設・暮らし', '税務署・会計事務所');
INSERT INTO "classcode" VALUES (10208, 9, 17, 0, '公共施設・暮らし', '法務局・法律事務所');
INSERT INTO "classcode" VALUES (10209, 9, 18, 0, '公共施設・暮らし', '裁判所');
INSERT INTO "classcode" VALUES (10210, 9, 19, 0, '公共施設・暮らし', '図書館');
INSERT INTO "classcode" VALUES (10211, 9, 20, 0, '公共施設・暮らし', 'ハローワーク');
INSERT INTO "classcode" VALUES (10212, 9, 21, 0, '公共施設・暮らし', '不動産');
INSERT INTO "classcode" VALUES (10213, 9, 22, 0, '公共施設・暮らし', '公民館・集会所');
INSERT INTO "classcode" VALUES (10214, 9, 23, 0, '公共施設・暮らし', '電気・ガス・水道・電話');
INSERT INTO "classcode" VALUES (10215, 9, 24, 0, '公共施設・暮らし', '報道機関');
INSERT INTO "classcode" VALUES (10299, 9, 25, 0, '公共施設・暮らし', 'その他(公共施設・暮らし)');
INSERT INTO "classcode" VALUES (10301, 26, 27, 0, '医療・福祉', '病院・医院・診療所');
INSERT INTO "classcode" VALUES (10302, 26, 28, 0, '医療・福祉', '歯科医院');
INSERT INTO "classcode" VALUES (10303, 26, 29, 0, '医療・福祉', '動物病院');
INSERT INTO "classcode" VALUES (10304, 26, 30, 0, '医療・福祉', '接骨・針灸院');
INSERT INTO "classcode" VALUES (10305, 26, 31, 0, '医療・福祉', '介護・福祉サービス');
INSERT INTO "classcode" VALUES (10399, 26, 32, 0, '医療・福祉', 'その他(医療・福祉)');
INSERT INTO "classcode" VALUES (10401, 33, 34, 0, '学校・習い事', '大学・短期大学');
INSERT INTO "classcode" VALUES (10402, 33, 35, 0, '学校・習い事', '高等専門学校');
INSERT INTO "classcode" VALUES (10403, 33, 36, 0, '学校・習い事', '高校');
INSERT INTO "classcode" VALUES (10404, 33, 37, 0, '学校・習い事', '中学校');
INSERT INTO "classcode" VALUES (10405, 33, 38, 0, '学校・習い事', '小学校');
INSERT INTO "classcode" VALUES (10406, 33, 39, 0, '学校・習い事', '幼稚園・保育園');
INSERT INTO "classcode" VALUES (10407, 33, 40, 0, '学校・習い事', '専修・専門学校');
INSERT INTO "classcode" VALUES (10408, 33, 41, 0, '学校・習い事', '語学学校・教室');
INSERT INTO "classcode" VALUES (10409, 33, 42, 0, '学校・習い事', '塾・予備校');
INSERT INTO "classcode" VALUES (10410, 33, 43, 0, '学校・習い事', '文科系スクール');
INSERT INTO "classcode" VALUES (10411, 33, 44, 0, '学校・習い事', 'スポーツ系スクール');
INSERT INTO "classcode" VALUES (10412, 33, 45, 0, '学校・習い事', 'ペットスクール');
INSERT INTO "classcode" VALUES (10499, 33, 46, 0, '学校・習い事', 'その他(学校・習い事)');
INSERT INTO "classcode" VALUES (10501, 47, 48, 0, '金融機関', '都市銀行');
INSERT INTO "classcode" VALUES (10502, 47, 49, 0, '金融機関', '地方銀行・その他銀行');
INSERT INTO "classcode" VALUES (10503, 47, 50, 0, '金融機関', '信用金庫・信用組合');
INSERT INTO "classcode" VALUES (10504, 47, 51, 0, '金融機関', '証券・保険会社');
INSERT INTO "classcode" VALUES (10505, 47, 52, 0, '金融機関', '消費者金融');
INSERT INTO "classcode" VALUES (10506, 47, 53, 0, '金融機関', 'ATM');
INSERT INTO "classcode" VALUES (10507, 47, 54, 0, '金融機関', '郵便局');
INSERT INTO "classcode" VALUES (10508, 47, 55, 0, '金融機関', '農協');
INSERT INTO "classcode" VALUES (10599, 47, 56, 0, '金融機関', 'その他(金融機関)');
INSERT INTO "classcode" VALUES (10601, 57, 58, 0, 'スポーツ施設', 'ゴルフ場');
INSERT INTO "classcode" VALUES (10602, 57, 59, 0, 'スポーツ施設', '野球場・競技場');
INSERT INTO "classcode" VALUES (10603, 57, 60, 0, 'スポーツ施設', 'スキー場');
INSERT INTO "classcode" VALUES (10604, 57, 61, 0, 'スポーツ施設', 'ゴルフ練習場');
INSERT INTO "classcode" VALUES (10605, 57, 62, 0, 'スポーツ施設', 'スケート場');
INSERT INTO "classcode" VALUES (10606, 57, 63, 0, 'スポーツ施設', 'マリーナ');
INSERT INTO "classcode" VALUES (10607, 57, 64, 0, 'スポーツ施設', 'テニスコート');
INSERT INTO "classcode" VALUES (10608, 57, 65, 0, 'スポーツ施設', 'バッティングセンター');
INSERT INTO "classcode" VALUES (10609, 57, 66, 0, 'スポーツ施設', 'サッカー場');
INSERT INTO "classcode" VALUES (10610, 57, 67, 0, 'スポーツ施設', '体育館');
INSERT INTO "classcode" VALUES (10611, 57, 68, 0, 'スポーツ施設', 'フィットネスクラブ');
INSERT INTO "classcode" VALUES (10612, 57, 69, 0, 'スポーツ施設', 'サーキット場');
INSERT INTO "classcode" VALUES (10699, 57, 70, 0, 'スポーツ施設', 'その他(スポーツ施設)');
INSERT INTO "classcode" VALUES (10701, 71, 72, 73, 'レジャー・遊ぶ', 'レジャー➡公園・緑地');
INSERT INTO "classcode" VALUES (10702, 71, 72, 74, 'レジャー・遊ぶ', 'レジャー➡キャンプ場');
INSERT INTO "classcode" VALUES (10703, 71, 72, 75, 'レジャー・遊ぶ', 'レジャー➡オートキャンプ場');
INSERT INTO "classcode" VALUES (10704, 71, 72, 76, 'レジャー・遊ぶ', 'レジャー➡ホール・国際展示場');
INSERT INTO "classcode" VALUES (10705, 71, 72, 77, 'レジャー・遊ぶ', 'レジャー➡海水浴場');
INSERT INTO "classcode" VALUES (10706, 71, 72, 78, 'レジャー・遊ぶ', 'レジャー➡プール');
INSERT INTO "classcode" VALUES (10707, 71, 72, 79, 'レジャー・遊ぶ', 'レジャー➡釣り堀');
INSERT INTO "classcode" VALUES (10708, 71, 72, 80, 'レジャー・遊ぶ', 'レジャー➡レンタサイクル');
INSERT INTO "classcode" VALUES (10709, 71, 72, 81, 'レジャー・遊ぶ', 'レジャー➡地ワイン・地ビール工房');
INSERT INTO "classcode" VALUES (10710, 71, 72, 82, 'レジャー・遊ぶ', 'レジャー➡牧場');
INSERT INTO "classcode" VALUES (10711, 71, 72, 83, 'レジャー・遊ぶ', 'レジャー➡灯台');
INSERT INTO "classcode" VALUES (10712, 71, 84, 85, 'レジャー・遊ぶ', '遊ぶ➡遊園地');
INSERT INTO "classcode" VALUES (10713, 71, 84, 86, 'レジャー・遊ぶ', '遊ぶ➡カラオケ');
INSERT INTO "classcode" VALUES (10714, 71, 84, 87, 'レジャー・遊ぶ', '遊ぶ➡ライブハウス');
INSERT INTO "classcode" VALUES (10715, 71, 84, 88, 'レジャー・遊ぶ', '遊ぶ➡ゲームセンター');
INSERT INTO "classcode" VALUES (10716, 71, 84, 89, 'レジャー・遊ぶ', '遊ぶ➡クラブ・ディスコ');
INSERT INTO "classcode" VALUES (10717, 71, 84, 90, 'レジャー・遊ぶ', '遊ぶ➡ボーリング場');
INSERT INTO "classcode" VALUES (10718, 71, 84, 91, 'レジャー・遊ぶ', '遊ぶ➡ビリヤード');
INSERT INTO "classcode" VALUES (10719, 71, 92, 93, 'レジャー・遊ぶ', 'リフレッシュ➡温泉');
INSERT INTO "classcode" VALUES (10720, 71, 92, 94, 'レジャー・遊ぶ', 'リフレッシュ➡サウナ・ヘルスセンター');
INSERT INTO "classcode" VALUES (10721, 71, 92, 95, 'レジャー・遊ぶ', 'リフレッシュ➡リゾート施設');
INSERT INTO "classcode" VALUES (10722, 71, 96, 97, 'レジャー・遊ぶ', '見る・観る➡動物園');
INSERT INTO "classcode" VALUES (10723, 71, 96, 98, 'レジャー・遊ぶ', '見る・観る➡植物園');
INSERT INTO "classcode" VALUES (10724, 71, 96, 99, 'レジャー・遊ぶ', '見る・観る➡水族館');
INSERT INTO "classcode" VALUES (10725, 71, 96, 100, 'レジャー・遊ぶ', '見る・観る➡映画館');
INSERT INTO "classcode" VALUES (10726, 71, 96, 101, 'レジャー・遊ぶ', '見る・観る➡博物館・資料館');
INSERT INTO "classcode" VALUES (10727, 71, 96, 102, 'レジャー・遊ぶ', '見る・観る➡美術館');
INSERT INTO "classcode" VALUES (10728, 71, 96, 103, 'レジャー・遊ぶ', '見る・観る➡天文台・プラネタリウム');
INSERT INTO "classcode" VALUES (10729, 71, 96, 104, 'レジャー・遊ぶ', '見る・観る➡劇場');
INSERT INTO "classcode" VALUES (10730, 71, 105, 106, 'レジャー・遊ぶ', 'ギャンブル➡競馬・競輪・競艇');
INSERT INTO "classcode" VALUES (10731, 71, 105, 107, 'レジャー・遊ぶ', 'ギャンブル➡オートレース場');
INSERT INTO "classcode" VALUES (10732, 71, 105, 108, 'レジャー・遊ぶ', 'ギャンブル➡パチンコ');
INSERT INTO "classcode" VALUES (10733, 71, 105, 109, 'レジャー・遊ぶ', 'ギャンブル➡雀荘');
INSERT INTO "classcode" VALUES (10734, 71, 110, 0, 'レジャー・遊ぶ', '占い');
INSERT INTO "classcode" VALUES (10735, 71, 111, 0, 'レジャー・遊ぶ', 'マンガ喫茶');
INSERT INTO "classcode" VALUES (10736, 71, 112, 0, 'レジャー・遊ぶ', 'ペットの泊まれるキャンプ場');
INSERT INTO "classcode" VALUES (10737, 71, 113, 0, 'レジャー・遊ぶ', 'ドックラン・スパ');
INSERT INTO "classcode" VALUES (10799, 71, 114, 0, 'レジャー・遊ぶ', 'その他(レジャー・遊ぶ)');
INSERT INTO "classcode" VALUES (20000, 71, 284, 285, 'レジャー・遊ぶ', '文化財➡国宝');
INSERT INTO "classcode" VALUES (20001, 71, 284, 286, 'レジャー・遊ぶ', '文化財➡重要文化財');
INSERT INTO "classcode" VALUES (20002, 71, 284, 287, 'レジャー・遊ぶ', '文化財➡特別史跡');
INSERT INTO "classcode" VALUES (20003, 71, 284, 288, 'レジャー・遊ぶ', '文化財➡史跡');
INSERT INTO "classcode" VALUES (20004, 71, 284, 289, 'レジャー・遊ぶ', '文化財➡天然記念物');
INSERT INTO "classcode" VALUES (20005, 71, 284, 290, 'レジャー・遊ぶ', '文化財➡重要有形民俗文化財');
INSERT INTO "classcode" VALUES (20006, 71, 284, 291, 'レジャー・遊ぶ', '文化財➡重要無形民俗文化財');
INSERT INTO "classcode" VALUES (20007, 71, 284, 292, 'レジャー・遊ぶ', '文化財➡無形民俗文化財');
INSERT INTO "classcode" VALUES (20008, 71, 284, 293, 'レジャー・遊ぶ', '文化財➡保護文化財');
INSERT INTO "classcode" VALUES (20009, 71, 284, 294, 'レジャー・遊ぶ', '文化財➡国登録有形文化財');
INSERT INTO "classcode" VALUES (20010, 71, 284, 295, 'レジャー・遊ぶ', '文化財➡無形文化財');
INSERT INTO "classcode" VALUES (20011, 71, 284, 296, 'レジャー・遊ぶ', '文化財➡有形民俗文化財');
INSERT INTO "classcode" VALUES (10801, 115, 116, 0, '宿泊', 'ホテル');
INSERT INTO "classcode" VALUES (10802, 115, 117, 0, '宿泊', 'ビジネスホテル');
INSERT INTO "classcode" VALUES (10803, 115, 118, 0, '宿泊', '旅館・民宿');
INSERT INTO "classcode" VALUES (10804, 115, 119, 0, '宿泊', 'カプセルホテル');
INSERT INTO "classcode" VALUES (10805, 115, 120, 0, '宿泊', 'ペンション');
INSERT INTO "classcode" VALUES (10806, 115, 121, 0, '宿泊', '公共宿舎');
INSERT INTO "classcode" VALUES (10807, 115, 122, 0, '宿泊', 'ペットの泊まれる宿');
INSERT INTO "classcode" VALUES (10899, 115, 123, 0, '宿泊', 'その他(宿泊)');
INSERT INTO "classcode" VALUES (10901, 124, 125, 0, 'グルメ', 'ファーストフード');
INSERT INTO "classcode" VALUES (10902, 124, 126, 0, 'グルメ', 'ファミリーレストラン');
INSERT INTO "classcode" VALUES (10903, 124, 127, 128, 'グルメ', '和食系➡和食');
INSERT INTO "classcode" VALUES (10904, 124, 127, 129, 'グルメ', '和食系➡うどん・そば');
INSERT INTO "classcode" VALUES (10905, 124, 127, 130, 'グルメ', '和食系➡寿司');
INSERT INTO "classcode" VALUES (10906, 124, 127, 131, 'グルメ', '和食系➡お好み焼・もんじゃ・たこ焼');
INSERT INTO "classcode" VALUES (10907, 124, 127, 132, 'グルメ', '和食系➡とんかつ・串かつ料理');
INSERT INTO "classcode" VALUES (10908, 124, 127, 133, 'グルメ', '和食系➡鳥料理');
INSERT INTO "classcode" VALUES (10909, 124, 134, 0, 'グルメ', '洋食');
INSERT INTO "classcode" VALUES (10910, 124, 135, 0, 'グルメ', '中華');
INSERT INTO "classcode" VALUES (10911, 124, 136, 0, 'グルメ', 'ラーメン');
INSERT INTO "classcode" VALUES (10912, 124, 137, 0, 'グルメ', 'カレー');
INSERT INTO "classcode" VALUES (10913, 124, 138, 0, 'グルメ', 'カフェ・喫茶・甘味処');
INSERT INTO "classcode" VALUES (10914, 124, 139, 0, 'グルメ', 'イタリアン');
INSERT INTO "classcode" VALUES (10915, 124, 140, 0, 'グルメ', '焼肉・韓国料理');
INSERT INTO "classcode" VALUES (10916, 124, 141, 0, 'グルメ', 'フレンチ');
INSERT INTO "classcode" VALUES (10917, 124, 142, 0, 'グルメ', 'ステーキハウス');
INSERT INTO "classcode" VALUES (10918, 124, 143, 144, 'グルメ', 'お酒➡居酒屋');
INSERT INTO "classcode" VALUES (10919, 124, 143, 145, 'グルメ', 'お酒➡ビアレストラン・ガーデン');
INSERT INTO "classcode" VALUES (10920, 124, 143, 146, 'グルメ', 'お酒➡バー・レストランバー');
INSERT INTO "classcode" VALUES (10921, 124, 143, 147, 'グルメ', 'お酒➡スナック・クラブ・パブ');
INSERT INTO "classcode" VALUES (10922, 124, 148, 0, 'グルメ', 'エスニック');
INSERT INTO "classcode" VALUES (10923, 124, 149, 0, 'グルメ', '宅配・テイクアウト');
INSERT INTO "classcode" VALUES (10924, 124, 150, 0, 'グルメ', 'ペットの入れるレストラン');
INSERT INTO "classcode" VALUES (10999, 124, 151, 0, 'グルメ', 'その他(グルメ)');
INSERT INTO "classcode" VALUES (11001, 152, 153, 0, 'ショッピング', 'デパート・駅ビル');
INSERT INTO "classcode" VALUES (11002, 152, 154, 0, 'ショッピング', 'アウトレット・ショッピングモール');
INSERT INTO "classcode" VALUES (11003, 152, 155, 156, 'ショッピング', 'ファッション・美容➡メガネ');
INSERT INTO "classcode" VALUES (11004, 152, 155, 157, 'ショッピング', 'ファッション・美容➡靴・衣料品');
INSERT INTO "classcode" VALUES (11005, 152, 155, 158, 'ショッピング', 'ファッション・美容➡理容室・美容室');
INSERT INTO "classcode" VALUES (11006, 152, 155, 159, 'ショッピング', 'ファッション・美容➡エステ・ネイルサロン');
INSERT INTO "classcode" VALUES (11007, 152, 155, 160, 'ショッピング', 'ファッション・美容➡アクセサリー');
INSERT INTO "classcode" VALUES (11008, 152, 155, 161, 'ショッピング', 'ファッション・美容➡ランジェリー');
INSERT INTO "classcode" VALUES (11009, 152, 162, 163, 'ショッピング', '生活➡ディスカウントショップ');
INSERT INTO "classcode" VALUES (11010, 152, 162, 164, 'ショッピング', '生活➡コンビニ');
INSERT INTO "classcode" VALUES (11011, 152, 162, 165, 'ショッピング', '生活➡スーパー');
INSERT INTO "classcode" VALUES (11012, 152, 162, 166, 'ショッピング', '生活➡家電・パソコン');
INSERT INTO "classcode" VALUES (11013, 152, 162, 167, 'ショッピング', '生活➡ホームセンター');
INSERT INTO "classcode" VALUES (11014, 152, 162, 168, 'ショッピング', '生活➡薬局・ドラックストア');
INSERT INTO "classcode" VALUES (11015, 152, 162, 169, 'ショッピング', '生活➡リサイクルショップ');
INSERT INTO "classcode" VALUES (11016, 152, 162, 170, 'ショッピング', '生活➡文具雑貨');
INSERT INTO "classcode" VALUES (11017, 152, 162, 171, 'ショッピング', '生活➡携帯電話ショップ');
INSERT INTO "classcode" VALUES (11018, 152, 162, 172, 'ショッピング', '生活➡クリーニング');
INSERT INTO "classcode" VALUES (11019, 152, 162, 173, 'ショッピング', '生活➡介護・福祉用品');
INSERT INTO "classcode" VALUES (11020, 152, 174, 175, 'ショッピング', '飲食➡酒屋・酒蔵');
INSERT INTO "classcode" VALUES (11021, 152, 174, 176, 'ショッピング', '飲食➡お菓子・ケーキパン');
INSERT INTO "classcode" VALUES (11022, 152, 174, 177, 'ショッピング', '飲食➡市場');
INSERT INTO "classcode" VALUES (11023, 152, 174, 178, 'ショッピング', '飲食➡食料品');
INSERT INTO "classcode" VALUES (11024, 152, 179, 180, 'ショッピング', '趣味➡書店');
INSERT INTO "classcode" VALUES (11025, 152, 179, 181, 'ショッピング', '趣味➡スポーツ用品');
INSERT INTO "classcode" VALUES (11026, 152, 179, 182, 'ショッピング', '趣味➡民芸・工芸品');
INSERT INTO "classcode" VALUES (11027, 152, 179, 183, 'ショッピング', '趣味➡レンタル');
INSERT INTO "classcode" VALUES (11028, 152, 179, 184, 'ショッピング', '趣味➡インテリア');
INSERT INTO "classcode" VALUES (11029, 152, 179, 185, 'ショッピング', '趣味➡バイク・自転車');
INSERT INTO "classcode" VALUES (11030, 152, 179, 186, 'ショッピング', '趣味➡花屋・園芸');
INSERT INTO "classcode" VALUES (11031, 152, 179, 187, 'ショッピング', '趣味➡CD・レコード');
INSERT INTO "classcode" VALUES (11032, 152, 179, 188, 'ショッピング', '趣味➡おもちゃ');
INSERT INTO "classcode" VALUES (11033, 152, 179, 189, 'ショッピング', '趣味➡ペットショップ');
INSERT INTO "classcode" VALUES (11034, 152, 179, 190, 'ショッピング', '趣味➡カメラ・写真');
INSERT INTO "classcode" VALUES (11035, 152, 179, 191, 'ショッピング', '趣味➡楽器店');
INSERT INTO "classcode" VALUES (11036, 152, 179, 192, 'ショッピング', '趣味➡アウトドア用品');
INSERT INTO "classcode" VALUES (11037, 152, 179, 193, 'ショッピング', '趣味➡アンティーク');
INSERT INTO "classcode" VALUES (11038, 152, 194, 0, 'ショッピング', 'お土産・物産');
INSERT INTO "classcode" VALUES (11099, 152, 195, 0, 'ショッピング', 'その他(ショッピング)');
INSERT INTO "classcode" VALUES (11101, 196, 197, 0, '地名', '山・山岳');
INSERT INTO "classcode" VALUES (11102, 196, 198, 0, '地名', '湖・沼・池');
INSERT INTO "classcode" VALUES (11103, 196, 199, 0, '地名', '島');
INSERT INTO "classcode" VALUES (11104, 196, 200, 0, '地名', '橋・トンネル・ダム');
INSERT INTO "classcode" VALUES (11105, 196, 201, 0, '地名', '住宅・団地');
INSERT INTO "classcode" VALUES (11106, 196, 202, 0, '地名', 'ビル・マンション');
INSERT INTO "classcode" VALUES (11199, 196, 203, 0, '地名', 'その他(地名)');
INSERT INTO "classcode" VALUES (11201, 204, 205, 0, '企業', '水産・農林業');
INSERT INTO "classcode" VALUES (11202, 204, 206, 0, '企業', '鉱業');
INSERT INTO "classcode" VALUES (11203, 204, 207, 0, '企業', '建設業');
INSERT INTO "classcode" VALUES (11204, 204, 208, 0, '企業', '食料品');
INSERT INTO "classcode" VALUES (11205, 204, 209, 0, '企業', 'パルプ・紙');
INSERT INTO "classcode" VALUES (11206, 204, 210, 0, '企業', '化学');
INSERT INTO "classcode" VALUES (11207, 204, 211, 0, '企業', '石油・石炭製品');
INSERT INTO "classcode" VALUES (11208, 204, 212, 0, '企業', 'ゴム製品');
INSERT INTO "classcode" VALUES (11209, 204, 213, 0, '企業', 'ガラス・土石製品');
INSERT INTO "classcode" VALUES (11210, 204, 214, 0, '企業', '鉄鋼');
INSERT INTO "classcode" VALUES (11211, 204, 215, 0, '企業', '非鉄金属');
INSERT INTO "classcode" VALUES (11212, 204, 216, 0, '企業', '金属製品');
INSERT INTO "classcode" VALUES (11213, 204, 217, 0, '企業', '機械');
INSERT INTO "classcode" VALUES (11214, 204, 218, 0, '企業', '電気機器');
INSERT INTO "classcode" VALUES (11215, 204, 219, 0, '企業', '輸送用機器');
INSERT INTO "classcode" VALUES (11216, 204, 220, 0, '企業', '精密機器');
INSERT INTO "classcode" VALUES (11217, 204, 221, 0, '企業', 'その他製品');
INSERT INTO "classcode" VALUES (11218, 204, 222, 0, '企業', '卸売業・小売業');
INSERT INTO "classcode" VALUES (11219, 204, 223, 0, '企業', '金融業');
INSERT INTO "classcode" VALUES (11220, 204, 224, 0, '企業', '不動産業');
INSERT INTO "classcode" VALUES (11221, 204, 225, 0, '企業', '陸運業');
INSERT INTO "classcode" VALUES (11222, 204, 226, 0, '企業', '海運業');
INSERT INTO "classcode" VALUES (11223, 204, 227, 0, '企業', '空運業');
INSERT INTO "classcode" VALUES (11224, 204, 228, 0, '企業', '倉庫・運輸関連業');
INSERT INTO "classcode" VALUES (11225, 204, 229, 0, '企業', '通信業');
INSERT INTO "classcode" VALUES (11226, 204, 230, 0, '企業', '電気・ガス業');
INSERT INTO "classcode" VALUES (11227, 204, 231, 0, '企業', 'サービス業');
INSERT INTO "classcode" VALUES (11228, 204, 232, 0, '企業', '医薬品');
INSERT INTO "classcode" VALUES (11299, 204, 233, 0, '企業', 'その他(企業)');
INSERT INTO "classcode" VALUES (11301, 234, 235, 0, '観光・イベント', 'お祭り');
INSERT INTO "classcode" VALUES (11302, 234, 236, 0, '観光・イベント', '花見');
INSERT INTO "classcode" VALUES (11303, 234, 237, 0, '観光・イベント', '潮干狩り');
INSERT INTO "classcode" VALUES (11304, 234, 238, 0, '観光・イベント', '果物狩り');
INSERT INTO "classcode" VALUES (11305, 234, 239, 0, '観光・イベント', '花火');
INSERT INTO "classcode" VALUES (11306, 234, 240, 0, '観光・イベント', 'フリーマーケット');
INSERT INTO "classcode" VALUES (11307, 234, 241, 0, '観光・イベント', '名所・旧跡');
INSERT INTO "classcode" VALUES (11308, 234, 242, 0, '観光・イベント', '紅葉');
INSERT INTO "classcode" VALUES (11309, 234, 243, 0, '観光・イベント', 'ホタル');
INSERT INTO "classcode" VALUES (11310, 234, 244, 0, '観光・イベント', '夜景');
INSERT INTO "classcode" VALUES (11311, 234, 245, 0, '観光・イベント', '風光明媚');
INSERT INTO "classcode" VALUES (11312, 234, 246, 0, '観光・イベント', '星空');
INSERT INTO "classcode" VALUES (11313, 234, 247, 0, '観光・イベント', '観光案内・バス');
INSERT INTO "classcode" VALUES (11314, 234, 248, 0, '観光・イベント', '旅行代理店');
INSERT INTO "classcode" VALUES (11315, 234, 249, 0, '観光・イベント', 'ペットと行ける観光名所');
INSERT INTO "classcode" VALUES (11316, 234, 250, 0, '観光・イベント', '名産物・特産物');
INSERT INTO "classcode" VALUES (11317, 234, 251, 0, '観光・イベント', '伝統工芸');
INSERT INTO "classcode" VALUES (11399, 234, 252, 0, '観光・イベント', 'その他(観光・イベント)');
INSERT INTO "classcode" VALUES (11401, 253, 254, 0, '無線スポット', 'Mzone');
INSERT INTO "classcode" VALUES (11402, 253, 255, 0, '無線スポット', 'HOTSPOT');
INSERT INTO "classcode" VALUES (11403, 253, 256, 0, '無線スポット', 'Mフレッツ');
INSERT INTO "classcode" VALUES (11404, 253, 257, 0, '無線スポット', 'フレッツ・スポット');
INSERT INTO "classcode" VALUES (11405, 253, 258, 0, '無線スポット', 'Yahoo!BBモバイル');
INSERT INTO "classcode" VALUES (11406, 253, 259, 0, '無線スポット', '無線LAN倶楽部');
INSERT INTO "classcode" VALUES (11407, 253, 260, 0, '無線スポット', 'BizPortal');
INSERT INTO "classcode" VALUES (11408, 253, 261, 0, '無線スポット', 'eoスポット');
INSERT INTO "classcode" VALUES (11409, 253, 262, 0, '無線スポット', 'モバイルポイント');
INSERT INTO "classcode" VALUES (11410, 253, 263, 0, '無線スポット', 'Air11');
INSERT INTO "classcode" VALUES (11411, 253, 264, 0, '無線スポット', 'みあこネット');
INSERT INTO "classcode" VALUES (11412, 253, 265, 0, '無線スポット', 'フリー無線スポット');
INSERT INTO "classcode" VALUES (11601, 266, 267, 0, '車・ドライブ', 'レンタカー');
INSERT INTO "classcode" VALUES (11602, 266, 268, 0, '車・ドライブ', 'ガソリンスタンド');
INSERT INTO "classcode" VALUES (11603, 266, 269, 0, '車・ドライブ', 'カー用品店');
INSERT INTO "classcode" VALUES (11604, 266, 270, 0, '車・ドライブ', 'カーディーラー');
INSERT INTO "classcode" VALUES (11605, 266, 271, 0, '車・ドライブ', '整備工場');
INSERT INTO "classcode" VALUES (11606, 266, 272, 0, '車・ドライブ', '洗車場');
INSERT INTO "classcode" VALUES (11607, 266, 273, 0, '車・ドライブ', '中古車販売');
INSERT INTO "classcode" VALUES (11608, 266, 274, 0, '車・ドライブ', '道の駅');
INSERT INTO "classcode" VALUES (11609, 266, 275, 0, '車・ドライブ', '駐車場');
INSERT INTO "classcode" VALUES (11699, 266, 276, 0, '車・ドライブ', 'その他(車・ドライブ)');
INSERT INTO "classcode" VALUES (11701, 277, 278, 0, '冠婚葬祭・その他', 'キリスト教・修道院');
INSERT INTO "classcode" VALUES (11702, 277, 279, 0, '冠婚葬祭・その他', '霊園');
INSERT INTO "classcode" VALUES (11703, 277, 280, 0, '冠婚葬祭・その他', '神社・寺');
INSERT INTO "classcode" VALUES (11704, 277, 281, 0, '冠婚葬祭・その他', '結婚式場');
INSERT INTO "classcode" VALUES (11705, 277, 282, 0, '冠婚葬祭・その他', '葬儀場');
INSERT INTO "classcode" VALUES (11799, 277, 283, 0, '冠婚葬祭・その他', 'その他');
INSERT INTO "classcode" VALUES (20012, 297, 298, 0, '不明', '不明');
COMMIT;

INSERT INTO "main"."regdevice" VALUES ('GPKDBJA001213', 'TEST', 716, '鳥取-大学');

SELECT
'{"dbver":"' || strftime('%Y%m%d%H', 'now', 'localtime') || '", "dburl":"http://busdev.ike.tottori-u.ac.jp/android_landmark_db/update_' || strftime('%Y%m%d', 'now', 'localtime') || '.sqlite3", "dbtip":"「最新のデータベース」\n更新完成"}';
.quit
