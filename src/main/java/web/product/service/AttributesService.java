package web.product.service;

import java.util.List;

import web.product.dao.AttributesDAO;
import web.product.dao.impl.AttributesDAOImp;
import web.product.entity.AttrWithValue;



public interface AttributesService {
	


	
	List<AttrWithValue> getAll(Integer ctgID);

}
