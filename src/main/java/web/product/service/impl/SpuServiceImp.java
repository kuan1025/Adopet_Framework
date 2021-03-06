package web.product.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import web.product.dao.SpuDAO;
import web.product.dao.impl.SpuDAOJDBC;
import web.product.entity.ProdSelection;
import web.product.entity.SkuVO;
import web.product.entity.SpuVO;
import web.product.service.SpuService;

@Service
public class SpuServiceImp implements SpuService {

	
		@Autowired
		SpuDAO spuDao;
	

	// 取得產品價格 數量
	public SkuVO getStockAndPrice(List<String> key, Integer spuID) {

		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);

			SkuVO skuVO = spuDAO.getPriceAndStock(key, spuID);

			session.commit();
			return skuVO;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Transactional
	public int insertSPU(int ctgID, String spuName, String descript) {

		SpuVO spuVO = new SpuVO();
		if (spuName != null && descript != null) {

			spuVO.setCtgID(ctgID);
			spuVO.setSpuName(spuName);
			spuVO.setDescript(descript);

			return spuDao.inserSPU(spuVO).getSpuID();
		} else {
			return -1;
		}

	}

	@Override
	public List<SkuVO> getAllProd() {
		List<SkuVO> allProd = new ArrayList<>();
		Gson gson = new Gson();
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);
			allProd = spuDAO.getAllProd();
			// 考慮到前端畫面處理 轉為字串

			allProd = jsonPaser(allProd);

			session.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return allProd;
	}

	// 之後需做修正
	@Override
	public List<SkuVO> queryByName(String prodName) {

		List<SkuVO> allProd = new ArrayList<>();
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);
			allProd = spuDAO.queryByName(prodName);
			allProd = jsonPaser(allProd);
			session.commit();
		} catch (Exception e) {

		}

		return allProd;
	}

	// 首頁 分頁處理
	@Override
	public PageInfo<SkuVO> getMainPage(int curPage, Integer pageSize) {

		List<SkuVO> mainPage = new ArrayList<>();
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);

			PageHelper.startPage(curPage, pageSize);
			mainPage = spuDAO.getMainPage();
			mainPage = jsonPaser(mainPage);
			PageInfo<SkuVO> spuPageInfo = new PageInfo<>(mainPage);

			session.commit();
			return spuPageInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public PageInfo<SkuVO> selectedPage(ProdSelection prodSelection, Integer curPage, Integer pageSize) {

		List<SkuVO> selectedPage = new ArrayList<>();
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);

			// prodSelection 都為null 做預設查詢

			// 每次查詢 若沒有指定頁碼就預設為1
			if (curPage != 0) {
				PageHelper.startPage(curPage, pageSize);
			} else {
				PageHelper.startPage(1, pageSize);
			}
			selectedPage = spuDAO.selectedPage(prodSelection);
			selectedPage = jsonPaser(selectedPage);
			PageInfo<SkuVO> spuPageInfo = new PageInfo<>(selectedPage);

			session.commit();
			return spuPageInfo;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 取得產品細節
	public SpuVO getDetail(SpuVO spuVO) {

		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);

			SpuVO detail = spuDAO.getDetail(spuVO);

			session.commit();
			return detail;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 根據skuID找skuVo
	public SkuVO getSkuVO(Integer skuID) {
		try {
			InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
			SqlSessionFactory factory = sqlSessionFactoryBuilder.build(inputStream);
			SqlSession session = factory.openSession();

			SpuDAO spuDAO = session.getMapper(SpuDAO.class);

			SkuVO skuVO = spuDAO.getSkuVO(skuID);
			// 懶得再轉 用之前寫的方法
			List<SkuVO> list = new ArrayList<>();
			list.add(skuVO);
			
			list = jsonPaser(list);
			skuVO = list.get(0);
			return skuVO;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	// 4/29 初版

	// json 轉換！！
	public final static String regEx = "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";

	// 只要給一個集合 就做轉換
	private static List<SkuVO> jsonPaser(List<SkuVO> allProd) {

		Gson gson = new Gson();
		allProd.forEach(e -> {
			String fromJson = gson.fromJson(e.getSpecAttr(), String.class);
			// 型態為 k v k v 用內部類做轉換
			SpuAttrPasre data = gson.fromJson(fromJson, SpuAttrPasre.class);
			e.setSpecAttr(
					data.getAttrN1() + "\t" + data.getAttrV1() + "\t" + data.getAttrN2() + "\t" + data.getAttrV2());
		});
		return allProd;
	}

	// 內部類 僅用來做資料轉換
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	class SpuAttrPasre {
		private String attrN1;
		private String attrN2;
		private String attrV1;
		private String attrV2;
	}

}
