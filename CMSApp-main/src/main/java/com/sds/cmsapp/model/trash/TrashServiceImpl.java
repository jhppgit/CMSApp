package com.sds.cmsapp.model.trash;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sds.cmsapp.domain.DocumentVersion;
import com.sds.cmsapp.domain.Trash;
import com.sds.cmsapp.domain.VersionLog;
import com.sds.cmsapp.model.document.DocumentDAO;
import com.sds.cmsapp.model.document.DocumentVersionDAO;
import com.sds.cmsapp.model.versionlog.VersionLogDAO;

@Service
public class TrashServiceImpl implements TrashService{
	
	@Autowired
	TrashDAO trashDAO;
	
	@Autowired
	DocumentDAO documentDAO;
	
	@Autowired
	DocumentVersionDAO documentVersionDAO;
	
	@Autowired
	VersionLogDAO versionLogDAO;

	@Override
	public int insert(Trash trash) {
		return trashDAO.insert(trash);
	}

	@Override
	public int restore(Integer trash_idx) {
		return trashDAO.delete(trash_idx);
	}

	@Override
	@Transactional
	public int delete(Integer trash_idx) {
		Trash trash = trashDAO.select(trash_idx);
		documentDAO.delete(trash.getDocument().getDocument_idx());
		int result = trashDAO.delete(trash_idx);
		
		return result;
	}

	@Override
	public int deleteAll() {
		return trashDAO.deleteAll();
	}

	@Override
	public List selectAllWithRange(Map map) {
		List<Trash> trashList = trashDAO.selectAllWithRange(map);
		for (int i = 0; i < trashList.size(); i++) {
			Trash trash = trashList.get(i);
			DocumentVersion documentVersion = documentVersionDAO.selectByDocumentIdx(i);
			VersionLog versionLog = documentVersion.getVersionLog();
			trash.setVersionLog(versionLog);
		}
		return trashList;
	}

	@Override
	public boolean isTrash(Integer document_idx) {
		boolean flag = false;
		if(trashDAO.selectByDocumentIdx(document_idx) != null) {
			flag = true;
		}
		return flag;
	}

	@Override
	public int selectCount() {
		return trashDAO.selectCount();
	}
	

}
