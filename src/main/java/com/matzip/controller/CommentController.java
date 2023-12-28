package com.matzip.controller;


import com.matzip.dto.CommentDto;
import com.matzip.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
//
//    @GetMapping("/all")
//    @ResponseBody
//    public ResponseEntity<?> getAllComments() {
//        List<CommentDto> allComments = commentService.findAll();
//        return new ResponseEntity<>(allComments, HttpStatus.OK);
//    }

    // 댓글 작성 또는 대댓글 작성durkl
    //commentDto를 받아서 commentService.save(commentDto)를 호출
    // 댓글을 저장하고, 저장된 댓글을 다시 조회하여 반환.
    // 반환된 댓글 정보는 클라이언트에게 JSON 형태로 전달
    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<?> save(@RequestBody CommentDto commentDto) {
        Long saveResult = commentService.save(commentDto);
        if (saveResult != null) {
            CommentDto savedComment = commentService.findById(saveResult);
            return new ResponseEntity<>(savedComment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("게시글이 존재하지 않습니다.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/saveReply/{parentId}")
    public ResponseEntity<?> saveReply(@RequestBody CommentDto commentDto, @PathVariable Long parentId) {
        System.out.println("saveReply parentId==================================: " + parentId);
        System.out.println("saveReply parentId: " + parentId);
        Long commentId = commentService.saveReply(commentDto); // 부모 댓글 ID를 가진 대댓글을 저장
        System.out.println("saveReply commentId: " + commentId);
        System.out.println("saveReply parentId: " + parentId);
        System.out.println("saveReply commentDto: " + commentDto);
        if (commentId != null) {
            CommentDto savedReply = commentService.findById(commentId);
            System.out.println("saveReply : " + savedReply);
            return new ResponseEntity<>(savedReply, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("부모 댓글을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    //부모 댓글의 ID와 연결된 자식 댓글들을 가져오고 있으며,
    // 이를 해당 댓글에 설정하여 반환하는 것
    // 이 부분은 댓글 트리 형태로 데이터를 가져온다
    @GetMapping("/{commentId}")
    public ResponseEntity<?> getComment(@PathVariable("commentId") Long commentId) {
        // 주어진 commentId를 사용하여 댓글 정보를 조회.
        CommentDto commentDto = commentService.findById(commentId, null); // boardId를 null로 전달
        //받은 commentId를 출력
        System.out.println("Received commentId: " + commentId);
        // 조회된 CommentDto가 null이 아닌 경우
        if (commentDto != null) {
            // 받은 commentId를 다시 출력합니다.
            System.out.println("Received commentId: " + commentId);
            // 부모 댓글의 ID를 가져옵니다.
            Long parentId = commentDto.getParentId();
            // 부모 댓글의 ID와 연결된 자식 댓글들을 가져옴.
            List<CommentDto> children = commentService.findByBoardId(commentDto.getBoardId(), parentId);
            // CommentDto에 자식 댓글들을 설정합니다.
            commentDto.setChildren(children);
            // ResponseEntity로 성공 응답을 반환(부모 댓글 ID를 포함)
            return new ResponseEntity<>(Collections.singletonMap("parentId", parentId), HttpStatus.OK);
        } else {
            // CommentDto가 null인 경우, 실패 응답을 반환합니다.
            return new ResponseEntity<>("댓글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

// 리뷰 화면
    @GetMapping("/edit/{commentId}")
    @ResponseBody
    public ResponseEntity<?> editComment(@PathVariable Long commentId, Model model) {
        CommentDto commentDto = commentService.findById(commentId);
        model.addAttribute("comment", commentDto);

        Long boardId = commentDto.getBoardId();
        List<CommentDto> comments = commentService.findByBoardId(boardId);
        model.addAttribute("comments", comments);

        return new ResponseEntity<>(comments, HttpStatus.OK); // JSON 형태로 댓글 리스트 반환
    }

    // 리뷰 수정
    @PostMapping("/update/{id}")
    public String updateComment(@PathVariable Long id, @ModelAttribute CommentDto commentDto, RedirectAttributes redirectAttributes) {
        try {
            CommentDto updatedComment = commentService.update(id, commentDto, commentDto.getParentId());
            redirectAttributes.addFlashAttribute("successMessage", "Comment updated successfully");
            return "redirect:/board/" + updatedComment.getBoardId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Comment could not be updated");
            return "redirect:/comment/edit/" + id;
        }
    }


    // 리뷰 삭제
    @GetMapping("/delete/{id}")
    public String deleteMember(@PathVariable Long id) {
        // 댓글을 삭제하고 해당 상품 페이지로 리다이렉트합니다.
        Long boardId = commentService.deleteComment(id);
        return "redirect:/board/" + boardId;
    }
}