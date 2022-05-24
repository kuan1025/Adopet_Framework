package web.product.dao.impl;
import java.util.List;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import web.product.dao.PImgDAO;
import web.product.entity.PImgVO;
import web.product.entity.SpuVO;
import web.product.util.HibernateUtil;



public class PImgDAOImp implements PImgDAO {
	
//	@PersistenceContext
	Session session;
	
	public Session getSession() {
		return session;
	}
	
	
	
	

	public int uploadPics(List<PImgVO> imgList) {

		

		int index = -1;
		imgList.forEach(item -> {
			getSession().save(item);
		});

		index = 1;


		return index;
	}

	@Override
	public PImgVO getPicByID(Integer skuID) {
		
		
		PImgVO pImgVO = getSession().get(PImgVO.class, skuID);
		
		return pImgVO;
	}
	

	@Override
	public List<PImgVO> getSpuPics(SpuVO spuVO) {
		// TODO Auto-generated method stub
		return null;
	}
}
