package com.mjbmall.elasticsearch.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjbmall.elasticsearch.core.controller.BaseController;
import com.mjbmall.elasticsearch.core.entity.HitEntity;
import com.mjbmall.elasticsearch.core.until.DzResponse;
import com.mjbmall.elasticsearch.domain.Product;
import com.mjbmall.elasticsearch.domain.vo.QueryRequest;
import com.mjbmall.elasticsearch.repositories.ProductRepository;

/**
 * ProductController
 * @author Gerry_Pang
 */
@Controller
@RequestMapping("/search/product")
public class ProductController extends BaseController {

	@Autowired
	private ProductRepository productRepository;
	
    /**
     * List查询
     * @param query
     * @param request
     * @return
     */
    @RequestMapping(value = "list", method = {RequestMethod.GET} )
    @ResponseBody
    public List<HitEntity<Product>> search(@RequestParam(value="query") String query) {
    	String deCodeQuery = "";
		try {
			deCodeQuery = URLDecoder.decode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.toString());
		}
    	List<HitEntity<Product>> returnList = productRepository.findPageByOneParam(deCodeQuery);
    	
    	return returnList;
    }
    
    /**
     * Page分页查询 GET
     * @param query
     * @param page
     * @param pageSize 
     * @return Page<HitEntity<Product>>
     */
    @RequestMapping(value = "page", method = RequestMethod.GET )
    @ResponseBody
    public Page<HitEntity<Product>> getPage(
    		@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "page", required = false, defaultValue = "0") int page,
			@RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
    	String deCodeQuery = "";
    	Page<HitEntity<Product>> returnPage = null;
    	
       	try {
       		deCodeQuery = URLDecoder.decode(query, "UTF-8");
       		Pageable pageable = new PageRequest(page, pageSize);
       		logger.info("/search/page -> pageSize = {}, pageNumber = {}", pageable.getPageNumber(), pageable.getPageSize());
       		returnPage = productRepository.findPageByOneParam(deCodeQuery, pageable);
    	} catch (UnsupportedEncodingException e) {
    		logger.error(e.toString());
    	} catch (Exception e) {
    		logger.error(e.toString());
		}
		return returnPage;
    }
    
    /**
     * Page分页查询 POST
     * @param QueryRequest
     * @return DzResponse
     */
    @RequestMapping(value = "page", method = RequestMethod.POST )
    @ResponseBody
    public DzResponse postPage(@RequestBody QueryRequest queryReq) {
    	DzResponse result = null;
    	String deCodeQuery = "";
    	Page<HitEntity<Product>> returnPage = null;
    	try {
    		deCodeQuery = URLDecoder.decode(queryReq.getQuery(), "UTF-8");
	    	Pageable pageable = new PageRequest(queryReq.getPage(), queryReq.getPageSize());
	    	logger.info("/search/page -> pageSize = {}, pageNumber = {}", pageable.getPageNumber(), pageable.getPageSize());
	    	// 根据请求参数判断是否走排序
	    	if(StringUtils.isBlank(queryReq.getSortField()) && StringUtils.isBlank(queryReq.getSortOrder())){
	    		returnPage = productRepository.findPageByOneParam(deCodeQuery, pageable);
	    	}else{
	    		returnPage = productRepository.findPageByOneParam(deCodeQuery, pageable, queryReq.getSortField(), queryReq.getSortOrder());
	    	}
	    	result = DzResponse.SUCCESS(returnPage);
    	} catch (UnsupportedEncodingException e) {
    		logger.error(e.toString());
    		result = DzResponse.FAILURE(e.getMessage());
    	}catch (Exception e) {
    		logger.error(e.toString());
    		result = DzResponse.FAILURE(e.getMessage());
		}
    	return result;
    }
    
    public void toJson(Object obj){
        // write your code here
        ObjectMapper mapper = new ObjectMapper();
        try {
			String jsonInString = mapper.writeValueAsString(obj);
			logger.info(jsonInString);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }
}
