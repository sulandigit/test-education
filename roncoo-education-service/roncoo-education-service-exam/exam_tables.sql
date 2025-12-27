-- 考试模块数据库表结构

-- 1. 考试信息表
CREATE TABLE `exam` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status_id` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
  `sort` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  `exam_name` varchar(100) NOT NULL COMMENT '考试名称',
  `exam_desc` text COMMENT '考试描述',
  `course_id` bigint(20) DEFAULT NULL COMMENT '关联课程ID',
  `paper_id` bigint(20) NOT NULL COMMENT '试卷ID',
  `duration` int(11) NOT NULL DEFAULT '60' COMMENT '考试时长(分钟)',
  `pass_score` int(11) NOT NULL DEFAULT '60' COMMENT '及格分数',
  `total_score` int(11) NOT NULL DEFAULT '100' COMMENT '总分',
  `exam_type` tinyint(3) NOT NULL DEFAULT '1' COMMENT '考试类型(1:练习模式,2:考试模式)',
  `is_enable` tinyint(3) NOT NULL DEFAULT '1' COMMENT '是否启用(1:启用,0:禁用)',
  `start_time` datetime DEFAULT NULL COMMENT '考试开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '考试结束时间',
  `allow_times` int(11) NOT NULL DEFAULT '-1' COMMENT '允许考试次数(-1:无限次,其他:具体次数)',
  `is_random_question` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否打乱题目顺序(1:打乱,0:不打乱)',
  `is_random_option` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否打乱选项顺序(1:打乱,0:不打乱)',
  `exam_count` int(11) NOT NULL DEFAULT '0' COMMENT '参考人数',
  PRIMARY KEY (`id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_status_enable` (`status_id`, `is_enable`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试信息表';

-- 2. 试卷信息表
CREATE TABLE `exam_paper` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status_id` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
  `sort` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  `paper_name` varchar(100) NOT NULL COMMENT '试卷名称',
  `paper_desc` text COMMENT '试卷描述',
  `total_score` int(11) NOT NULL DEFAULT '100' COMMENT '总分',
  `question_count` int(11) NOT NULL DEFAULT '0' COMMENT '题目总数',
  `paper_type` tinyint(3) NOT NULL DEFAULT '1' COMMENT '试卷类型(1:固定试卷,2:随机试卷)',
  `is_enable` tinyint(3) NOT NULL DEFAULT '1' COMMENT '是否启用(1:启用,0:禁用)',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  PRIMARY KEY (`id`),
  KEY `idx_status_enable` (`status_id`, `is_enable`),
  KEY `idx_create_user` (`create_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='试卷信息表';

-- 3. 考试题目表
CREATE TABLE `exam_question` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status_id` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
  `sort` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  `paper_id` bigint(20) NOT NULL COMMENT '试卷ID',
  `question_type` tinyint(3) NOT NULL COMMENT '题目类型(1:单选题,2:多选题,3:判断题,4:填空题,5:简答题)',
  `question_content` text NOT NULL COMMENT '题目内容',
  `question_analysis` text COMMENT '题目解析',
  `question_score` int(11) NOT NULL DEFAULT '1' COMMENT '题目分值',
  `question_level` tinyint(3) NOT NULL DEFAULT '1' COMMENT '题目难度(1:简单,2:中等,3:困难)',
  `is_required` tinyint(3) NOT NULL DEFAULT '1' COMMENT '是否必答(1:必答,0:选答)',
  `question_no` int(11) NOT NULL DEFAULT '1' COMMENT '题目序号',
  PRIMARY KEY (`id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_question_type` (`question_type`),
  KEY `idx_question_no` (`question_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试题目表';

-- 4. 题目答案选项表
CREATE TABLE `exam_answer` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status_id` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
  `sort` int(11) NOT NULL DEFAULT '1' COMMENT '排序',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `answer_tag` varchar(10) NOT NULL COMMENT '选项标识(A,B,C,D等)',
  `answer_content` text NOT NULL COMMENT '选项内容',
  `is_correct` tinyint(3) NOT NULL DEFAULT '0' COMMENT '是否正确答案(1:正确,0:错误)',
  `answer_no` int(11) NOT NULL DEFAULT '1' COMMENT '选项序号',
  PRIMARY KEY (`id`),
  KEY `idx_question_id` (`question_id`),
  KEY `idx_answer_no` (`answer_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目答案选项表';

-- 5. 考试记录表
CREATE TABLE `exam_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status_id` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `exam_id` bigint(20) NOT NULL COMMENT '考试ID',
  `paper_id` bigint(20) NOT NULL COMMENT '试卷ID',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `use_time` int(11) DEFAULT '0' COMMENT '用时(秒)',
  `score` int(11) DEFAULT '0' COMMENT '得分',
  `total_score` int(11) NOT NULL DEFAULT '100' COMMENT '总分',
  `correct_count` int(11) DEFAULT '0' COMMENT '正确题数',
  `error_count` int(11) DEFAULT '0' COMMENT '错误题数',
  `no_answer_count` int(11) DEFAULT '0' COMMENT '未答题数',
  `exam_status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '考试状态(1:未开始,2:进行中,3:已完成,4:已超时)',
  `is_passed` tinyint(3) DEFAULT '0' COMMENT '是否通过(1:通过,0:未通过)',
  `exam_times` int(11) NOT NULL DEFAULT '1' COMMENT '考试次数',
  PRIMARY KEY (`id`),
  KEY `idx_user_exam` (`user_id`, `exam_id`),
  KEY `idx_paper_id` (`paper_id`),
  KEY `idx_exam_status` (`exam_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考试记录表';

-- 6. 答题记录表
CREATE TABLE `exam_question_record` (
  `id` bigint(20) NOT NULL COMMENT '主键ID',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `status_id` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态(1:正常，0:禁用)',
  `exam_record_id` bigint(20) NOT NULL COMMENT '考试记录ID',
  `question_id` bigint(20) NOT NULL COMMENT '题目ID',
  `user_answer` varchar(500) DEFAULT NULL COMMENT '用户答案',
  `correct_answer` varchar(500) DEFAULT NULL COMMENT '正确答案',
  `is_correct` tinyint(3) DEFAULT '0' COMMENT '是否正确(1:正确,0:错误)',
  `score` int(11) DEFAULT '0' COMMENT '得分',
  `question_score` int(11) NOT NULL DEFAULT '1' COMMENT '题目分值',
  `use_time` int(11) DEFAULT '0' COMMENT '答题用时(秒)',
  PRIMARY KEY (`id`),
  KEY `idx_exam_record_id` (`exam_record_id`),
  KEY `idx_question_id` (`question_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='答题记录表';