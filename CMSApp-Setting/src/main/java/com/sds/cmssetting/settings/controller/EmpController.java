package com.sds.cmssetting.settings.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sds.cmssetting.common.FileManager;
import com.sds.cmssetting.domain.Dept;
import com.sds.cmssetting.domain.Emp;
import com.sds.cmssetting.domain.EmpDetail;
import com.sds.cmssetting.domain.Role;
import com.sds.cmssetting.exception.UploadException;
import com.sds.cmssetting.model.emp.EmpDetailService;
import com.sds.cmssetting.model.emp.EmpService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class EmpController {

//	@Autowired
//	private ResourceLoader resourceLoader;
//	
//	@Value("${upload.location}")
//	private String uploadLocation;
	
	@Autowired
	private FileManager fileManager;
	
	@Autowired
	private EmpService empService;
	
	@Autowired
	private EmpDetailService empDetailService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/emp/regist")
	public ResponseEntity<String> regist(Emp emp, EmpDetail empDetail, @RequestParam("deptIdx") int deptIdx, @RequestParam("roleCode") int roleCode) {
		try {
			 MultipartFile file = empDetail.getFile();
			 // log.debug("title: "+empDetail.getFile());
			 // log.debug("original Filename: "+file.getOriginalFilename());
			 // File directory = resourceLoader.getResource("classpath:/static/profileImg/").getFile();
			 // log.debug("파일을 저장할 경로는 "+directory.getAbsolutePath());			 
	            if (file != null && !file.isEmpty()) {            // 파일이 존재한다면 
	                // Path path = Paths.get(directory.getAbsolutePath());
	                // Path savePath = path.resolve(file.getOriginalFilename());
	                // Files.copy(file.getInputStream(), savePath);
	                // log.debug(savePath.toString());
	                empDetail.setEmpProfileUrl(fileManager.save(empDetail)); // 서버에 저장
	            } 
	        
            Dept dept = new Dept();
            dept.setDeptIdx(deptIdx);
            emp.setDept(dept);
            
            Role role = new Role();
            role.setRoleCode(roleCode);
            emp.setRole(role);    
	            
            // EmpDetail 객체를 DB에 저장하는 로직	        
	        empService.insert(emp);
	        empDetail.setEmp(emp);
	        empDetail.setEmpPw(passwordEncoder.encode(empDetail.getEmpPw())); 
	        empDetailService.insert(empDetail);

            log.warn("사원 등록 성공");
            return ResponseEntity.ok("Employee added successfully");
        } catch (UploadException e) {
        	e.printStackTrace();
        	log.warn("사원 등록 실패");
            // throw new UploadException("사원 등록 실패", e);
        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Employee registration failed");
        }
	}
	
	@PostMapping("/emp/update")
    public ResponseEntity<?> update(@RequestParam("empIdx") List<Integer> empIdxList,
                         @RequestParam("roleCode") List<Integer> roleCodeList,
                         @RequestParam("deptIdx") List<Integer> deptIdxList) {
        for (int i = 0; i < empIdxList.size(); i++) {
            Emp emp = empService.selectByEmpIdx(empIdxList.get(i));
            if (emp != null) {
                Dept dept = new Dept();
                dept.setDeptIdx(deptIdxList.get(i));
                emp.setDept(dept);

                Role role = new Role();
                role.setRoleCode(roleCodeList.get(i));
                emp.setRole(role);

                empService.update(emp);
            } else {
            	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }
        return ResponseEntity.ok(null);
    }
	
	@PostMapping("/setting/mypage/update")
	public ResponseEntity<String> update(Emp emp, EmpDetail empDetail, @RequestParam("deptIdx") int deptIdx, @RequestParam("roleCode") int roleCode) {
		try {
			 MultipartFile file = empDetail.getFile();
	            if (file != null && !file.isEmpty()) {            			 // 파일이 존재한다면 
	                empDetail.setEmpProfileUrl(fileManager.save(empDetail)); // 서버에 저장
	            } 
	        
           Dept dept = new Dept();
           dept.setDeptIdx(deptIdx);
           emp.setDept(dept);
           
           Role role = new Role();
           role.setRoleCode(roleCode);
           emp.setRole(role);    
	       
           System.out.println("emp.getEmpName(): " + emp.getEmpName());
           System.out.println("empDetail.getEmpId(): "+ empDetail.getEmpId());
           System.out.println("empDetail.getEmpPw(): " + empDetail.getEmpPw());
           
           // 이름, id, pw등이 변경되지 않았다면 원래 값으로 설정
           if(emp.getEmpName() == null) {
        	   emp.setEmpName(empService.selectByEmpIdx(emp.getEmpIdx()).getEmpName());
		   } if(empDetail.getEmpId() == null) {
        	   empDetail.setEmpId(empDetailService.selectByEmpIdx(emp.getEmpIdx()).getEmpId());
		   } if(empDetail.getEmpPw() == null) {
			   empDetail.setEmpPw(empDetailService.selectByEmpIdx(emp.getEmpIdx()).getEmpPw());
		   }
		   
           // EmpDetail 객체를 DB에 저장하는 로직	        
	        empService.update(emp);
	        empDetail.setEmp(emp);
	        empDetailService.update(empDetail);

	        return ResponseEntity.ok("Employee update successfully");
       } catch (UploadException e) {
       	   e.printStackTrace();
           // throw new UploadException("사원 수정 실패", e);
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Employee update failed");
       }
		
	}
}
