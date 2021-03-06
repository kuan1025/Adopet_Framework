package web.product.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.javassist.expr.NewArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ecpay.payment.integration.AllInOne;
import ecpay.payment.integration.domain.AioCheckOutALL;
import web.order.entity.OrderDetailVO;
import web.order.entity.OrdersVO;
import web.order.service.OrderDetailService;
import web.order.service.OrdersService;
import web.product.dao.CartDAO;
import web.product.dao.impl.CartDAOImp;
import web.product.entity.CartItem;
import web.product.entity.SkuVO;
import web.product.service.CartService;
import web.product.service.SkuService;
import web.product.service.SpuService;

@Service
public class CartServiceImp implements CartService {

	
	CartDAO cartDao=new CartDAOImp();
	OrdersService orderService = new OrdersService();
	OrderDetailService orderDetailService = new OrderDetailService();
	
	@Autowired
	SpuService spuService;	
	@Autowired
	SkuService skuService;
	
	
	
	
	public static AllInOne allInOne;

	@Override
	public String addItem(Integer memID, CartItem cartItem) {

		return cartDao.addItem(memID, cartItem);
	}

	@Override
	public String updateItem(Integer memID, CartItem cartItem) {

		return cartDao.updateItem(memID, cartItem);
	}

	@Override
	public String delSingleItem(Integer memID, CartItem cartItem) {

		return cartDao.delSingleItem(memID, cartItem);
	}

	@Override
	public String delMulItem(Integer memID, List<CartItem> cartItems) {

		return cartDao.delMulItem(memID, cartItems);
	}

	@Override
	public List<CartItem> getCart(Integer memID) {

		return cartDao.getCart(memID);
	}

	@Override
	public Integer updateNum(Integer memID, CartItem cartItem) {

		return cartDao.updateNum(memID, cartItem);
	}

	@Override
	public SkuVO updateCartItem(Integer memID, CartItem cartItem) {

		String res = updateItem(memID, cartItem);
		int skuID = Integer.parseInt(res);
		// ???skuID?????? ??????
		SkuVO skuVO = spuService.getSkuVO(skuID);

		return skuVO;
	}

	public List<OrderDetailVO> cartCheckOut(List<Integer> skuIDArr) {

		// List ??????skuID
		List<SkuVO> skuVO = new ArrayList<SkuVO>();

		// List ??????checkout cart
		List<CartItem> cart = new ArrayList<CartItem>();

		// ??????????????? mysql
		skuIDArr.forEach(e -> {
			// ????????????
			skuVO.add(spuService.getSkuVO(e));
		});
		// ???redis ???????????????
		// ??????memID = 1
		List<CartItem> allCart = getCart(new Integer(1));
		// ??????????????????????????????
		for (int j = 0; j < skuVO.size(); j++) {
			for (int i = 0; i < allCart.size(); i++) {
				CartItem cartItem = allCart.get(i);
				SkuVO skuVOItem = skuVO.get(j);
				// ???noSQL??????????????????skuID
				if (cartItem.getSkuID() == skuVOItem.getSkuID()) {
					cart.add(cartItem);
				}
			}
		}
		// ??????????????????(????????????)
		// cart ??? skuvo ????????????????????? ?????????????????????????????? ???????????????
		List<OrderDetailVO> detail = new ArrayList<OrderDetailVO>();
		for (int i = 0; i < skuVO.size(); i++) {
			SkuVO skuItem = skuVO.get(i);
			OrderDetailVO orderDetailVO = new OrderDetailVO(null, null, skuItem.getSkuID(),
					skuItem.getSpuVO().getSpuName(), cart.get(i).getNum(), skuItem.getSkuPrice());
			detail.add(orderDetailVO);
		}

		return detail;
	}

	public String takeOrder(List<Integer> skuIDArr, OrdersVO ordersVO, Integer memID) {

		// List ??????checkout cart
		List<CartItem> cart = new ArrayList<CartItem>();
		// ???redis???????????????????????????
		List<CartItem> allCart = getCart(memID);
		// ??????????????????????????????
		for (int j = 0; j < skuIDArr.size(); j++) {
			for (int i = 0; i < allCart.size(); i++) {
				CartItem cartItem = allCart.get(i);

				Integer skuVOItem = skuIDArr.get(j);
				// ???noSQL??????????????????skuID
				if (cartItem.getSkuID() == skuVOItem) {
					cart.add(cartItem);
				}
			}
		}

		// ????????????????????? ??????????????? ??????

		// ?????????mysql ?????????skuVO ?????????????????????
		// ??????List ??????
		List<SkuVO> readyCheck = new ArrayList<SkuVO>();
		// skuID list?????????????????????
		skuIDArr.forEach(e -> {
			// ???????????? ??????????????????
			readyCheck.add(spuService.getSkuVO(e));
		});

		// ???list?????????????????????
		List<SkuVO> sellList = new ArrayList<SkuVO>();
		// ???????????? ???????????? - ??????????????????
		for (int j = 0; j < cart.size(); j++) {
			for (int i = 0; i < readyCheck.size(); i++) {

				CartItem cartItem = cart.get(j);
				SkuVO skuVO = readyCheck.get(i);

				// ???redis??????????????????skuID
				if (cartItem.getSkuID() == skuVO.getSkuID()) {
					Integer curstock = skuVO.getStock();
					// ????????????
					Integer cartNum = cartItem.getNum();
					curstock = curstock - cartNum;
					skuVO.setStock(curstock);
					sellList.add(skuVO);
				}
			}
		}
		// ?????????????????????
		List<String> prodName = new ArrayList<String>();
		// ????????????????????????
		for (int i = 0; i < sellList.size(); i++) {

			SkuVO skuVO = sellList.get(i);
			prodName.add(skuVO.getSpuVO().getSpuName());

		}

		// ??????????????????
		Optional<String> reduce = prodName.stream().reduce((String acc, String curr) -> {
			return acc + "#" + curr;
		});

		String itemName = reduce.get();

		// ??????
		ordersVO.setMemID(memID);

		// ????????????
		int gk = orderService.insertOrderGk(ordersVO);
		//
		Integer orderPrice = ordersVO.getOrderPrice();

		// ????????????
		orderDetailService.addDetails(sellList, memID, gk, cart);

		// ??????
		// ??????
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String orderDate = sdf.format(new Date());

		allInOne = new AllInOne("");

		System.out.println();

		AioCheckOutALL aCheckOut = new AioCheckOutALL();
		aCheckOut.setMerchantTradeNo(gk + "tga101");
		aCheckOut.setMerchantTradeDate(orderDate);
		aCheckOut.setTotalAmount(orderPrice + "");
		aCheckOut.setTradeDesc("test");
		aCheckOut.setItemName(itemName);
		aCheckOut.setClientBackURL("http://localhost:8081/Adopets/comAction?action=ecoMainP");

		aCheckOut.setReturnURL("http://localhost:8081/Adopets//epayCheckOrder");


		aCheckOut.setNeedExtraPaidInfo("N");
//		System.out.println(allInOne.aioCheckOut(aCheckOut, null));

		return allInOne.aioCheckOut(aCheckOut, null);

		// ?????????????????????domain??????????????????
		// ????????????
//		skuService.takeOrder(sellList);

		// ???????????????
//		cartService.delMulItem(memID, cart);

	}

}
