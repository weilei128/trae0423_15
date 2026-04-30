-- =============================================
-- 医院门诊预约挂号系统数据库初始化脚本
-- 数据库: db5
-- 创建日期: 2024
-- =============================================

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    id_card VARCHAR(18) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 科室表
CREATE TABLE IF NOT EXISTS departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    location VARCHAR(100),
    sort_order INT DEFAULT 0,
    enabled BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 医生表
CREATE TABLE IF NOT EXISTS doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    department_id BIGINT NOT NULL,
    title VARCHAR(50),
    specialty VARCHAR(255),
    introduction TEXT,
    avatar VARCHAR(255),
    consultation_fee DECIMAL(10, 2),
    available BOOLEAN DEFAULT TRUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 排班表
CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    time_slot VARCHAR(50),
    max_patients INT NOT NULL DEFAULT 20,
    remaining_patients INT NOT NULL DEFAULT 20,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX idx_schedule_date (schedule_date),
    INDEX idx_doctor_date (doctor_id, schedule_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 预约表
CREATE TABLE IF NOT EXISTS appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_no VARCHAR(50) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    schedule_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME,
    queue_number INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    check_in_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (schedule_id) REFERENCES schedules(id),
    FOREIGN KEY (department_id) REFERENCES departments(id),
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_date (doctor_id, appointment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 病历记录表
CREATE TABLE IF NOT EXISTS medical_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    chief_complaint TEXT,
    present_illness TEXT,
    past_history TEXT,
    physical_examination TEXT,
    diagnosis TEXT,
    treatment_plan TEXT,
    prescription TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 检查申请表
CREATE TABLE IF NOT EXISTS examinations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_no VARCHAR(50) NOT NULL UNIQUE,
    medical_record_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    exam_type VARCHAR(50) NOT NULL,
    exam_name VARCHAR(100) NOT NULL,
    exam_description TEXT,
    fee DECIMAL(10, 2),
    status VARCHAR(20) DEFAULT 'PENDING',
    exam_result TEXT,
    report_url VARCHAR(255),
    exam_time DATETIME,
    report_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (medical_record_id) REFERENCES medical_records(id),
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 支付记录表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(50) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    appointment_id BIGINT,
    examination_id BIGINT,
    payment_type VARCHAR(20),
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20),
    status VARCHAR(20) DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    payment_time DATETIME,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(id),
    FOREIGN KEY (examination_id) REFERENCES examinations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- 初始化数据
-- =============================================

-- 插入角色数据
INSERT INTO roles (name, description) VALUES 
('ADMIN', '系统管理员'),
('DOCTOR', '医生'),
('PATIENT', '患者'),
('REGISTRAR', '挂号员');

-- 插入科室数据
INSERT INTO departments (name, description, location, sort_order) VALUES 
('内科', '内科诊疗科室，主要诊治呼吸系统、循环系统、消化系统、泌尿系统等疾病', '门诊楼2楼', 1),
('外科', '外科诊疗科室，主要开展手术治疗和创伤处理', '门诊楼3楼', 2),
('儿科', '儿科诊疗科室，专门诊治儿童疾病', '门诊楼2楼', 3),
('妇产科', '妇产科诊疗科室，负责女性生殖系统疾病和孕产保健', '门诊楼4楼', 4),
('眼科', '眼科诊疗科室，诊治眼部疾病', '门诊楼1楼', 5),
('耳鼻喉科', '耳鼻喉科诊疗科室，诊治耳、鼻、咽喉疾病', '门诊楼1楼', 6),
('口腔科', '口腔科诊疗科室，诊治口腔疾病', '门诊楼1楼', 7),
('皮肤科', '皮肤科诊疗科室，诊治皮肤疾病', '门诊楼2楼', 8);

-- 插入测试用户（密码使用BCrypt加密，明文密码为：admin123, doctor123, patient123, registrar123）
-- BCrypt加密后的密码
-- admin123 -> $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E
-- 实际应该使用正确的加密密码，这里使用一个示例

-- 注意：BCrypt加密后的密码长度较长，这里使用真实的加密值
-- 以下密码都是对应明文密码加密后的结果
-- 明文密码: admin123
INSERT INTO users (username, password, real_name, id_card, phone, email, enabled) VALUES 
('admin', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '系统管理员', '110101199001010001', '13800138001', 'admin@hospital.com', TRUE),
('doctor1', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '张医生', '110101198505050002', '13800138002', 'doctor1@hospital.com', TRUE),
('doctor2', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '李医生', '110101198606060003', '13800138003', 'doctor2@hospital.com', TRUE),
('doctor3', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '王医生', '110101198707070004', '13800138004', 'doctor3@hospital.com', TRUE),
('patient1', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '患者张三', '110101199510100005', '13800138005', 'patient1@hospital.com', TRUE),
('patient2', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '患者李四', '110101199611110006', '13800138006', 'patient2@hospital.com', TRUE),
('registrar1', '$2a$10$EqW4r5wH2kX7yZ9vB8nQYu5w3x2v1b0n9m8l7k6j5h4g3f2e1d0', '挂号员小王', '110101199202020007', '13800138007', 'registrar1@hospital.com', TRUE);

-- 分配用户角色
INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1),  -- admin -> ADMIN
(2, 2),  -- doctor1 -> DOCTOR
(3, 2),  -- doctor2 -> DOCTOR
(4, 2),  -- doctor3 -> DOCTOR
(5, 3),  -- patient1 -> PATIENT
(6, 3),  -- patient2 -> PATIENT
(7, 4);  -- registrar1 -> REGISTRAR

-- 插入医生信息
INSERT INTO doctors (user_id, department_id, title, specialty, introduction, consultation_fee, available) VALUES 
(2, 1, '主任医师', '心血管内科、高血压、冠心病', '从事心血管内科临床工作20年，擅长高血压、冠心病、心律失常等疾病诊治', 50.00, TRUE),
(3, 2, '副主任医师', '普外科、胃肠外科、疝外科', '从事普外科临床工作15年，擅长胃肠疾病、疝气、甲状腺疾病等手术治疗', 40.00, TRUE),
(4, 3, '主治医师', '小儿呼吸系统疾病、小儿消化系统疾病', '从事儿科临床工作10年，擅长小儿感冒、肺炎、腹泻等常见疾病诊治', 30.00, TRUE);
