package web.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.product.dao.AttributesDAO;
import web.product.dao.impl.AttributesDAOImp;
import web.product.entity.AttrWithValue;
import web.product.service.AttributesService;




@Service
public class AttributesServiceImp implements AttributesService {

	@Autowired
	AttributesDAO attrDAO;
	
	public List<AttrWithValue> getAll(Integer ctgID){
		return attrDAO.getAll(ctgID);
	}
	
}
