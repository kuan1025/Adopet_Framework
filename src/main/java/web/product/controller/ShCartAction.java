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
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import configuration.SpringJavaConfig;
import web.order.entity.OrderDetailVO;
import web.order.entity.OrdersVO;
import web.order.service.OrderDetailService;
import web.order.service.OrdersService;
import web.product.entity.CartItem;
import web.product.entity.SkuVO;
import web.product.service.AttributesService;
import web.product.service.CartService;
import web.product.service.CategoryService;
import web.product.service.SkuService;
import web.product.service.SpuService;
import web.product.service.impl.CartServiceImp;
import web.product.service.impl.SkuServiceImp;
import web.product.service.impl.SpuServiceImp;

//@WebServlet("/shCartAction")
public class ShCartAction extends HttpServlet {
	Gson gson = new Gson();

	ApplicationContext context = new AnnotationConfigApplicationContext(SpringJavaConfig.class);
	CartService cartService = context.getBean("cartServiceImp",CartService.class);
	SpuService spuService = context.getBean("spuServiceImp",SpuService.class);
	
	OrdersService orderService=new OrdersService();
	OrderDetailService orderDetailServiceImp=new OrderDetailService();
	

	SkuService skuService= context.getBean("skuServiceImp",SkuService.class);
	
	
	
	
	

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

		String action = req.getParameter("action");

		if ("getCart".equals(action)) {

			try {
			// ????????????
			// ??????memID = 1
			Integer memID = new Integer(1);
			
			List<CartItem> cart = cartService.getCart(memID);

			// ????????????List???????????????
			List<SkuVO> skuVO = new ArrayList<SkuVO>();

			// ???cart?????? spuID ????????????

			cart.forEach(e -> {
				SkuVO skuID = new SkuVO();
				skuVO.add(spuService.getSkuVO(e.getSkuID()));
			});
			req.setAttribute("cart", cart);
			req.setAttribute("cartList", skuVO);
			req.getRequestDispatcher("/views/ecommerce/shoping-cart.jsp").forward(req, resp);
			return;
			}catch (Exception e) {
				System.out.println("?????????????????????");
			}
		}

		doPost(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

		String data = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);

		JsonObject fromJson = gson.fromJson(data, JsonObject.class);

		String action = fromJson.get("action").getAsString();

		// ????????????
		if ("addItem".equals(action)) {
			try {
			// ?????????????????? ????????????id
//			req.getSession().getAttribute("memID")
			// ?????????memID = 1
			Integer memID = new Integer(1);
			JsonElement jsonElement = fromJson.get("cartItem");
			CartItem cartItem = gson.fromJson(jsonElement, CartItem.class);
			String res = cartService.addItem(memID, cartItem);
			resp.getWriter().append(gson.toJson(res));
			}catch (Exception e) {
				System.out.println("??????????????????");
			}
			return;
			// ???????????????
		} else if ("updateCart".equals(action)) {

			try {
			// ????????????

			// ??????memID = 1
			Integer memID = new Integer(1);
			JsonElement jsonElement = fromJson.get("cartItem");

			CartItem cartItem = gson.fromJson(jsonElement, CartItem.class);

			// ?????????????????????skuVO
			SkuVO skuVO = cartService.updateCartItem(memID, cartItem);
		
			resp.getWriter().append(gson.toJson(skuVO));
			}catch (Exception e) {
				System.out.println("????????????");
			}
			
			return;
			
		} else if ("deleteCart".equals(action)) {

			try {
			// ????????????

			// ??????memID = 1
			Integer memID = new Integer(1);
			JsonElement jsonElement = fromJson.get("cartItem");

			CartItem cartItem = gson.fromJson(jsonElement, CartItem.class);
			String res = cartService.delSingleItem(memID, cartItem);

			resp.getWriter().append(gson.toJson(res));
			}catch (Exception e) {
				System.out.println("????????????");
			}

			return;
		} else if ("updateNum".equals(action)) {

			try {
			// ????????????

			// ??????memID = 1
			Integer memID = new Integer(1);
			JsonElement jsonElement = fromJson.get("cartItem");

			CartItem cartItem = gson.fromJson(jsonElement, CartItem.class);

			Integer updateNum = cartService.updateNum(memID, cartItem);

			resp.getWriter().append(gson.toJson(updateNum));
			}catch (Exception e) {
				System.out.println("?????????????????????????????????");
			}
			return;
			// ??????????????????
		} else if ("cartCheckOut".equals(action)) {
			
			try {
				
			JsonElement jsonElement = fromJson.get("skuIDArr");
			// ??????List ??????????????????skuID
			List<Integer> skuIDArr = gson.fromJson(jsonElement, new TypeToken<List<Integer>>() {
			}.getType());

			// ??????????????????list ????????????????????????
			List<OrderDetailVO> detail = cartService.cartCheckOut(skuIDArr);
			

			HttpSession session = req.getSession();
			session.setAttribute("checkout", detail);


			req.getRequestDispatcher("/views/ecommerce/checkout.jsp").forward(req, resp);
			
			}catch (Exception e) {
				System.out.println("????????????????????????");
			}
			return;

		} else if ("takeOrder".equals(action)) {
			
		try {
			
		
			JsonElement jsonElement = fromJson.get("skuIDArr");
			// ??????List ??????????????????skuID
			List<Integer> skuIDArr = gson.fromJson(jsonElement, new TypeToken<List<Integer>>() {
			}.getType());
			// ????????????
			Integer memID = new Integer(1);
			
		// ????????????
			jsonElement = fromJson.get("ordersVO");
			
		
			
			OrdersVO ordersVO = gson.fromJson(jsonElement, OrdersVO.class);
			
	
			String takeOrder = cartService.takeOrder(skuIDArr, ordersVO, memID);

			// ??????session
			req.getSession().removeAttribute("checkout");
			resp.getWriter().append(takeOrder);
		}catch (Exception e) {
			System.out.println("????????????");
		}

		}

		return;
	}

}
