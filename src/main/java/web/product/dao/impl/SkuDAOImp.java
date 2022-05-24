package web.product.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import web.product.dao.SkuDAO;
import web.product.entity.SkuVO;
import web.product.util.HibernateUtil;



public class SkuDAOImp implements SkuDAO {

//	@PersistenceContext
	Session session;
	
	public Session getSession() {
		return this.session;
	}
	
	
	@Override
	public int insertSKU(List<SkuVO> skus) {

		
		
		int index = -1;
		skus.forEach(item -> {
			 getSession().save(item);

		});
		index = 1;


		return index;
	}

	@Override
	public int updateProd(SkuVO skuVO) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateProds(List<SkuVO> skus) {
		// TODO Auto-generated method stub
		return 0;
	}



	

}
