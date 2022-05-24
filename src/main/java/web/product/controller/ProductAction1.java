package web.product.controller;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import web.product.entity.AttrWithValue;
import web.product.entity.CategoryVO;
import web.product.entity.ImgTFVO;
import web.product.service.AttributesService;
import web.product.service.CategoryService;
import web.product.service.SkuService;


@RestController
@RequestMapping("/ProductAction")
public class ProductAction1 {
	Gson gson=new Gson();
	
	@Autowired
	CategoryService categoryService ;
	@Autowired
	AttributesService attributesService ;
	@Autowired
	SkuService skuService;
	
	
	
	@GetMapping("/{ctgName}")
	public String getAttrList(@PathVariable String ctgName) {
		try {	
			
		// 查詢該類型的id
		int ctgID = categoryService.queryCtgID(ctgName);
		
		
		// 取得對應的Attr模板
		 List<AttrWithValue> attrWithValue = attributesService.getAll(ctgID);
		 return gson.toJson(attrWithValue);
		} catch (Exception e) {
			System.out.println("取得規格模板有誤！");
			
			return null;
		}	
		 
		
		
	}
	
	@GetMapping("/getCatgName")
	public String getCatgName() {
		
		
		try {	
			List<CategoryVO> allCategory = categoryService.getAllCategory();
			
			return gson.toJson(allCategory);
			} catch (Exception e) {
				System.out.println("取得所有商品類型有誤！");
			}
		
		return null;
	}
	
	@PostMapping("/launchProd")
	public String launchProd(HttpServletRequest req) {

		
			System.out.println("進入");
			try {
				String data = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
				
				JsonObject fromJson = gson.fromJson(data, JsonObject.class);
				
				
				
				// 類型名稱
				String ctgName = fromJson.get("ctgName").getAsString();
				// 商品名稱
				String spuName = fromJson.get("spuName").getAsString();
				// 商品描述
				String descript = fromJson.get("descript").getAsString();
				// 商品圖片 base64
				JsonObject jObjectImg = fromJson.get("spuImgs").getAsJsonObject();
				ImgTFVO imgs = gson.fromJson(jObjectImg, ImgTFVO.class);
				// 規格
				// List接 所有選擇規格
				List<JsonElement> attrList = new ArrayList<>();
				JsonArray attrArr = fromJson.get("specAttrs").getAsJsonArray();
				attrArr.forEach(item -> {
					attrList.add(item);
				});
				// 接產品所有庫存
				JsonArray jstocks = fromJson.get("stocks").getAsJsonArray();
				List<Integer> stocks = new ArrayList<>();
				for (JsonElement element : jstocks) {
					stocks.add(gson.fromJson(element, Integer.class));
				}
				// 接產品所有價格
				JsonArray skuPrices = fromJson.get("skuPrices").getAsJsonArray();
				List<Integer> prices = new ArrayList<>();
				for (JsonElement element : skuPrices) {
					prices.add(gson.fromJson(element, Integer.class));
				}
				
				// 進行商品新增
				skuService.insertProd(ctgName, spuName, descript, prices, stocks, attrList, imgs);
					}catch (Exception e) {
						System.out.println("商品新增失敗");
					}
				
	
		
		return null;
		
	}
	

}
