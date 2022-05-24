package web.product.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

import web.product.dao.CategoryDAO;
import web.product.dao.impl.CategoryDAOImp;
import web.product.entity.CategoryVO;
import web.product.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImp implements CategoryService {

	@Autowired
	CategoryDAO categoryDAO ;
	
	@Transactional
	public int queryCtgID(String ctgName) {
		return categoryDAO.queryCtgID(ctgName);
	}
	
	
	@Transactional
	public List<CategoryVO> getAllCategory(){
	
		return categoryDAO.getAllCategory();
	}

}
