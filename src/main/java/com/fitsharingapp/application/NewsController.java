package com.fitsharingapp.application;

import com.fitsharingapp.domain.news.NewsService;
import com.fitsharingapp.domain.news.dto.CreateNewsDTO;
import com.fitsharingapp.domain.news.repository.News;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.Constants.FS_USER_ID_HEADER;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    @ResponseStatus(CREATED)
    public News createNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId, @RequestBody CreateNewsDTO newsDTO) {
        return newsService.createNews(fsUserId, newsDTO);
    }

    @GetMapping("/published")
    public List<News> getAllPublishedNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        return newsService.getAllPublishedNews(fsUserId);
    }

    @GetMapping("/received")
    public List<News> getAllReceivedNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        return newsService.getAllReceivedNews(fsUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId, @PathVariable String id) {
        newsService.deleteNews(fsUserId, id);
    }

}
