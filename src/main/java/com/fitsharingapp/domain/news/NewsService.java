package com.fitsharingapp.domain.news;

import com.fitsharingapp.application.news.dto.CreateNewsRequest;
import com.fitsharingapp.application.news.dto.NewsResponse;
import com.fitsharingapp.common.ErrorCode;
import com.fitsharingapp.common.ServiceException;
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

    public News createNews(UUID fsUserId, CreateNewsRequest newsDTO) {
        userService.validateUser(newsDTO.receiverFsUserId(), RECEIVER_NOT_FOUND);
//        relationshipService.validateRelationship(fsUserId, newsDTO.receiverFsUserId());
        return newsRepository.save(newsMapper.toEntity(newsDTO, fsUserId));
    }

    public List<NewsResponse> getAllPublishedNews(UUID fsUserId) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        return newsRepository.findAllByPublisherFsUserId(fsUserId, sort)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUserNameById(news.getPublisherFsUserId()),
                        userService.getUserNameById(news.getReceiverFsUserId()))
                    )
                .toList();
    }

    public List<NewsResponse> getAllReceivedNews(UUID fsUserId) {
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        return newsRepository.findAllByReceiverFsUserId(fsUserId, sort)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUserNameById(news.getPublisherFsUserId()),
                        userService.getUserNameById(news.getReceiverFsUserId()))
                    )
                .toList();
    }

    public void deleteNews(UUID fsUserId, UUID id) {
        newsRepository.findById(id)
                .ifPresent(news -> {
                    if (!news.getPublisherFsUserId().equals(fsUserId)) {
                        throw new ServiceException(ErrorCode.NEWS_IS_NOT_PUBLISHED_BY_USER);

                    }
                    newsRepository.delete(news);
                });
    }

    public void deleteAllNews(UUID fsUserId) {
        newsRepository.deleteAllByPublisherFsUserId(fsUserId);
    }

    public void deleteNewsForPublisherAndReceiver(UUID sender, UUID recipient) {
        newsRepository.deleteByPublisherFsUserIdAndReceiverFsUserId(sender, recipient);
        newsRepository.deleteByPublisherFsUserIdAndReceiverFsUserId(recipient, sender);
    }

    public List<NewsResponse> getAllReceivedNewsFromFriend(UUID fsUserId, UUID friendFsUserId) {
        return newsRepository.findAllByPublisherFsUserIdAndReceiverFsUserId(friendFsUserId, fsUserId)
                .stream()
                .map(news -> newsMapper.toResponse(
                        news,
                        userService.getUserNameById(news.getPublisherFsUserId()),
                        userService.getUserNameById(news.getReceiverFsUserId()))
                    )
                .toList();
    }

}
