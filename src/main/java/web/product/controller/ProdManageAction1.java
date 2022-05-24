package web.product.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import web.product.entity.SkuVO;
import web.product.service.SkuService;
import web.product.service.SpuService;

@RestController
@RequestMapping("/prodManage")
public class ProdManageAction1 {
	
	@Autowired
	SpuService spuSerive;
	
	@Autowired
	SkuService skuService;
	
	
	Gson gson = new Gson();
	
	@GetMapping("/prodList")
	public String prodList(HttpSession httpSession) {
		
		try {
		List<SkuVO> allProds = spuSerive.getAllProd();

		httpSession.setAttribute("allProds", allProds);
		return null;
		} catch (Exception e) {
			System.out.println("有誤ProdList");
		}
		
		return null;
	}
	
	@PutMapping("/update")
	public String updateProd(@RequestBody SkuVO skuVO,HttpSession session) {
		System.out.println("update");
		System.out.println(skuVO);
		try {
			
		
		int update = skuService.updateProd(skuVO);
		
		if (update != -1) {
			// 更新後 也要刷新產品req
			List<SkuVO> allProds = spuSerive.getAllProd();

			session.setAttribute("allProds", allProds);

			return "更新成功";
		}
		}catch (Exception e) {
			System.out.println("有誤updateProd");
		}
		return "更新有誤";
	}
	
	@GetMapping("/searchByName/{prodName}")
	public String searchByName(@PathVariable String prodName,HttpSession session) {
		System.out.println("/searchByName");
		// 錯誤驗證
					LinkedList<String> errorMsgs = new LinkedList<String>();
					try {
					
					if(!"".equals(prodName)&&!(prodName.trim().length()==0)) {				
					List<SkuVO> allProds = spuSerive.queryByName(prodName);
					session.setAttribute("allProds", allProds);
					}
					return "show";
				} catch (Exception e) {
						errorMsgs.add("查詢商品名稱有誤" + e.getMessage());
						System.out.println(errorMsgs);
					}
		return "fail";
	}
	
	
	
	
	
	

}
