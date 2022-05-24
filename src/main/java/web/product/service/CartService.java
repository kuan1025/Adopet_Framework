package web.product.service;

import java.util.List;

import web.order.entity.OrderDetailVO;
import web.order.entity.OrdersVO;

import web.product.entity.CartItem;
import web.product.entity.SkuVO;


public interface CartService {

	
	
	
	String addItem(Integer memID,CartItem cartItem);
	
	String updateItem(Integer memID, CartItem cartItem);
	
	String delSingleItem(Integer memID, CartItem cartItem);
	
	String delMulItem(Integer memID, List<CartItem> cartItems);
	
	
	List<CartItem> getCart(Integer memID);
	
	
	Integer updateNum(Integer memID,CartItem cartItem);
	
	SkuVO updateCartItem(Integer memID, CartItem cartItem);
	
	List<OrderDetailVO>  cartCheckOut(List<Integer> skuIDArr);
	
	String takeOrder(List<Integer> skuIDArr, OrdersVO ordersVO, Integer memID); 
	
}
