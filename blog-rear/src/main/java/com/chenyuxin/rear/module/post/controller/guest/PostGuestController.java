package com.chenyuxin.rear.module.post.controller.guest;

import com.chenyuxin.rear.common.context.UserContext;
import com.chenyuxin.rear.common.model.PageResult;
import com.chenyuxin.rear.common.model.Result;
import com.chenyuxin.rear.module.post.model.dto.LikePostDto;
import com.chenyuxin.rear.module.post.model.dto.MakeCommentDto;
import com.chenyuxin.rear.module.post.model.vo.PagePostVo;
import com.chenyuxin.rear.module.post.service.PostCommentService;
import com.chenyuxin.rear.module.post.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostGuestController {
    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private PostService postService;
    // 评论帖子
    @PostMapping("/comment")
    public Result<Void> makeComment(@RequestBody MakeCommentDto makeCommentDto){
        Long userId = UserContext.getUserId();
        postCommentService.makeComment(userId,makeCommentDto);
        return Result.success();
    }
    // 分页查询帖子
    @GetMapping("/page")
    public Result<PageResult<PagePostVo>> pagePost(@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        Integer viewScope = UserContext.getViewScope();
        PageResult<PagePostVo> pageResult = postService.pagePost(viewScope,pageNum,pageSize);
        return Result.success(pageResult);
    }
    // 点赞帖子
    @PostMapping("/like")
    public Result<Void> likePost(@RequestBody LikePostDto likePostDto){
        postService.likePost(likePostDto);
        return Result.success();
    }

}
