-- 취향 목록 --
INSERT INTO taste (name, parent_id) VALUES
                                        ('전시회', NULL),
                                        ('콘서트', NULL),
                                        ('뮤지컬/연극', NULL),
                                        ('클래식/무용', NULL);

-- 전시회 하위 항목
INSERT INTO taste (name, parent_id)
SELECT '팝업 전시', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '사진 전시', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '현대미술', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '설치미술', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '디지털 아트', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '건축 전시', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '장식미술', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '문화 전시', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '과학 전시', id FROM taste WHERE name = '전시회'
UNION ALL SELECT '역사 전시', id FROM taste WHERE name = '전시회';

-- 콘서트 하위 항목
INSERT INTO taste (name, parent_id)
SELECT '팝', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '랩/힙합', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '락/메탈', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '케이팝', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '팬미팅', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '페스티벌', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '트로트', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '발라드', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '인디', id FROM taste WHERE name = '콘서트'
UNION ALL SELECT '토크/강연', id FROM taste WHERE name = '콘서트';

-- 뮤지컬/연극 하위 항목
INSERT INTO taste (name, parent_id)
SELECT '드라마', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '코미디', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '로맨스', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '판타지', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '스릴러', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '실험극', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '역사극', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '오리지널/내한', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '창작', id FROM taste WHERE name = '뮤지컬/연극'
UNION ALL SELECT '라이선스', id FROM taste WHERE name = '뮤지컬/연극';

-- 클래식/무용 하위 항목
INSERT INTO taste (name, parent_id)
SELECT '오케스트라', id FROM taste WHERE name = '클래식/무용'
UNION ALL SELECT '오페라', id FROM taste WHERE name = '클래식/무용'
UNION ALL SELECT '발레', id FROM taste WHERE name = '클래식/무용'
UNION ALL SELECT '현대무용', id FROM taste WHERE name = '클래식/무용'
UNION ALL SELECT '전통무용', id FROM taste WHERE name = '클래식/무용'
UNION ALL SELECT '국악', id FROM taste WHERE name = '클래식/무용';

-- 태그 목록 --
insert into tag (name) values ('전시회') , ('콘서트'), ('뮤지컬') , ('영화제');
insert into tag (name) values ('동행구함') , ('티켓 있음'), ('현장 구매') , ('스탠딩'), ('무대 인사');
insert into tag (name) values ('남성 환영') , ('여성 환영'), ('중앙 블럭') , ('통로석');
insert into tag (name) values ('10대 환영') , ('20대 환영'), ('30대 블럭') , ('40대 환영'), ('연령 무관');

