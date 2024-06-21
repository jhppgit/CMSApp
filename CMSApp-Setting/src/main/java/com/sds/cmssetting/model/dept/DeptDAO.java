package com.sds.cmssetting.model.dept;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.sds.cmssetting.domain.Dept;

@Mapper
public interface DeptDAO {
	
	public int insert(final Dept dept);
	public List<Dept> selectAllDeptName();
	public List<Dept> selectAll();
	public Dept selectByDeptIdx(final int deptIdx);
	public void delete(final Dept dept);
	
}
