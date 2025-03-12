SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE careers;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO careers (id, job, detail_job_en, detail_job_kr)
VALUES (1, 'DESIGNER', 'Product Designer', '[
  "프로덕트 디자이너"
]'),
       (2, 'DESIGNER', 'Graphic Designer', '[
         "그래픽 디자이너"
       ]'),
       (3, 'DESIGNER', 'Interaction Designer', '[
         "인터랙션 디자이너",
         "인터렉션 디자이너"
       ]'),
       (4, 'DESIGNER', 'UX/UI Designer', '[
         "유엑스유아이 디자이너",
         "유엑유아 디자이너",
         "유아이유엑스 디자이너",
         "유아유엑 디자이너"
       ]'),
       (5, 'DESIGNER', 'UX Designer', '[
         "유엑스 디자이너",
         "유엑 디자이너"
       ]'),
       (6, 'DESIGNER', 'UI Designer', '[
         "유아이 디자이너"
       ]'),
       (7, 'DEVELOPER', 'Server Developer', '[
         "서버 개발자"
       ]'),
       (8, 'DEVELOPER', 'AI Developer', '[
         "에이아이 개발자",
         "에아이 개발자"
       ]'),
       (9, 'DEVELOPER', 'Frontend Developer', '[
         "프론트 개발자",
         "프론티드 개발자"
       ]'),
       (10, 'DEVELOPER', 'iOS Developer', '[
         "아오스 개발자",
         "ios 개발자",
         "아이오에스 개발자"
       ]'),
       (11, 'DEVELOPER', 'Android Developer', '[
         "안드로이드 개발자"
       ]'),
       (12, 'DEVELOPER', 'Full-Stack Developer', '[
         "풀스택 개발자",
         "풀스텍 개발자"
       ]'),
       (13, 'DEVELOPER', 'DevOps Developer', '[
         "데브옵스 개발자"
       ]'),
       (14, 'DEVELOPER', 'Desktop Developer', '[
         "데스크톱 개발자",
         "데스크탑 개발자"
       ]'),
       (15, 'DEVELOPER', 'Game Developer', '[
         "게임 개발자"
       ]'),
       (16, 'DEVELOPER', 'Data Engineering Developer', '[
         "데이타 개발자",
         "다타 개발자",
         "데이터 개발자",
         "엔지니어링 개발자"
       ]'),
       (17, 'DEVELOPER', 'QA Developer', '[
         "큐에이 개발자",
         "QA 개발자"
       ]');
