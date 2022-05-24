package web.product.controller;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import web.product.entity.PImgVO;
import web.product.entity.SkuVO;
import web.product.entity.SpuVO;
import web.product.service.PImgService;
import web.product.service.SpuService;

@Controller
@RequestMapping("/cartAction")
public class EcomCartAction1 {

	@Autowired
	SpuService spuService;

	@Autowired
	PImgService pImgService;

	Gson gson = new Gson();

	@GetMapping("/getDetail/{spuID}")
	public String getDetail(@PathVariable Integer spuID, HttpServletRequest request) {

		LinkedList<String> errorMsgs = new LinkedList<String>();

		try {

			if (spuID != null) {

				SpuVO spuVO = new SpuVO();

				spuVO.setSpuID(spuID);

				// 商品資訊
				SpuVO detail = spuService.getDetail(spuVO);
				// 傳屬性
				List<String> attrList = new ArrayList<String>();

				request.setAttribute("prodName", detail.getSpuName());
				request.setAttribute("descript", detail.getDescript());
				request.setAttribute("spuID", detail.getSpuID());

				detail.getSkuVO().forEach(e -> {
					attrList.add(e.getSpecAttr());
				});
				request.setAttribute("skuVO", attrList);

			}
		} catch (Exception e) {
			errorMsgs.add("進入商品詳情有錯誤" + e.getMessage());
			System.out.println(errorMsgs);

			return null;
		}
		return "/ecommerce/shop-details";
	}

	@ResponseBody
	@GetMapping("/getSpuPic/{pnum}/{spuID}")
	public void getSpuPic(@PathVariable String spuID, @PathVariable String pnum, HttpServletResponse resp) {

		// 錯誤驗證
		LinkedList<String> errorMsgs = new LinkedList<String>();

		try {

			SpuVO spuVO = new SpuVO();

			if (spuID != null && !"".equals(spuID) && pnum != null && !"".equals(pnum)) {
				Integer parseInt = Integer.parseInt(spuID);
				Integer pnumInt = Integer.parseInt(pnum);
				spuVO.setSpuID(parseInt);
				// 商品照片

				List<PImgVO> spuPics = pImgService.getSpuPics(spuVO);
				byte[] spuImg = spuPics.get(pnumInt).getSpuImg();

				OutputStream outputSream = resp.getOutputStream();
				InputStream in = new ByteArrayInputStream(spuImg);
				int len = 0;
				byte[] buf = new byte[1024];
				while ((len = in.read(buf, 0, 1024)) != -1) {
					outputSream.write(buf, 0, len);
				}
				outputSream.close();

				return;

			}
		} catch (Exception e) {
			errorMsgs.add("照片取得錯誤" + e.getMessage());
			System.out.println(errorMsgs);
			return;
		}

	}

	@PostMapping("/getPrcSk")
	@ResponseBody
	public void getPrcSk(HttpServletRequest req, HttpServletResponse resp) {

		// 錯誤驗證
		LinkedList<String> errorMsgs = new LinkedList<String>();
		try {
		String data = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);

		JsonObject fromJson = gson.fromJson(data, JsonObject.class);


				// 所有勾選規格
				JsonArray asJsonArray = fromJson.get("arr").getAsJsonArray();
				// 迴圈處理
				List<String> arr = new ArrayList<String>();

				asJsonArray.forEach(e -> {
					arr.add(e.getAsString().trim());
				});
				// 對應的spuID 去資料庫找對應的規格價格和數量
				JsonElement jsonElement = fromJson.get("spuID");
				Integer spuID = null;
				if (jsonElement != null) {
					spuID = jsonElement.getAsInt();
				}

				SkuVO stockAndPrice = spuService.getStockAndPrice(arr, spuID);

				resp.getWriter().append(gson.toJson(stockAndPrice));

			
		} catch (Exception e) {
			errorMsgs.add("商品詳情屬性規格取得錯誤" + e.getMessage());
			System.out.println(errorMsgs);
			return;
		}

	}

}
