package web.product.controller;

import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import web.order.entity.OrderDetailVO;
import web.order.entity.OrdersVO;
import web.product.entity.CartItem;
import web.product.entity.SkuVO;
import web.product.service.CartService;
import web.product.service.SkuService;
import web.product.service.SpuService;


@Controller
@RequestMapping("/shCartAction")
public class ShCartAction1 {
	
	Gson gson = new Gson();
	
	@Autowired
	CartService cartService ;
	
	@Autowired
	SpuService spuService ;
	
	@Autowired
	SkuService skuService;
	
	
	@GetMapping("/cart")
	public String getCart(HttpServletRequest req) {
		
		try {
			// 判斷登入
			// 假定memID = 1
			Integer memID = new Integer(1);
			
			List<CartItem> cart = cartService.getCart(memID);

			// 建立一個List做畫面呈現
			List<SkuVO> skuVO = new ArrayList<SkuVO>();

			// 用cart裡的 spuID 去找產品

			cart.forEach(e -> {
				skuVO.add(spuService.getSkuVO(e.getSkuID()));
			});
			
			req.setAttribute("cart", cart);
			req.setAttribute("cartList", skuVO);
			
			}catch (Exception e) {
				System.out.println("取得購物車有誤");
			}
		
		return "/ecommerce/shoping-cart";
	}
	@PostMapping("/cart")
	@ResponseBody
	public void addItem(@RequestBody String cartItem,HttpServletResponse resp) {
					
		CartItem data = gson.fromJson(cartItem, CartItem.class);

	
		try {
			
			// 判斷是否登入 取得用戶id
//			req.getSession().getAttribute("memID")
			// 先假設memID = 1
			Integer memID = new Integer(1);
			
			String res = cartService.addItem(memID, data);
			resp.getWriter().append(gson.toJson(res));
		} catch (Exception e) {
			System.out.println("商品新增有誤");
		}
	}
	@PutMapping("/cart")
	@ResponseBody
	public void updateCart(@RequestBody String cartItem,HttpServletResponse resp) {
		
		resp.setContentType("text/html; charset=UTF-8");
		
		CartItem data = gson.fromJson(cartItem, CartItem.class);
		
		try {
			// 判斷登入

			// 假定memID = 1
			Integer memID = new Integer(1);
			
			// 新增並取得新的skuVO
			SkuVO skuVO = cartService.updateCartItem(memID, data);
			
			resp.getWriter().append(gson.toJson(skuVO));
			}catch (Exception e) {
				System.out.println("更改有誤");
			}
		
	}
	@DeleteMapping("/cart")
	@ResponseBody
	public void deleteCart(@RequestBody String cartItem,HttpServletResponse resp) {
		
		CartItem data = gson.fromJson(cartItem, CartItem.class);
		try {
			// 判斷登入
			// 假定memID = 1
			Integer memID = new Integer(1);
			
			String res = cartService.delSingleItem(memID, data);

			resp.getWriter().append(gson.toJson(res));
			}catch (Exception e) {
				System.out.println("刪除有誤");
			}
	}
	
	@PutMapping("/updateNum")
	@ResponseBody
	public void updateNum(@RequestBody String cartItem,HttpServletResponse resp) {
		
		CartItem data = gson.fromJson(cartItem, CartItem.class);
		try {
		// 判斷登入

		// 假定memID = 1
		Integer memID = new Integer(1);

		Integer updateNum = cartService.updateNum(memID, data);

		resp.getWriter().append(gson.toJson(updateNum));
		}catch (Exception e) {
			System.out.println("修正購物車商品數量有誤");
		}
		
	}
	
	@PostMapping("/cartCheckOut")
	@ResponseBody
	public String cartCheckOut(HttpServletRequest req) {
		
		try {
				
		String data = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);

		JsonObject fromJson = gson.fromJson(data, JsonObject.class);
		
		JsonElement jsonElement = fromJson.get("skuIDArr");
		// 轉成List 取得帶結帳的skuID
		List<Integer> skuIDArr = gson.fromJson(jsonElement, new TypeToken<List<Integer>>() {
		}.getType());

		// 提供商品規格list 取得訂單明細物件
		List<OrderDetailVO> detail = cartService.cartCheckOut(skuIDArr);
		

		HttpSession session = req.getSession();
		session.setAttribute("checkout", detail);
		return "/ecommerce/checkout";

		
		}catch (Exception e) {
			System.out.println("有error");
		}
		return "/ecommerce/checkout";
	}
	@PostMapping("/takeOrder")
	@ResponseBody
	public void takeOrder(HttpServletRequest req,HttpServletResponse resp) {
		
		try {
			
			String data = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);

			JsonObject fromJson = gson.fromJson(data, JsonObject.class);
			
			JsonElement jsonElement = fromJson.get("skuIDArr");
			// 轉成List 取得帶結帳的skuID
			List<Integer> skuIDArr = gson.fromJson(jsonElement, new TypeToken<List<Integer>>() {
			}.getType());
			// 會員驗證
			Integer memID = new Integer(1);
			
			// 建立訂單
			jsonElement = fromJson.get("ordersVO");
			
			OrdersVO ordersVO = gson.fromJson(jsonElement, OrdersVO.class);
			
			
			String takeOrder = cartService.takeOrder(skuIDArr, ordersVO, memID);

			// 清除session
			req.getSession().removeAttribute("checkout");
			resp.getWriter().append(takeOrder);
			

		}catch (Exception e) {
			System.out.println("結帳有誤");
		}
		
		
	}
	
	
	
	
	

}
