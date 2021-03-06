package web.product.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import web.product.dao.PImgDAO;
import web.product.dao.impl.PImgJDBC;
import web.product.entity.ImgTFVO;
import web.product.entity.PImgVO;
import web.product.entity.SpuVO;
import web.product.service.PImgService;

	
@Service
public class PImgServiceImp implements PImgService {
	
	@Autowired
	 PImgDAO pImgDAO;

	public int addPics(int spuID, ImgTFVO imgs) {

		// 主照片
		byte[] mainImg;
		// 附照片
		List<PImgVO> imgList = new ArrayList<>();
		// 去除掉base64 前綴名
		
		mainImg = Base64.getDecoder().decode(imgs.getMainImg().split(",")[1]);
		// 1是主照片
		imgList.add(new PImgVO(null, spuID, mainImg, 1));
		for (String subImg : imgs.getSubImg()) {
			byte[] subPic = Base64.getDecoder().decode(subImg.split(",")[1]);
			imgList.add(new PImgVO(null, spuID, subPic, 0));
		}
		return pImgDAO.uploadPics(imgList);
	}

	@Override
	public PImgVO getPic(Integer spuID) {
	
		return pImgDAO.getPicByID(spuID);
	}

	@Override
	public List<PImgVO> getSpuPics(SpuVO spuVO) {
		
		return pImgDAO.getSpuPics(spuVO);
	}

}
