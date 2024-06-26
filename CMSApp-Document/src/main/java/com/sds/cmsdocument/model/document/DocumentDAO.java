package com.sds.cmsdocument.model.document;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import com.sds.cmsdocument.domain.Document;
import com.sds.cmsdocument.domain.DocumentVersion;
import com.sds.cmsdocument.domain.VersionLog;

@Mapper
public interface DocumentDAO {
	
	public List<Document> selectAllByRange(Map<String,Integer> map);
	
	public List selectAllForDashboard(Map map);
	
	// 결재 상태별 문서 수
	public int countForDashboard(int statusCode);
	
	public List<Document> selectAll(); // 전체 문서 목록 조회

	public Document select(int documentIdx); // 선택 문서 조회
	
	public Document selectByDocumentIdx(int documentIdx);
	
	public List<Document> selectByFolderIdx(int folderIdx); // mybatis 연결 부탁드려요~ (박준형)

	public int documentInsert(Document document);
	
	//버전 생성
	public int versionInsert(VersionLog versionLog);

	//문서 현재 버전 테이블 
	public int documentVersionInsert(DocumentVersion documentVersion);
	
	//파일 리스트 조회
	public List<DocumentVersion> documentListSelect(final Map<String, Integer> map);
	
	// 문서 수정
	public int update(Document document);
	
	
	// 문서 삭제
	public int delete(int documentIdx);
	
}