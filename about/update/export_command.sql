select '','','','','','','','','','','','','','','','','','DELETE FROM landmark;','BEGIN TRANSACTION;' union all
select 'INSERT INTO "landmark"(int_landmark_id,vc_landmark_name,vc_landmark_addr,vc_landmark_yomi,tx_landmark_comment,vc_landmark_type,int_landmark_class,int_landmark_lat,int_landmark_lng,f_landmark_fix) VALUES (',
lm.ID,",'",lm.name,"','",lm.address,"','",lm.yomi,"','",class.name,"',",class.ID,",",code.ID,",",lm.north,",",lm.east,",10.0);"
from landmark lm left join landmark_class lc on lm.ID=landmark_ID left join code on CODE = code.ID left join class on class.ID=sector_ID
where level=1 -- LIMIT 0,10
#INTO OUTFILE 'busnet_insert.sql'
#group by b.name
;
select 'COMMIT;'

/*
select a.*,b.name,CONCAT(c.name,ifnull(CONCAT('ÅÑÅÑ',d.name),''))
from code a left join (select * from class where level=1) b
on a.sector_ID=b.id left join (select * from class where level=2) c
on a.subsector_ID=c.id left join (select * from class where level=3) d
on a.group_ID=d.id
-- group by b.name,c.name
order by 2 asc
*/
