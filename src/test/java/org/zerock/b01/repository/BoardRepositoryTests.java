package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.BoardImage;
import org.zerock.b01.dto.board.BoardListAllDTO;
import org.zerock.b01.dto.board.BoardListReplyCountDTO;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Test
    void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..." + i)
                    .content("content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            final Board result = boardRepository.save(board);
            log.info("BNO: " + result.getBno());
        });
    }

    @Test
    void testSelect() {
        Long bno = 100L;
        final Optional<Board> result = boardRepository.findById(bno);

        final Board board = result.orElseThrow();

        log.info(board);
    }

    @Test
    void testUpdate() {
        Long bno = 100L;
        final Board board = boardRepository.findById(bno).orElseThrow();

        board.change("update..title 100", "update content 100");

        boardRepository.save(board);
    }

    @Test
    void testPaging() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        final Page<Board> result = boardRepository.findAll(pageable);

        log.info(result);
    }

    @Test
    void testSearch() {
        final Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());
        boardRepository.search1(pageable);
    }

    @Test
    void testSearchAll() {
        String[] types = {"t", "c", "w"};

        String keyword = "1";

        final Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        final Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        log.info(result.getTotalPages());
        log.info(result.getSize());
        log.info(result.getNumber());
        log.info(result.hasPrevious() + ": " + result.hasNext());
        result.getContent().forEach(log::info);
    }

    @Test
    void testSearchReplyCount() {
        String[] types = {"t", "c", "w"};
        String keyword = "1";

        final Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        final Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        // total page
        log.info(result.getTotalPages());

        // page size
        log.info(result.getSize());

        // pageNumber
        log.info(result.getNumber());

        // prev next
        log.info("result.hasPrevious() :" + result.hasPrevious());

        result.getContent().forEach(log::info);
    }

    @Test
    void testInsertWithImages() {
        final Board board = Board.builder()
                .title("Image Test")
                .content("첨부 파일 테스트")
                .writer("tester")
                .build();

        for (int i = 0; i < 3; i++) {
            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    void testReadWithImages() {
        final Board result = boardRepository.findByIdWithImages(1L).orElseThrow();

        log.info(result);
        log.info("-------------------------");
        for (BoardImage boardImage : result.getImageSet()) {
            log.info(boardImage);
        }
    }

    @Transactional
    @Commit
    @Test
    void testModifyImages() {
        final Board board = boardRepository.findByIdWithImages(1L).orElseThrow();

        board.clearImage();

        for (int i = 0; i < 2; i++) {
            board.addImage(UUID.randomUUID().toString(), "updateFile" + i + ".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    @Transactional
    @Commit
    void testRemovalAll() {
        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);
    }

    @Test
    void testInsertAll() {
        for (int i = 1; i <= 100; i++) {
            final Board board = Board.builder()
                    .title("Title.." + i)
                    .content("Contnet.." + i)
                    .writer("Writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++) {
                if (i % 5 == 0) {
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i + "file" + j + ".jpg");
            }
            boardRepository.save(board);
        }
    }

    @Transactional
    @Test
    void testSearchImageReplyCount() {
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

//        boardRepository.searchWithAll(null, null, pageable);

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null, null, pageable);
        log.info("---------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));
    }
}
