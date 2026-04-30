package com.hospital.outpatient.config;

import com.hospital.outpatient.entity.Department;
import com.hospital.outpatient.entity.Doctor;
import com.hospital.outpatient.entity.Role;
import com.hospital.outpatient.entity.User;
import com.hospital.outpatient.repository.DepartmentRepository;
import com.hospital.outpatient.repository.DoctorRepository;
import com.hospital.outpatient.repository.RoleRepository;
import com.hospital.outpatient.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        initRoles();
        initDepartments();
        initUsers();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(Role.ADMIN);
            adminRole.setDescription("系统管理员");
            roleRepository.save(adminRole);
            
            Role doctorRole = new Role();
            doctorRole.setName(Role.DOCTOR);
            doctorRole.setDescription("医生");
            roleRepository.save(doctorRole);
            
            Role patientRole = new Role();
            patientRole.setName(Role.PATIENT);
            patientRole.setDescription("患者");
            roleRepository.save(patientRole);
            
            Role registrarRole = new Role();
            registrarRole.setName(Role.REGISTRAR);
            registrarRole.setDescription("挂号员");
            roleRepository.save(registrarRole);
        }
    }

    private void initDepartments() {
        if (departmentRepository.count() == 0) {
            Department internal = new Department();
            internal.setName("内科");
            internal.setDescription("内科诊疗科室，主要诊治呼吸系统、循环系统、消化系统、泌尿系统等疾病");
            internal.setLocation("门诊楼2楼");
            internal.setSortOrder(1);
            departmentRepository.save(internal);
            
            Department surgery = new Department();
            surgery.setName("外科");
            surgery.setDescription("外科诊疗科室，主要开展手术治疗和创伤处理");
            surgery.setLocation("门诊楼3楼");
            surgery.setSortOrder(2);
            departmentRepository.save(surgery);
            
            Department pediatrics = new Department();
            pediatrics.setName("儿科");
            pediatrics.setDescription("儿科诊疗科室，专门诊治儿童疾病");
            pediatrics.setLocation("门诊楼2楼");
            pediatrics.setSortOrder(3);
            departmentRepository.save(pediatrics);
            
            Department gynecology = new Department();
            gynecology.setName("妇产科");
            gynecology.setDescription("妇产科诊疗科室，负责女性生殖系统疾病和孕产保健");
            gynecology.setLocation("门诊楼4楼");
            gynecology.setSortOrder(4);
            departmentRepository.save(gynecology);
            
            Department ophthalmology = new Department();
            ophthalmology.setName("眼科");
            ophthalmology.setDescription("眼科诊疗科室，诊治眼部疾病");
            ophthalmology.setLocation("门诊楼1楼");
            ophthalmology.setSortOrder(5);
            departmentRepository.save(ophthalmology);
            
            Department ent = new Department();
            ent.setName("耳鼻喉科");
            ent.setDescription("耳鼻喉科诊疗科室，诊治耳、鼻、咽喉疾病");
            ent.setLocation("门诊楼1楼");
            ent.setSortOrder(6);
            departmentRepository.save(ent);
            
            Department stomatology = new Department();
            stomatology.setName("口腔科");
            stomatology.setDescription("口腔科诊疗科室，诊治口腔疾病");
            stomatology.setLocation("门诊楼1楼");
            stomatology.setSortOrder(7);
            departmentRepository.save(stomatology);
            
            Department dermatology = new Department();
            dermatology.setName("皮肤科");
            dermatology.setDescription("皮肤科诊疗科室，诊治皮肤疾病");
            dermatology.setLocation("门诊楼2楼");
            dermatology.setSortOrder(8);
            departmentRepository.save(dermatology);
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName(Role.ADMIN).orElseThrow(() -> new RuntimeException("角色不存在"));
            Role doctorRole = roleRepository.findByName(Role.DOCTOR).orElseThrow(() -> new RuntimeException("角色不存在"));
            Role patientRole = roleRepository.findByName(Role.PATIENT).orElseThrow(() -> new RuntimeException("角色不存在"));
            Role registrarRole = roleRepository.findByName(Role.REGISTRAR).orElseThrow(() -> new RuntimeException("角色不存在"));
            
            Department internalDept = departmentRepository.findAll().stream()
                    .filter(d -> d.getName().equals("内科")).findFirst().orElseThrow(() -> new RuntimeException("科室不存在"));
            Department surgeryDept = departmentRepository.findAll().stream()
                    .filter(d -> d.getName().equals("外科")).findFirst().orElseThrow(() -> new RuntimeException("科室不存在"));
            Department pediatricsDept = departmentRepository.findAll().stream()
                    .filter(d -> d.getName().equals("儿科")).findFirst().orElseThrow(() -> new RuntimeException("科室不存在"));
            
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRealName("系统管理员");
            admin.setIdCard("110101199001010001");
            admin.setPhone("13800138001");
            admin.setEmail("admin@hospital.com");
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            
            User doctor1 = new User();
            doctor1.setUsername("doctor");
            doctor1.setPassword(passwordEncoder.encode("doctor123"));
            doctor1.setRealName("张医生");
            doctor1.setIdCard("110101198505050002");
            doctor1.setPhone("13800138002");
            doctor1.setEmail("doctor1@hospital.com");
            Set<Role> doctorRoles = new HashSet<>();
            doctorRoles.add(doctorRole);
            doctor1.setRoles(doctorRoles);
            userRepository.save(doctor1);
            
            Doctor doc1 = new Doctor();
            doc1.setUser(doctor1);
            doc1.setDepartment(internalDept);
            doc1.setTitle("主任医师");
            doc1.setSpecialty("心血管内科、高血压、冠心病");
            doc1.setIntroduction("从事心血管内科临床工作20年，擅长高血压、冠心病、心律失常等疾病诊治");
            doc1.setConsultationFee(50);
            doctorRepository.save(doc1);
            
            User doctor2 = new User();
            doctor2.setUsername("doctor2");
            doctor2.setPassword(passwordEncoder.encode("doctor123"));
            doctor2.setRealName("李医生");
            doctor2.setIdCard("110101198606060003");
            doctor2.setPhone("13800138003");
            doctor2.setEmail("doctor2@hospital.com");
            Set<Role> doctorRoles2 = new HashSet<>();
            doctorRoles2.add(doctorRole);
            doctor2.setRoles(doctorRoles2);
            userRepository.save(doctor2);
            
            Doctor doc2 = new Doctor();
            doc2.setUser(doctor2);
            doc2.setDepartment(surgeryDept);
            doc2.setTitle("副主任医师");
            doc2.setSpecialty("普外科、胃肠外科、疝外科");
            doc2.setIntroduction("从事普外科临床工作15年，擅长胃肠疾病、疝气、甲状腺疾病等手术治疗");
            doc2.setConsultationFee(40);
            doctorRepository.save(doc2);
            
            User doctor3 = new User();
            doctor3.setUsername("doctor3");
            doctor3.setPassword(passwordEncoder.encode("doctor123"));
            doctor3.setRealName("王医生");
            doctor3.setIdCard("110101198707070004");
            doctor3.setPhone("13800138004");
            doctor3.setEmail("doctor3@hospital.com");
            Set<Role> doctorRoles3 = new HashSet<>();
            doctorRoles3.add(doctorRole);
            doctor3.setRoles(doctorRoles3);
            userRepository.save(doctor3);
            
            Doctor doc3 = new Doctor();
            doc3.setUser(doctor3);
            doc3.setDepartment(pediatricsDept);
            doc3.setTitle("主治医师");
            doc3.setSpecialty("小儿呼吸系统疾病、小儿消化系统疾病");
            doc3.setIntroduction("从事儿科临床工作10年，擅长小儿感冒、肺炎、腹泻等常见疾病诊治");
            doc3.setConsultationFee(30);
            doctorRepository.save(doc3);
            
            User patient1 = new User();
            patient1.setUsername("patient");
            patient1.setPassword(passwordEncoder.encode("patient123"));
            patient1.setRealName("患者张三");
            patient1.setIdCard("110101199510100005");
            patient1.setPhone("13800138005");
            patient1.setEmail("patient1@hospital.com");
            Set<Role> patientRoles = new HashSet<>();
            patientRoles.add(patientRole);
            patient1.setRoles(patientRoles);
            userRepository.save(patient1);
            
            User patient2 = new User();
            patient2.setUsername("patient2");
            patient2.setPassword(passwordEncoder.encode("patient123"));
            patient2.setRealName("患者李四");
            patient2.setIdCard("110101199611110006");
            patient2.setPhone("13800138006");
            patient2.setEmail("patient2@hospital.com");
            Set<Role> patientRoles2 = new HashSet<>();
            patientRoles2.add(patientRole);
            patient2.setRoles(patientRoles2);
            userRepository.save(patient2);
            
            User registrar1 = new User();
            registrar1.setUsername("registrar");
            registrar1.setPassword(passwordEncoder.encode("registrar123"));
            registrar1.setRealName("挂号员小王");
            registrar1.setIdCard("110101199202020007");
            registrar1.setPhone("13800138007");
            registrar1.setEmail("registrar1@hospital.com");
            Set<Role> registrarRoles = new HashSet<>();
            registrarRoles.add(registrarRole);
            registrar1.setRoles(registrarRoles);
            userRepository.save(registrar1);
        }
    }
}
