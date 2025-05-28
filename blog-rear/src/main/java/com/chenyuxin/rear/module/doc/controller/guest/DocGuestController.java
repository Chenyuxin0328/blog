package com.chenyuxin.rear.module.doc.controller.guest;

import com.chenyuxin.rear.common.context.UserContext;
import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.module.doc.model.vo.GetRecentDocVo;
import com.chenyuxin.rear.module.doc.model.vo.PageDocVo;
import com.chenyuxin.rear.module.doc.model.vo.SelectDocCategoryVo;
import com.chenyuxin.rear.module.doc.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/doc")
public class DocGuestController {
    @Autowired
    private DocService docService;
    @GetMapping("/page")
    public Result<PageResult<PageDocVo>> pageDoc(@RequestParam("pageNum")Integer pageNum,
                                           @RequestParam("pageSize")Integer pageSize,
                                           @RequestParam(value = "search",required = false)String search,
                                           @RequestParam(value = "categoryId",required = false)Long categoryId){

        Integer viewScope = UserContext.getViewScope();
        PageResult<PageDocVo> pageDocVoPageResult = docService.pageDoc(pageNum, pageSize, search, categoryId, viewScope);
        return Result.success(pageDocVoPageResult);
    }
    // 查询技术文档分类
    @GetMapping("/category")
    public Result<List<SelectDocCategoryVo>> selectDocCategory(){
        List<SelectDocCategoryVo> selectDocCategoryVos = docService.selectDocCategory();
        return Result.success(selectDocCategoryVos);
    }
    // 查询近期文章
    @GetMapping("/recently")
    public Result<List<GetRecentDocVo>> getRecentDoc(){
        Integer viewScope = UserContext.getViewScope();
        List<GetRecentDocVo> getRecentDocVos = docService.getRecentDoc(viewScope);
        return Result.success(getRecentDocVos);
    }
}
