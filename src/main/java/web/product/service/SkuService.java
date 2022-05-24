package web.product.service;

import java.util.List;

import com.google.gson.JsonElement;

import web.product.dao.SkuDAO;
import web.product.dao.impl.SkuDAOJDBC;
import web.product.entity.ImgTFVO;
import web.product.entity.SkuVO;
import web.product.service.impl.CategoryServiceImp;
import web.product.service.impl.PImgServiceImp;
import web.product.service.impl.SkuServiceImp;
import web.product.service.impl.SpuServiceImp;


public interface SkuService {

	
	
	
	void insertProd(String ctgName, String spuName,String descript,List<Integer> prices,List<Integer> stocks,List<JsonElement> attrList,ImgTFVO imgs);

	int insertSKU(int spuID, List<Integer> prices, List<Integer> stocks, List<JsonElement> attrList);

	int updateProd(SkuVO skuVO);
	
	int takeOrder(List<SkuVO> checkOut);
	

	
	
}
