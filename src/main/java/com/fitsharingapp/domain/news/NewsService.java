package com.fitsharingapp.domain.news;

import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
import com.fitsharingapp.domain.news.dto.CreateNewsDTO;
import com.fitsharingapp.domain.news.repository.ActivityType;
import com.fitsharingapp.domain.news.repository.News;
import com.fitsharingapp.domain.news.repository.NewsRepository;
import com.fitsharingapp.domain.relationship.RelationshipService;
import com.fitsharingapp.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.fitsharingapp.common.ErrorCode.RECEIVER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsMapper newsMapper;
    private final UserService userService;
    private final RelationshipService relationshipService;

    public News createNews(UUID fsUserId, CreateNewsDTO newsDTO) {
        userService.validateUser(newsDTO.receiverFsUserId(), RECEIVER_NOT_FOUND);
        relationshipService.validateRelationship(fsUserId, newsDTO.receiverFsUserId());
        ActivityType activityType = ActivityType.validateAndGet(newsDTO.activityType());
        return newsRepository.save(newsMapper.toEntity(newsDTO, fsUserId, activityType));
    }

    public List<News> getAllPublishedNews(UUID fsUserId) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        return newsRepository.findAllByPublisherFsUserId(fsUserId, sort);
    }

    public List<News> getAllReceivedNews(UUID fsUserId) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        return newsRepository.findAllByReceiverFsUserId(fsUserId, sort);
    }

    public void deleteNews(UUID fsUserId, String id) {
        newsRepository.findById(id)
                .ifPresent(news -> {
                    if (!news.getPublisherFsUserId().equals(fsUserId)) {
                        throw new ServiceException(ErrorCode.NEWS_IS_NOT_PUBLISHED_BY_USER);

                    }
                    newsRepository.deleteById(id);
                });
    }

    public void deleteAllNews(UUID fsUserId) {
        newsRepository.deleteAllByPublisherFsUserId(fsUserId);
    }

}
