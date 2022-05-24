package web.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.product.dao.ValueDAO;
import web.product.dao.impl.ValueDAOJDBC;
import web.product.entity.ValueVO;
import web.product.service.ValueService;




public class ValueServiceImp implements ValueService {
	
		
		ValueDAO valueDAO=new ValueDAOJDBC();
	
	public List<ValueVO> getAllValue(Integer attrID){
		return valueDAO.getAllValue(attrID);
	}
	
}
