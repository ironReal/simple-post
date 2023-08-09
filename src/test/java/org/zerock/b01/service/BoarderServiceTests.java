package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.*;
import org.zerock.b01.dto.board.BoardDTO;
import org.zerock.b01.dto.board.BoardImageDTO;
import org.zerock.b01.dto.board.BoardListAllDTO;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Log4j2
@SpringBootTest
class BoarderServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    void testRegister() {
        log.info(boardService.getClass().getName());

        final BoardDTO boardDTO = BoardDTO.builder()
                .title("Sample title...")
                .content("Sample content...")
                .writer("user))")
                .build();

        final Long bno = boardService.register(boardDTO);

        log.info(bno);
    }

    @Test
    void testModify() {
        final BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("Updated...101")
                .content("Updated Content...101")
                .build();

        boardDTO.setFileNames(List.of(UUID.randomUUID() + "_zzz.jpg"));

        boardService.modify(boardDTO);
    }

    @Test
    void testList() {
        final PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();
        final PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);

        log.info(responseDTO);
    }

    @Test
    void testRegisterWithImages() {

        log.info(boardService.getClass().getName());

        final BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID() + "_aaa.jpg",
                        UUID.randomUUID() + "_bbb.jpg",
                        UUID.randomUUID() + "_ccc.jpg"
                ));

        final Long bno = boardService.register(boardDTO);

        log.info(bno);
    }

    @Test
    void testReadAll() {
        Long bno = 101L;

        final BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        for (String fileName : boardDTO.getFileNames()) {
            log.info(fileName);
        }
    }

    @Test
    void testRemoveAll() {
        Long bno = 1L;

        boardService.remove(bno);

    }

    @Test
    void testListWithAll() {

        final PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        final PageResponseDTO<BoardListAllDTO> responseDTO = boardService.listWithAll(pageRequestDTO);

        final List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {
            log.info(boardListAllDTO.getBno() + ": " + boardListAllDTO.getTitle());

            if (boardListAllDTO.getBoardImages() != null) {
                for (BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info(boardImage);
                }
            }
            log.info("----------------------");
        });
    }
}
