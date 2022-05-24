package web.product.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import web.product.dao.SpuDAO;
import web.product.entity.ProdSelection;
import web.product.entity.SkuVO;
import web.product.entity.SpuVO;


@Repository
public class SpuDAOJDBC implements SpuDAO {
	
	
	@PersistenceContext
	Session session;
	
	public Session getSession() {
		return session;
	}
	
		

	
	@Override
	public SpuVO inserSPU(SpuVO spuVO) {
		Integer id = (Integer)getSession().save(spuVO);
		spuVO.setSpuID(id);
		return spuVO;		
	}

	@Override
	public List<SkuVO> getAllProd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SkuVO> queryByName(String prodName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SkuVO> getMainPage() {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public List<SkuVO> selectedPage(ProdSelection prodSelection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SpuVO getDetail(SpuVO souSpuVO) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SkuVO getPriceAndStock(List<String> key,Integer spuID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SkuVO getSkuVO(Integer skuID) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
