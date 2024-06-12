# Kotlin Backend Engineer 과제

## 본 프로젝트는 아래와 같은 구성으로 작성 되었습니다.

아래와 같은 컴포넌트로 구성하였으며 각 요소에 대해 설명합니다.
큰 틀은 clean architecture 와 흡사하나, 엄격하게 지키지는 아니하였습니다.

### 사용된 Framework 및 library
- spring boot
- spring data jpa (with h2 db)
- swagger-ui
- kotest (코틀린에서 조금 더 자연스런 방법으로 테스트를 정의 할 수 있도록 도와줍니다)

### domain 
- 도메인 규칙, 혹은 데이터의 형태를 담고 있는 class 입니다.
- 본래 엄격하게 clean architecture 를 구성할 경우, domain 영역의 클래스는 
  repository 나 framework 등의 기술 세부사항에 의존성을 갖고 있으면 아니 되나,
  본 프로젝트에서는 Persist 를 위한 의존성은 허용하기로 하였습니다.

### application 
- useCases 비즈니스 흐름의 단위입니다.
- service useCase 의 실제적인 구현체이며, 비즈니스 로직을 담고 있습니다.
- application 내에 있는 컴포넌트들은 의존성의 방향이 외부로 향하지 않으며, repository 등의
  외부 layer 로 연동이 필요한 인터페이스와 DI 를 사용해 의존성을 역전시켜 사용합니다.

### controller
- application 의 외부 interface adapter 입니다.
- 입력값 validation 과 API 사양 등의 정의를 담고 있습니다.
- application layer 에서 넘겨받은 domain 객체를 외부로 바로 노출하지 않고, 외부와 계약된 스펙으로 매핑하여 응답합니다.

### repository
- application 의 out port 를 위한 interface 입니다. 
- 보통, repository 뿐만 아닌 외부영역과의 연결을 위한 interface 들을 총칭하여 
  다른 이름으로 packaging 하기도 하나, 본 프로젝트에서는 외부 인프라 영역이 
  DB 만 존재하므로 repository 로 명명 하였습니다.
- 본래 쿼리를 위해 query dsl 을 적용 하였으나, querydsl 의 서브쿼리 수행의 한계로 로직
  복잡도가 증가하는 경향을 보여 @Query annotation 을 사용한 jpql 로 변경하였습니다.
- 집계를 위한 쿼리가 자주 사용 되는 경우, 성능과 관리 효율성을 위해 DB 또는 테이블을 별도로 분리하여
  비동기 또는 배치 구조로 집계 데이터를 별도 적재하는 것이 일반적이나, 작은 프로젝트의 목적 상 쿼리로 해결합니다
  성능 문제가 있는 경우 1차적으로는 자주 변하지 않는 데이터의 특성을 감안하여 캐시를 적용할 수 있을 것으로 보이고,
  그 이후 튜닝이 필요한 경우 집계 DB 의 분리를 고려합니다.

### exception
- 외부에 노출하기 위한 에러를 정의합니다. http status code 와 간단한 메세지를 담습니다.

### config
- application 을 구동하기 위한 framework 설정 등이 위치합니다.

### extension
- 그 외 확장 함수 등이 위치합니다.

```

### API 목록

API 스펙은 간략하게 앱 실행 후 swagger-ui 를 통해서도 확인할 수 있습니다.

http://localhost:8080/swagger-ui/index.html

#### CoordinateController
- 카테고리별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
  - GET /coordinates/cheapest
- 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품 가격, 총액을 조회하는 API
  - GET /coordinates/cheapest-by-brand

#### ProductController
- 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
  - GET /products/cheapest-and-most-expensive?category_name=상의
- 상품 조회 (id)
  - GET /products/{id}
- 상품 추가
  - POST /Products
- 상품 수정
  - PUT /Products/{id}
- 상품 삭제
  - DELETE /Products/{id}

#### BrandController
- 브랜드 조회 (id)
  - GET /brands/{id}
- 브랜드 조회 (브랜드명)
  - GET /brands?name=A
- 브랜드 추가
  - POST /brands
- 브랜드 수정
  - PUT /brands
- 브랜드 삭제
  - DELETE /brands/{id}

### 앱 실행 방법

```shell
./gradlew bootRun
```


### 테스트 구동 방법

```shell
./gradlew test
```
