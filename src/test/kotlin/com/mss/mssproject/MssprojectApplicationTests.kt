package com.mss.mssproject

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    // 전체 테스트 동시 수행시 db initializing 관련 문제가 발생하여 임시조치
    properties = [
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=none"
    ]
)
class MssprojectApplicationTests {

    @Test
    fun contextLoads() {
    }
}
