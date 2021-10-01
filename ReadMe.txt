- 1주차 강의
    
    ### 1. **Controller에서 보내주는 종류**
    
    ![Controller 와 HTTP Response 전달방식.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/537e0530-3c4d-4a55-bdda-12beec190821/Controller_와_HTTP_Response_전달방식.png)
    
    > @ResponseBody를 붙여주는 이유
    > 
    
    @Controller  + @ResponseBody = @RestController
    
    @ResponseBody는 response를  json형태로 보내주거나 받기 위해 붙이는 것인데
    
    맵핑마다 붙여주기 힘드니 컨트롤러를 레스트컨트롤러로 바꿔주는것
    
    ### 2. **Request 받은 다음의 흐름**
    
    ![컨트롤 뒤에서 하는일.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/929bcb44-a154-416b-9bb4-6c9a37052161/컨트롤_뒤에서_하는일.png)
    
    > DispatcherServlet --> Controller
    > 
    - API를 처리해 줄 Controller 를 찾아 요청을 전달
    - Handler mapping 에는 API path 와 Controller함수가 매칭되어 있다. (함수 이름을 마음대로 설정 가능했던 이유)
    - Controller 에서 요청하는 Request의 정보 ("Model") 전달
    
    > Contorller -->DispathcerServlet
    > 
    1. Controller가 Client 로부터 받은 API 요청의 처리
    2. "Model" 정보와 "View" 정보를 DispatcherServlet 으로 전달
    
    > DispatcherServlet --> Client
    > 
    1. ViewResolver 통해 View 에 Model 을 적용 (Thymleef 템플릿 엔진)
    2. View 를 Client 에게 응답으로 전달
    
    ### 3. **Annotation 별 샘플코드**
    
    ![request메세지.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/92200ceb-7809-4fb6-89c4-a0ef2d336d03/request메세지.png)
    
    Request 받는것
    
    Response 주는것
    
    ### 4. AllInOneController 의 문제점
    
    - 처음부터 끝까지 다 읽어야 코드 내용을 이해할 수 있다.
    - 현업에서는 코드 추가 혹은 변경 요청이 계속 생긴다.
    - 객체지향 프로그래밍이 되지 않는다.
    - 처리 과정을 분리 시키는 예
    
    ![객체지향 프로그래밍 예.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7e847ebf-ab67-4ad2-ab66-e00fb30dd52c/객체지향_프로그래밍_예.png)
    
    ![객체 지향 프로그래밍 예2.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e52fdcb9-b41a-4ddb-8802-6326a0c256a4/객체_지향_프로그래밍_예2.png)
    
     
    
    ### 5. DI(의존성 주입)
    
    > **강한결합 예제**
    > 
    1. Controller1이 Service1객체를 생성하여 사용
    
    ```java
    public class Controller1 {
    private final Service1 service1;
    public Controller1() {
    this.service1 = new Service1();
    }
    }
    ```
    
    1. Service1이 Repository1 객체를 생성하여 사용
    
    ```java
    public class Service1 {
    private final Repository1 repository1;
    public Service1() {
    this.repository1 = new Repository1();
    }
    }
    ```
    
    1. Repository1 객체 선언
    
    ```java
    public class Repository1 { ... }
    ```
    
    1. 만약 다음과 같이 변경된다면
    
        a.   Repository1 객체 생성 시 DB 접속 id, pw 를 받아서 DB 접속 시 사용
    
    - 생성자에 DB 접속 id, pw 를 추가
    
    ```java
    public class Repository1 {
    public Repository1(String id, String pw) {
    // DB 연결
    Connection connection = DriverManager.getConnection("jdbc:h2:mem:springcoredb", id, pw);
    }
    }
    ```
    
    > **강한결합의 문제점**
    > 
    - Controller 5 개가 각각 Service1을 생성하여 사용 중
    - Repository1 생성자 변경에 의해...
    
          ⇒ 모든 Controller와 모든 Service의 코드 변경이 필요 
    
    ![강한결합 문제점.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/323ecf09-385a-4c05-9303-45cee13d6982/강한결합_문제점.png)
    
    > **해결방법 (느슨한 결합)**
    > 
    - 각 객체에 대한 객체 생성은 딱 1번만
    - 생성된 객체를 모든 곳에서 재사용
    - 컨트롤러 부터 시작하는게 아닌 레파지토리 부터 생성해서 이어나간다
    1. Repository1 클래스 선언 및 객체 생성 → repository1
    
    ```java
    public class Repository1 { ... }
    // 객체 생성
    Repository1 repository1 = new Repository1();
    ```
    
    1. Service1 클래스 선언 및 객체 생성 (repository1 상용) → service1
    
    ```java
    Class Service1 {
    private final Repository1 repitory1;
    // repository1 객체 사용
    public Service1(Repository1 repository1) {
    ~~this.repository1 = new Repository1();~~
    this.repository1 = repository1;
    }
    }
    // 객체 생성
    Service1 service1 = new Service1(repository1);
    ```
    
    1. Controller1 선언 (service1 사용)
    
    ```java
    Class Controller1 {
    private final Service1 service1;
    // service1 객체 사용
    public Controller1(Service1 service1) {
    this.service1 = new Service1();
    this.service1 = service1;
    }
    }
    ```
    
    1. 다음과 같이 변경된다면,
    
       a.   Repository1 객체 생성 시 Db 접속 id, pw 를 받아서 DB 접속 시 사용
    
    - 생성자에 접속 id, pw 를 추가
    
    ```java
    public class Repository1 {
    public Repository1(String id, String pw) {
    // DB 연결
    Connection connection = DriverManager.getConnection("jdbc:h2:mem:springcoredb", id, pw);
    }
    }
    // 객체 생성
    String id = "sa";
    String pw = "";
    Repository1 repository1 = new Repository1(id, pw);
    ```
    
    > **개선결과**
    > 
    
    ⇒ Repository1 생성자 변경은 이제 누구에게도 영향을 끼치지 않음 
    
    ⇒ Service1 생성자가 변경되도 모든 Controller 변경 필요X
    
    결론적으로, **강한 결합** ⇒ **느슨한 결합**
    
    ![느슨한 결합.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/c54519cf-fa45-44c0-8dc5-ab12ca7d1182/느슨한_결합.png)
    
    > **제어의 역전**
    > 
    
    ![제어의 역전.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7d66e4d2-ff6d-443d-b180-58d509d44dac/제어의_역전.png)
    
    - 일반적: 사용자가 자신이 필요한 객체를 생성해서 사용
    - IoC (제어의 역전)
        - 용도에 맞게 필요한 객체를 그냥 가져다 사용
            - "**DI(Dependency Injection)**"혹은 한국말로 "의존성 주입"이라고 한다
        - 사용할 객체가 어떻게 만들어졌는지는 알 필요 없음
        - 실생활 예제) 가위의 용도별 사용 (객체는 소문자)
            - 음식을 자를때 필요한 가위는? → 부엌가위(생성되어 있는 객체 kitchenScissors)
            - 무늬를 내며 자를 때 필요한 가위는?  → 핑킹가위(생성되어 있는 객체 pinkingShears )
            - 정원의 나무를 다듬을 때 필요한 가위는? → 전지가위(생성되어 있는 객체 pruningShears)
            
    
    > **스프링 IoC 컨테이너 사용하기**
    > 
    - **빈 (Bean)**: 스프링이 관리하는 객체
    - **스프링 IoC 컨테이너**: **'빈'**을 모아둔 통
    
    - 스프링 '빈' 등록 방법
        1. @Component
            - 클래스 선언 위에 설정
            
            ```java
            @Component
            public class ProductService { ... }
            ```
            
            - 스프링 서버가 뜰때 스프링 IoC에 '빈' 저장
                - @Component 클래스에 대해서 스프링이 해주는 일
                
                ```java
                // 1. ProductService 객체 생성
                ProductService productService = new ProductService();
                // 2. 스프링 IoC 컨테이너에 빈 (productService) 저장
                // productService -> 스프링 IoC 컨테이너
                ```
                
                - 스프링 '빈' 이름: 클래스의 앞글자만 소문자로 변경
                    - public class ProductService → productService
            - '빈' 아이콘 확인 → 스프링 IoC 에서 관리할 '빈' 클래스라는 표시
            
            ![빈 표시.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/70aefdee-7b2c-4c57-bc5d-4edb1a1389d4/빈_표시.png)
            
            - @Component 적용 조건
                - @ComponentScan 에 설정해 준 packages 위치와 하위 packages 들
                
                ```java
                @Configuration
                @ComponentScan(basePackages = "com.sparta.springcore")
                class BeanConfig { ... }
                ```
                
                - @SpringBootApplication 에 의해 default 설정이 되어 있음
                    
                    com.sparta.springcore/SpringcoreApplication.java
                    
                
                ![스캔.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/e6f8df49-59a1-45ce-8ff9-a83255cd0422/스캔.png)
                
                - 테스트 코드
                
                ```java
                package com.sparta.abc;
                import org.springframework.stereotype.Component;
                @Component
                public class TestClass {
                }
                ```
                
        2. @Bean
            - 직접 객체를 생성하여 빈으로 등록 요청
            
            ```java
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            @Configuration
            public class BeanConfiguration {
            @Bean
            public ProductRepository productRepository() {
            String dbUrl = "jdbc:h2:mem:springcoredb";
            String dbId = "sa";
            String dbPassword = "";
            return new ProductRepository(dbUrl, dbId, dbPassword)
            }
            }
            ```
            
            - 스프링 서버가 뜰 때 스프링 IoC에 '빈' 저장
            
            ```java
            // 1. @Bean 설정된 함수 호출
            ProductRepository productRepository = beanConfiguration.productRepository();
            // 2. 스프링 IoC 컨테이너에 빈 (productRepository) 저장
            // productRepository -> 스프링 IoC 컨테이너
            ```
            
            스프링 '빈 ' 이름: @Bean 이 설정된 함수명
            
            - public ProductRepository productRepository() {..} → productRepository
            
    - 스프링 '빈' 사용 방법
        1. @Autowired
            - 멤버변수 선언 위에 @Autowired → 스프링에 의해 DI(의존성 주입) 됨
            
            ```java
            @Component
            public class ProductService {
            @Autowired
            private ProductRepository productRepository;
            // ...
            }
            ```
            
            - '빈'을 사용할 함수 위에 @Autowired  → 스프링에 의해 DI 됨
            
            ```java
            @Component
            public class ProductService {
            private final ProductRepository productRepository;
            @Autowired
            public ProductService(ProductRepository productRepository) {
            this.productRepository = productRepository;
            }
            // ...
            }
            ```
            
            - @Autowired 적용 조건
                - 스프링 IoC 컨테이너에 의해 관리되는 클래스에서만 가능
                
                ![관리되는객체.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/7930a7f0-6b31-4282-9e32-00b47142ebac/관리되는객체.png)
                
            - @Autowired 생략 조건
                - Spring 4.3 버젼 부터 @Autowired 생략가능
                - 생성자 선언이 1개 일 때만 생략 가능
                    - 파라미터가 다른 생성자들
                    
                    ```java
                    public class A {
                    @Autowired // 생략 불가
                    public A(B b) { ... }
                    @Autowired // 생략 불가
                    public A(B b, C c) { ... }
                    }
                    ```
                    
                
                - Lombok 의 @RequiredArgsConstructor 를 사용하면 다음과 같이 코딩 가능
                
                ```java
                @RequiredArgsConstructor // final로 선언된 멤버 변수를 자동으로 생성합니다.
                @RestController // JSON으로 데이터를 주고받음을 선언합니다.
                public class ProductController {
                private final ProductService productService;
                // 생략 가능
                // @Autowired
                // public ProductController(ProductService productService) {
                // this.productService = productService;
                // }
                }
                ```
                
            1. ApplicationContext
                - 스프링 IoC 컨테이너에서 빈을 수동으로 가져오는 방법
                
                ```java
                @Component
                public class ProductService {
                private final ProductRepository productRepository;
                @Autowired
                public ProductService(ApplicationContext context) {
                // 1.'빈' 이름으로 가져오기
                ProductRepository productRepository = (ProductRepository) context.getBean("productRepository");
                // 2.'빈' 클래스 형식으로 가져오기
                // ProductRepository productRepository = context.getBean(ProductRepository.class);
                this.productRepository = productRepository;
                }
                // ...
                }
                ```
                
    
    ### 6. 스프링 3계층 Annotation
    
    - 스프링 3계층 Annotation 은 모두 @Component
        1. @Controller, @RestController
        2. @Service
        3. @Repository
    
    ### 7. 스프링 프레임워크 재이해
    
    > **A key element of Spring** is infrastructural support at the application level: Spring focuses on the "plumbing" of enterprise applications so that teams can focus on application-level **business logic**, without unnecessary ties to specific deployment environments
    > 
    
    - Enterprise applications 개발 편의성 제공
        - 고객 대상 웹 서비스 ex) 구글, 네이버, 쿠팡 등
    - 스프링은 결국 기업용 애플리케이션의 요구사항 해결에 초점을 맞춘 프레임워크
    - 기업용 애플리케이션 특성
        1. 신뢰성이 중요 (ex. 병원에서 수술 시 환자 기록이 바뀐다면 )
        2. 서버의 안정성 유지 중요 (ex. 복권 실시간 추첨에 서버 다운 된다면)
        3. 데이터 관리가 중요
            - 막대한 양의 데이터 관리 필요
            - 여러 사용자가 동시 접속 시 데이터 일관성
            - → 대부분 DB (데이터베이스) 사용
    
    > **스프링의 핵심 요소**
    > 
    - 비즈니스 로직 (business logic)에 집중하게 해 준다.
    - 웹 서비스 다이어그램
    
    ![웹서비스 다이어그램.png](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/9538f15d-7b9b-41db-90ce-8c9e63269bdb/웹서비스_다이어그램.png)
    
    - 서버개발자들이 신경써야 할 부분이 너무 많음
        1. API: 클라이언트 ↔ 서버
        2. 비즈니스 로직 (@Service)
            1. 실제로 사용자의 **"요구사항이 처리"되는 부분**
        3. DB: 서버 ↔ DB
    - "기업의 요구사항"에만 집중하여 개발할 수 있도록
        - 반복되고, 실수가 많은 부분 → 스프링이 대신!