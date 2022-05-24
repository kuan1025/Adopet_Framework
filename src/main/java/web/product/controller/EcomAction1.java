package web.product.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import web.product.entity.PImgVO;
import web.product.entity.ProdSelection;
import web.product.entity.SkuVO;
import web.product.service.PImgService;
import web.product.service.SpuService;

@Controller
@RequestMapping("/comAction")
public class EcomAction1 {

	// 每頁想要呈現的比數
	public static final int PAGE_SIZE = 8;

	@Autowired
	PImgService pImgService;

	@Autowired
	SpuService spuService;

	PageInfo<SkuVO> skuPage;

	Gson gson = new Gson();

	@GetMapping("/ecoMainP")
	public String ecoMainP(HttpSession session) {

		Object attribute = session.getAttribute("skuPage");

		skuPage = spuService.getMainPage(1, PAGE_SIZE);

		session.setAttribute("skuPage", skuPage);

		return "/ecommerce/Pet_Supplement";
	}

	@GetMapping("/getPic/{skuID}")
	@ResponseBody
	public void getPic(HttpServletResponse resp, @PathVariable String skuID) {

		// 錯誤驗證
		LinkedList<String> errorMsgs = new LinkedList<String>();
		try {

			if (skuID == null || (skuID.trim().length()) == 0) {
				errorMsgs.add("照片請求有誤");
			} else {

				PImgVO pic = pImgService.getPic(Integer.parseInt(skuID));
				byte[] spuImg = pic.getSpuImg();

				OutputStream outputSream = resp.getOutputStream();
				InputStream in = new ByteArrayInputStream(spuImg);
				int len = 0;
				byte[] buf = new byte[1024];
				while ((len = in.read(buf, 0, 1024)) != -1) {
					outputSream.write(buf, 0, len);
				}
				outputSream.close();
			}
		} catch (Exception e) {
			errorMsgs.add("照片上傳有誤" + e.getMessage());
			System.out.println(errorMsgs);
		}

	}

	// 資料有點多偷懶ＱＱ
	@PostMapping("/requirement")
	@ResponseBody
	public void requirement(HttpServletRequest req, HttpSession session) {

		String data;
		try {
			data = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);

			JsonObject fromJson = gson.fromJson(data, JsonObject.class);
			ProdSelection prodSelection;

			Integer lowC = null;
			Integer highC = null;
			Integer ctgID = null;
			String prodName = null;
			Integer curPage = null;
			// 不能為null 數字需要做判斷
			// 先轉字串 測試不為null 再轉數字

			String jsonLowC = fromJson.get("lowC").getAsString();

			if (jsonLowC != null && !"".equals(jsonLowC)) {
				lowC = Integer.parseInt(jsonLowC);
			}

			String jsonHighC = fromJson.get("highC").getAsString();
			if (jsonHighC != null && !"".equals(jsonHighC)) {
				highC = Integer.parseInt(jsonHighC);
			}

			String jsonCtgID = fromJson.get("ctgID").getAsString();
			if (jsonCtgID != null) {
				ctgID = Integer.parseInt(jsonCtgID);
				// 前端預設值ctgID = -1;
				if (ctgID == -1) {
					ctgID = null;
				}
			}

			String jsonCurPage = fromJson.get("curPage").getAsString();
			if (jsonCurPage != null && !"".equals(jsonCurPage)) {
				curPage = Integer.parseInt(jsonCurPage);

			}

			String jsonprodName = fromJson.get("prodName").getAsString();
			if (jsonprodName != null && !"".equals(jsonprodName)) {
				prodName = jsonprodName;
			} else {
				prodName = null;
			}

			prodSelection = new ProdSelection(lowC, highC, ctgID, prodName);
			// 查詢處理 放入session
			skuPage = spuService.selectedPage(prodSelection, curPage, PAGE_SIZE);
			req.getSession().setAttribute("skuPage", skuPage);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
