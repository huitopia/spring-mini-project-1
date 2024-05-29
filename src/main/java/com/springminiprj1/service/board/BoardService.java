package com.springminiprj1.service.board;

import com.springminiprj1.domain.board.Board;
import com.springminiprj1.domain.board.BoardFile;
import com.springminiprj1.mapper.board.BoardMapper;
import com.springminiprj1.mapper.member.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper mapper;
    private final MemberMapper memberMapper;
    final S3Client s3Client;

    @Value("${aws.s3.bucket.name}")
    String bucketName;
    @Value("${image.src.prefix}")
    String imageSrcPrefix;

    public void add(
            Board board,
            MultipartFile[] files,
            Authentication authentication
    ) throws IOException {
        board.setMemberId(Integer.valueOf(authentication.getName()));
        // 게시물 저장
        mapper.add(board);

        if (files != null) {
            for (MultipartFile file : files) {
                // db에 해당 게시물의 파일 목록 저장
                mapper.addFileName(board.getId(), file.getOriginalFilename());

                // S3 파일 저장
                String key = STR."prj-1/board/\{board.getId()}/\{file.getOriginalFilename()}";
                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build();

                s3Client.putObject(objectRequest,
                        RequestBody.fromInputStream(
                                file.getInputStream(), file.getSize())
                );

                /* Disk 파일 저장
                // 부모 디렉토리 만들기
                String dir = STR."/Users/hya/Desktop/Study/mini-prj-1/\{board.getId()}";
                File dirFile = new File(dir);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                // 파일 경로
                String path = STR."/Users/hya/Desktop/Study/mini-prj-1/\{board.getId()}/\{file.getOriginalFilename()}";
                File destination = new File(path);
                file.transferTo(destination); */
            }
        }
    }

    public boolean validate(Board board) {
        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }
        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }
        return true;
    }

    public Map<String, Object> list(Integer page, String searchType, String searchKeyword) {
        Map pageInfo = new HashMap();
        Integer countAll = mapper.countAllwithSearch(searchType, searchKeyword);

        Integer offset = (page - 1) * 10;
        Integer lastPageNumber = (countAll - 1) / 10 + 1;
        Integer leftPageNumber = (page - 1) / 10 * 10 + 1;
        Integer rightPageNumber = leftPageNumber + 9;

        rightPageNumber = Math.min(rightPageNumber, lastPageNumber);

        Integer prevPageNumber = (leftPageNumber - 1);
        Integer nextPageNumber = (rightPageNumber + 1);

        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }

        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }

        pageInfo.put("currentPageNumber", page);
        pageInfo.put("lastPageNumber", lastPageNumber);
        pageInfo.put("leftPageNumber", leftPageNumber);
        pageInfo.put("rightPageNumber", rightPageNumber);

        return Map.of(
                "pageInfo", pageInfo,
                "boardList", mapper.selectAllPaging(offset, searchType, searchKeyword)
        );
    }

    public Board selectBoardById(Integer id) {
        Board board = mapper.selectBoardById(id);
        List<String> fileNames = mapper.selectFileNameByBoardId(id);
        List<BoardFile> files = fileNames.stream()
                /*-- Disk
                .map(name -> new BoardFile(name, STR."http://127.0.0.1:8888/\{id}/\{name}")) */
                //-- S3
                .map(name -> new BoardFile(name, STR."\{imageSrcPrefix}/board/\{id}/\{name}"))
                .toList();

        board.setFileList(files);

        return board;
    }

    public void deleteBoardById(Integer id) {
        // file 조회
        List<String> fileNames = mapper.selectFileNameByBoardId(id);
        // S3 File
        for (String fileName : fileNames) {
            String key = STR."prj-1/board/\{id}/\{fileName}";
            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(objectRequest);
        }

        /* Disk 파일
        String dir = STR."/Users/hya/Desktop/Study/mini-prj-1/\{id}/";
        for (String fileName : fileNames) {
            File file = new File(dir + fileName);
            file.delete();
        }
        File dirFile = new File(dir);
        if (dirFile.exists()) { dirFile.delete(); } */

        // board file
        mapper.deleteFileByBoardId(id);

        // board
        mapper.deleteBoardById(id);
    }

    public void updateBoard(Board board, List<String> removeFileList, MultipartFile[] addFileList) throws IOException {
        if (removeFileList != null && removeFileList.size() > 0) {
            for (String fileName : removeFileList) {
                // S3 File Delete
                String key = STR."prj-1/board/\{board.getId()}/\{fileName}";
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
                s3Client.deleteObject(objectRequest);

                /* disk file delete
                String path = STR."/Users/hya/Desktop/Study/mini-prj-1/\{board.getId()}/\{fileName}";
                File file = new File(path);
                file.delete(); */

                // db file records delete
                mapper.deleteFileByBoardIdAndName(board.getId(), fileName);
            }
        }

        if (addFileList != null && addFileList.length > 0) {
            List<String> fileNameList = mapper.selectFileNameByBoardId(board.getId());
            for (MultipartFile file : addFileList) {
                String fileName = file.getOriginalFilename();
                if (!fileNameList.contains(fileName)) {
                    // 새 파일이 기존에 없을 때만 db에 추가
                    mapper.addFileName(board.getId(), fileName);
                }
                // S3 덮어쓰기
                String key = STR."prj-1/board/\{board.getId()}/\{file.getOriginalFilename()}";
                PutObjectRequest objectRequest = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build();

                s3Client.putObject(objectRequest,
                        RequestBody.fromInputStream(
                                file.getInputStream(), file.getSize()));

                /* Disk  덮어쓰기
                File dir = new File(STR."/Users/hya/Desktop/Study/mini-prj-1/\{board.getId()}");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String path = STR."/Users/hya/Desktop/Study/mini-prj-1/\{board.getId()}/\{fileName}";
                File destination = new File(path);
                file.transferTo(destination); */
            }
        }

        mapper.updateBoard(board);
    }

    public boolean hasAccess(Integer id, Authentication authentication) {
        Board board = mapper.selectBoardById(id);

        return board.getMemberId()
                .equals(Integer.valueOf(authentication.getName()));
    }

    public void like(Map<String, Object> req, Authentication authentication) {
        Integer boardId = (Integer) req.get("boardId");
        Integer memberId = Integer.valueOf(authentication.getName());

        // 이미 했으면
        int count = mapper.deleteLikeByBoardIdAndMemberId(boardId, memberId);

        // 안했으면
        if (count == 1) {
            mapper.insertLikeByBoardIdAndMemberId(boardId, memberId);
        }
    }
}
