package web.product.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import configuration.SpringJavaConfig;
import web.product.entity.AttrWithValue;
import web.product.entity.CategoryVO;
import web.product.entity.ImgTFVO;
import web.product.service.AttributesService;
import web.product.service.CategoryService;
import web.product.service.PImgService;
import web.product.service.SkuService;
import web.product.service.SpuService;
import web.product.service.ValueService;
import web.product.service.impl.AttributesServiceImp;
import web.product.service.impl.CategoryServiceImp;
import web.product.service.impl.PImgServiceImp;
import web.product.service.impl.SkuServiceImp;
import web.product.service.impl.SpuServiceImp;
import web.product.service.impl.ValueServiceImp;


//@WebServlet("/ProductAction")
public class ProductAction extends HttpServlet {
	private static final long serialVersionUID = 1L;

	
	private static Gson gson = null;
	
	ApplicationContext context = new AnnotationConfigApplicationContext(SpringJavaConfig.class);
	CategoryService categoryService = context.getBean("categoryServiceImp",CategoryService.class);
	AttributesService attributesService = context.getBean("attributesServiceImp",AttributesService.class);
	SkuService skuService= context.getBean("skuServiceImp",SkuService.class);
	
	



	public ProductAction() throws ServletException {

	
		gson = new Gson();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		
		
		String data = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		
		JsonObject fromJson = gson.fromJson(data, JsonObject.class);
		
			
		String action = fromJson.get("action").getAsString();
		
		
		
	
		
		// ????????????  
		if("launchProd".equals(action)) {
			try {
		// ????????????
		String ctgName = fromJson.get("ctgName").getAsString();
		// ????????????
		String spuName = fromJson.get("spuName").getAsString();
		// ????????????
		String descript = fromJson.get("descript").getAsString();
		// ???????????? base64
		JsonObject jObjectImg = fromJson.get("spuImgs").getAsJsonObject();
		ImgTFVO imgs = gson.fromJson(jObjectImg, ImgTFVO.class);
		// ??????
		// List??? ??????????????????
		List<JsonElement> attrList = new ArrayList<>();
		JsonArray attrArr = fromJson.get("specAttrs").getAsJsonArray();
		attrArr.forEach(item -> {
			attrList.add(item);
		});
		// ?????????????????????
		JsonArray jstocks = fromJson.get("stocks").getAsJsonArray();
		List<Integer> stocks = new ArrayList<>();
		for (JsonElement element : jstocks) {
			stocks.add(gson.fromJson(element, Integer.class));
		}
		// ?????????????????????
		JsonArray skuPrices = fromJson.get("skuPrices").getAsJsonArray();
		List<Integer> prices = new ArrayList<>();
		for (JsonElement element : skuPrices) {
			prices.add(gson.fromJson(element, Integer.class));
		}
		
		// ??????????????????
		skuService.insertProd(ctgName, spuName, descript, prices, stocks, attrList, imgs);
			}catch (Exception e) {
				System.out.println("??????????????????");
			}
		
		
		// ???????????? ??????????????????
		}else if("getCatgName".equals(action)){
			try {	
			List<CategoryVO> allCategory = categoryService.getAllCategory();
			response.getWriter().append(gson.toJson(allCategory));
			} catch (Exception e) {
				System.out.println("?????????????????????????????????");
			}
		// ????????????????????????
		}else if("getAttrList".equals(action)) {
			try {	
			String ctgName = fromJson.get("ctgName").getAsString();
			
			// ??????????????????id
			int ctgID = categoryService.queryCtgID(ctgName);
			
			// ???????????????Attr??????
			 List<AttrWithValue> attrWithValue = attributesService.getAll(ctgID);
		
			response.getWriter().append(gson.toJson(attrWithValue));
			} catch (Exception e) {
				System.out.println("???????????????????????????");
			}
		}

	}

}
