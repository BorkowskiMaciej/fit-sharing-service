package com.fitsharingapp.application.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.CreateReferenceNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.domain.news.NewsService;
import com.fitsharingapp.domain.news.repository.News;
import com.fitsharingapp.domain.news.repository.ReferenceNews;
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
    public News createNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId, @RequestBody
    CreateNewsRequest newsDTO) {
        return newsService.createNews(fsUserId, newsDTO);
    }

    @PostMapping("/reference")
    @ResponseStatus(CREATED)
    public ReferenceNews createReferenceNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId, @RequestBody
    CreateReferenceNewsRequest newsDTO) {
        return newsService.createReferenceNews(fsUserId, newsDTO);
    }

    @GetMapping("/published")
    public List<NewsResponse> getAllPublishedNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        return newsService.getAllPublishedNews(fsUserId);
    }

    @GetMapping("/received")
    public List<NewsResponse> getAllReceivedNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId) {
        return newsService.getAllReceivedNews(fsUserId);
    }

    @GetMapping("/received/{friendFsUserId}")
    public List<NewsResponse> getAllReceivedNewsFromFriend(
            @RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId,
            @PathVariable UUID friendFsUserId) {
        return newsService.getAllReceivedNewsFromFriend(fsUserId, friendFsUserId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteNews(@RequestHeader(value = FS_USER_ID_HEADER) UUID fsUserId, @PathVariable UUID id) {
        newsService.deleteNews(fsUserId, id);
    }

}
