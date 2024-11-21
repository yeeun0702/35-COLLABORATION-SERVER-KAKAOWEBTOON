package org.sopt.kakao.service;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.sopt.kakao.common.dto.ErrorMessage;
import org.sopt.kakao.common.exception.AppException;
import org.sopt.kakao.domain.Day;
import org.sopt.kakao.domain.Thumbnail;
import org.sopt.kakao.domain.Webtoon;
import org.sopt.kakao.repository.ThumbnailRepository;
import org.sopt.kakao.repository.WebtoonRepository;
import org.sopt.kakao.service.dto.WebtoonDayListResponse;
import org.sopt.kakao.service.dto.WebtoonDayResponse;
import org.sopt.kakao.service.dto.WebtoonListResponse;
import org.sopt.kakao.service.dto.WebtoonSearchResponse;
import org.springframework.stereotype.Service;

import static org.sopt.kakao.common.dto.ErrorMessage.THUMNAIL_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class WebtoonService {

    private final WebtoonRepository webtoonRepository;
    private final ThumbnailRepository thumbnailRepository;

    public WebtoonListResponse search(final String title) {
        List<Webtoon> findWebtoons = webtoonRepository.findAllByTitle(title);
        List<WebtoonSearchResponse> webtoons = findWebtoons.stream()
                .map(WebtoonSearchResponse::of)
                .toList();
        return new WebtoonListResponse(webtoons);
    }


    public WebtoonDayListResponse findWebtoonsByDay(final String dayString) {
        Day day = Day.convertToDay(dayString);
        List<WebtoonDayResponse> webtoons = webtoonRepository.findByDay(day).stream()
                .map(webtoon -> WebtoonDayResponse.of(webtoon, findThumbnail(webtoon)))
                .toList();
        return new WebtoonDayListResponse(webtoons);
    }


    private String findThumbnail(final Webtoon webtoon) {
        List<Thumbnail> thumbnail = thumbnailRepository.findByWebtoon(webtoon);
        return thumbnail.stream()
                .map(t -> t.getImage())
                .findFirst()
                .orElseThrow(() -> new AppException(THUMNAIL_NOT_FOUND));
    }

}
