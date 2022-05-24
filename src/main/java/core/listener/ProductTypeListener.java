package core.listener;

import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import configuration.SpringJavaConfig;
import web.product.entity.CategoryVO;
import web.product.service.CategoryService;


@WebListener
public class ProductTypeListener implements ServletContextListener {

	
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		
		ApplicationContext context = new AnnotationConfigApplicationContext(SpringJavaConfig.class);
		CategoryService categoryServiceImp = context.getBean("categoryServiceImp",CategoryService.class);
	
		List<CategoryVO> categoryList = categoryServiceImp.getAllCategory();
//
		sce.getServletContext().setAttribute("categoryList",categoryList);
	}
	
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

		 

	}


	
	
	
	
	

}
