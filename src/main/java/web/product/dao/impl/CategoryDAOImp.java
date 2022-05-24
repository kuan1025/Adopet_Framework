package web.product.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import lombok.val;
import web.product.dao.CategoryDAO;
import web.product.entity.CategoryVO;
import web.product.util.HibernateUtil;

@Repository
public class CategoryDAOImp implements CategoryDAO {

	@PersistenceContext
	private Session session;
//	private SessionFactory sessionFactory;

	public Session getSession() {
		return session;
	}

	public Integer queryCtgID(String ctgName) {

	

		// 1. 取得builder
		CriteriaBuilder builder = getSession().getCriteriaBuilder();

		// 2. builder 取得createQuery 物件
		CriteriaQuery<CategoryVO> query = builder.createQuery(CategoryVO.class);
		Root<CategoryVO> root = query.from(CategoryVO.class);
		Predicate p1 = builder.like(root.get("ctgName"), ctgName);
		query = query.where(p1);
		query.select(root);
		// 第三部
		TypedQuery<CategoryVO> typed = session.createQuery(query);
		List<CategoryVO> list = typed.getResultList();




		return list.get(0).getCtgID();
	}

	@Override
	public List<CategoryVO> getAllCategory() {

		Session session = this.getSession();

		CriteriaBuilder builder = session.getCriteriaBuilder();

		CriteriaQuery<CategoryVO> query = builder.createQuery(CategoryVO.class);
		Root<CategoryVO> root = query.from(CategoryVO.class);

		query.select(root);
		// 第三部
		TypedQuery<CategoryVO> typed = session.createQuery(query);
		List<CategoryVO> list = typed.getResultList();

		return list;
	}

	


	

}
