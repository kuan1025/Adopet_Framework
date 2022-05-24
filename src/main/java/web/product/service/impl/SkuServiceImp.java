package web.product.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonElement;

import web.product.dao.SkuDAO;
import web.product.dao.impl.SkuDAOJDBC;
import web.product.entity.ImgTFVO;
import web.product.entity.SkuVO;
import web.product.service.CategoryService;
import web.product.service.PImgService;
import web.product.service.SkuService;
import web.product.service.SpuService;;

@Service
public class SkuServiceImp implements SkuService {

	@Autowired
	SkuDAO skuDAO;
	
	@Autowired
	CategoryService categoryService ;
	
	@Autowired
	SpuService spuService ;
	
	@Autowired
	PImgService pImgService;
	
	
	
	public List<SkuVO> getAll(){
		
		return null;
	}
	
	
	@Transactional
	public int insertSKU(int spuID, List<Integer> prices, List<Integer> stocks, List<JsonElement> attrList) {

		// List 統一處理
		List<SkuVO> skus = new ArrayList<SkuVO>();

		// 確保資料長度一致
		if (prices.size() == stocks.size() || prices.size() == attrList.size()) {

			for (int i = 0; i < prices.size(); i++) {
				skus.add(new SkuVO(null, spuID, prices.get(i), stocks.get(i), attrList.get(i) + "", 1,
						new Timestamp(System.currentTimeMillis())));
			}

		}

		return skuDAO.insertSKU(skus);
	}

	@Override
	public int updateProd(SkuVO skuVO) {

		return skuDAO.updateProd(skuVO);
	}

	@Override
	public int takeOrder(List<SkuVO> checkOut) {

		return skuDAO.updateProds(checkOut);
	}

	@Override
	public void insertProd(String ctgName, String spuName, String descript, List<Integer> prices, List<Integer> stocks,
			List<JsonElement> attrList, ImgTFVO imgs) {

		int ctgID = categoryService.queryCtgID(ctgName);
		System.out.println(ctgID+"===ctgID");
		

		int spuID = spuService.insertSPU(ctgID, spuName, descript);
		System.out.println(spuID+"===spuID");

		int insertSKU = insertSKU(spuID, prices, stocks, attrList);
		System.out.println(insertSKU+"========insertSKU");
		
		// 照片上傳
		int addPics = pImgService.addPics(spuID, imgs);
		System.out.println(addPics+"============addPics");

	}

}
