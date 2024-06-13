package com.sds.cmsapp.model.document;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sds.cmsapp.domain.Document;
import com.sds.cmsapp.domain.DocumentVersion;
import com.sds.cmsapp.domain.VersionLog;
import com.sds.cmsapp.exception.DocumentException;
import com.sds.cmsapp.exception.VersionLogException;
import com.sds.cmsapp.model.versionlog.VersionLogDAO;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {
	
	@Autowired
	private DocumentDAO documentDAO;
	
	@Autowired
	private VersionLogDAO versionLogDAO;
	
	@Autowired
	private DocumentVersionDAO documentVersionDAO;

	@Autowired
	private DocumentDetailDAO documentDetailDAO;
	
	// 모든 문서 조회
	public List selectAll(Map map) {
		return null;
	};
	
	public List selectAllForDashboard(Map map) {
		return documentDAO.selectAllForDashboard(map);
	};
	
	// 결재 상태별 문서 수
	public int countForDashboard(int statusCode) {
		return documentDAO.countForDashboard(statusCode);
	};

	// 선택 문서 조회
	
	// returnType="Document"
	public Document select(int documentIdx) {
		return documentDAO.select(documentIdx);
	}; 
	
	// returnMap="DocumentMap"
	public Document selectByDocumentIdx(int documentIdx) {
		return documentDAO.selectByDocumentIdx(documentIdx);
	}; 
	
	//새로운 문서 작성
	@Transactional
    public void documentInsert(VersionLog versionLog) throws DocumentException, VersionLogException {
	    // 문서 내용이 비어있거나 공백인지 확인
		if (!StringUtils.hasText(versionLog.getTitle()) 
			    || !StringUtils.hasText(versionLog.getContent())
			    || !StringUtils.hasText(versionLog.getComments())) {
			    throw new VersionLogException("문서 내용이 비어있습니다.");
		}

        // 문서 삽입
		int result = documentDAO.documentInsert(versionLog.getDocument());
		
		log.debug("document reuslt is "+result);
		
		if(result < 1) {
			throw new DocumentException("문서 insert 실패 ");
		}
		
        result = documentDAO.versionInsert(versionLog);
        
       // log.debug("versionLog reuslt is "+result);
       // log.debug("document _idx is "+versionLog.getDocument().getDocument_idx());
        
		if(result < 1) {
			throw new VersionLogException("문서 버전 로그 등록실패 ");
		}
		
		DocumentVersion documentVersion = new DocumentVersion();
		documentVersion.setDocument(versionLog.getDocument());
		documentVersion.setVersionLog(versionLog);
		
		//log.debug("document_version is " + documentVersion);
		
		result = documentDAO.documentVersionInsert(documentVersion);
		
		if(result < 1) {
			throw new DocumentException("문서 현재 버전 insert 실패 ");
		}
    }

	@Override
	public List documentListSelect(Map map) {
		return documentDAO.documentListSelect(map);
	}
	
	//document/detail 문서 상세보기 
	public DocumentVersion documentDetailSelect(int documentIdx) {
	    
        return documentDetailDAO.documentDetailSelect(documentIdx);
	}
	
	@Override // 임시로 만들어뒀습니다 -박준형
	public int delete(int documentIdx) {
		return documentDAO.delete(documentIdx);
	}
	
	@Override // 박준형 추가
	public List<Document> selectByFolderIdx(int folder_idx) {
		return documentDAO.selectByFolderIdx(folder_idx);
	}

	@Transactional
	public void versionUpdate(VersionLog versionLog) throws VersionLogException{
		
		DocumentVersion documentVersion = documentDetailDAO.documentDetailSelect(versionLog.getDocument().getDocumentIdx());
		documentVersion.getVersionLog().setTitle(versionLog.getTitle());
		documentVersion.getVersionLog().setContent(versionLog.getContent());
		documentVersion.getVersionLog().setComments(versionLog.getComments());
		
		int maxVersion = documentDetailDAO.findMaxVersionByDocumentIdx(versionLog.getDocument().getDocumentIdx());
		
		//버전 로그 증가
		//int versionIdx = Integer.parseInt(documentVersion.getVersionLog().getVersion());
		String newVersion = Integer.toString(maxVersion + 1);
		documentVersion.getVersionLog().setVersion(newVersion);
		
		log.debug("versionLog = "+documentVersion.getVersionLog());
		
		int result = documentDetailDAO.versionLogInsert(documentVersion.getVersionLog());
		
		if(result < 1) {
			throw new VersionLogException("문서 버전 로그 등록실패 ");
		}
		

		
		result = documentDetailDAO.documentVersionUpdate(documentVersion.getVersionLog());
		
		if(result < 1) {
			throw new DocumentException("문서 현재 버전 업데이트 실패 ");
		}
	}
	
	public void documentVersionStatusUpdate(DocumentVersion documentVersion) {
		documentDetailDAO.documentVersionStatusUpdate(documentVersion);
		
	}
	
	@Override
	public Document fillVersionLog(final Document document) {
		DocumentVersion documentVersion = documentVersionDAO.selectByDocumentIdx(document.getDocumentIdx());
		VersionLog versionLog = documentVersion.getVersionLog();
		document.setVersionLog(versionLog);
		return document;
	}
}
