package com.evenly.took.feature.card.application;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.evenly.took.feature.card.client.dto.CrawledDto;
import com.evenly.took.global.exception.TookException;
import com.evenly.took.global.service.ServiceTest;

@Disabled
class LinkExtractorTest extends ServiceTest {

	@Autowired
	LinkExtractor linkExtractor;

	@Test
	void 네이버블로그_메인페이지_링크를_크롤링한다() {
		// given
		String link = "https://blog.naver.com/gassembly";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 네이버블로그_상세페이지_링크를_크롤링한다() {
		// given
		String link = "https://blog.naver.com/gassembly/223794644692";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 브런치_메인페이지_링크를_크롤링한다() {
		// given
		String link = "https://brunch.co.kr/@watergru";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 브런치_상세페이지_링크를_크롤링한다() {
		// given
		String link = "https://brunch.co.kr/@watergru/12";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 비핸스_메인페이지_링크를_크롤링하면_예외가_발생한다() {
		// given
		String link = "https://www.behance.net/";

		// when, then
		assertThatThrownBy(() -> linkExtractor.extractLink(link))
			.isInstanceOf(TookException.class)
			.hasMessage("크롤링에 실패하였습니다.");
	}

	@Test
	void 비핸스_상세페이지_링크를_크롤링한다() {
		// given
		String link = "https://www.behance.net/gallery/191497631/praise-up-SNS";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 미디움_메인페이지_링크를_크롤링한다() {
		// given
		String link = "https://medium.com/@navercloudplatform";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 미디움_상세페이지_링크를_크롤링한다() {
		// given
		String link = "https://medium.com/naver-cloud-platform/%EA%B8%B0%EC%88%A0%EB%B8%94%EB%A1%9C-3306ae7899c3";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 깃허브_링크를_크롤링한다() {
		// given
		String link = "https://github.com/depromeet/Took-BE";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 플레이스토어_링크를_크롤링한다() {
		// given
		String link = "https://play.google.com/store/apps/details?id=com.zzang.chongdae";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 애플스토어_링크를_크롤링한다() {
		// given
		String link = "https://apps.apple.com/kr/app/%EC%B9%B4%EC%B9%B4%EC%98%A4%ED%86%A1/id362057947";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 티스토리_메인페이지_링크를_크롤링한다() {
		// given
		String link = "https://helenason.tistory.com";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 티스토리_상세페이지_링크를_크롤링한다() {
		// given
		String link = "https://helenason.tistory.com/26";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 벨로그_메인페이지_링크를_크롤링한다() {
		// given
		String link = "https://velog.io/@helenason/posts";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}

	@Test
	void 벨로그_상세페이지_링크를_크롤링한다() {
		// given
		String link = "https://velog.io/@helenason/dreamhack-wargame-csrf-2";

		// when
		CrawledDto crawledDto = linkExtractor.extractLink(link);

		// then
		System.out.println(crawledDto);
	}
}
