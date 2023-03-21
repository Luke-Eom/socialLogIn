package com.example.demo.service;

import com.example.demo.dto.ReplySaveRequestDto;
import com.example.demo.entity.Board;
import com.example.demo.entity.Reply;
import com.example.demo.entity.User;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.ReplyRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ReplyRepository replyRepo;

    @Transactional
    public void createWriting(Board board, User user) { //title, content
        System.out.println("Board 정보 : title, content = " + board);
        board.setCount(0);
        board.setUser(user);
        boardRepo.save(board);
    }

    @Transactional(readOnly=true)
    public Page<Board> readAllWriting(Pageable pageable){
        return boardRepo.findAll(pageable);
    }

    @Transactional(readOnly=true)
    public Board readWriting(int id) {
        return boardRepo.findById(id)
                .orElseThrow(()->{
                    return new IllegalArgumentException("글상세보기 실패 : 아이디를 찾을 수 없습니다.");
                });
    }

    @Transactional
    public void deleteWriting(int id) {
        boardRepo.deleteById(id);
    }

    @Transactional
    public void updateWriting(int id, Board requestBoard) {

        Board board = boardRepo.findById(id)
                .orElseThrow(()->{
                    return new IllegalArgumentException("글 수정하기 실패: 아이디를 찾을 수 없습니다.");
                });

        board.setTitle(requestBoard.getTitle());
        board.setContent(requestBoard.getContent());

    }

    // Reply
    @Transactional
    public void createReply(ReplySaveRequestDto replySaveRequestDto) { //title, content

        Board board = boardRepo.findById(replySaveRequestDto.getBoardId())
                .orElseThrow(()->{
                    return new IllegalArgumentException("댓글 쓰기 실패: 게시글 아이디를 찾을 수 없습니다.");
                });//영속화 완료
        User user = userRepo.findById(replySaveRequestDto.getUserId())
                .orElseThrow(()->{
                    return new IllegalArgumentException("댓글 쓰기 실패: 해당 아이디를 찾을 수 없습니다.");
                });//영속화 완료

        Reply reply = Reply.builder()
                .user(user)
                .board(board)
                .content(replySaveRequestDto.getContent())
                .build();

        replyRepo.save(reply);
    }

    @Transactional
    public void deleteReply(int replyId) {
        replyRepo.deleteById(replyId);
    }


}
