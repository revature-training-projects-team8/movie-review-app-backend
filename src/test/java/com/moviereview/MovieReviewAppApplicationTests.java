package com.moviereview;

import com.moviereview.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestSecurityConfig.class)
class MovieReviewAppApplicationTests {

	@Test
	void contextLoads() {
	}

}
