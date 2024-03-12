# Aucison

<div align="center">
<img width="329" alt="image" src="https://github.com/aucison/Aucison_Service/assets/68382824/938dbe61-0f0b-4f8d-8b43-6a8bc9d578b8">

[![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https://github.com/aucison/Aucison_Service&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false)](https://hits.seeyoufarm.com)

</div>

# Aucison Web Page v1.0.0
**연합 프로젝트** <br/>

## 🚀 배포 주소

> **개발 버전** : 
> **백엔드 서버** : 

## 🖥️ 프로젝트 소개
중고 경매 사이트입니다. <!--추가할 내용 의논-->
<br>

## 🕰️ 개발 기간
**2023-09-17 ~ 2024-02-13**

2023-09-17 ~ 2023-09-30 : 프로젝트 구체화 및 </br>
2023-10-01 ~ 2023-10-21 : MSA구조로 구현</br>
2023-11-01 ~ 2024-02-13 : 모놀로틱 구조로 전환 및 구현</br>
2024-02-14 ~ : 보완 및 업데이트 진행중 </br>

### 🧑‍🤝‍🧑 멤버구성
<!--깃허브 링크 달아주기, 담당 서비스 더 자세히 적으면 좋을듯-->
 - 팀장 : [김태현](https://github.com/hotcoa922) - 기획 및 백엔드 개발
 - 팀원 : [오승미](https://github.com/seungmio) - 백엔드 개발
 - 팀원 : 황동현 - 프론트 개발
 - 팀원 : 손민재 - 프론트 개발
 - 팀원 : 이정인 - 디자인

---
<!--추가할 내용 의논-->
## Stacks 🐈

### 개발 환경
![Intellij IDEA](https://img.shields.io/badge/intellijidea-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![Github](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)                  
Java 17 </br>
IntelliJ </br>
Ubuntu 22.04 </br>
Spring Boot 3.1.4 </br>

### 사용 기술
Kafka </br>
ElasticSearch </br>
Docker </br>
AWS EC2, RDS, S3 </br>
OAuth2.0 & JWT </br>
HTTPS </br>
Spring Security </br>
GitHub Actions </br>
Amazon EC2 Auto Scaling </br>
AWS CodeDeploy </br>
MySQL </br>
JPA </br>

### 보안 기술
JWT  </br>
HTTPS </br>

### 협업 도구
![Jira](https://img.shields.io/badge/jira-0052CC?style=for-the-badge&logo=jira&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white)
![Discord](https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white)

### 문서화 도구
Postman

---
<!--추가할 내용 의논-->
## 화면 구성 📺
| 메인 페이지  |  ?? 페이지   |
| :-------------------------------------------: | :------------: |
|  <img width="329" src="??"/> |  <img width="329" src="??"/>|  
| ?? 페이지   |  ?? 페이지   |  
| <img width="329" src="??"/>   |  <img width="329" src="??"/>     |

---
<!--추가할 내용 의논-->
## 주요 기능 📦

### ⭐️ 소셜 로그인
- Google을 통해 회원가입이 가능하며 이수 추가적인 정보를 입력할 수 있습니다.
- 회원가입 시 Google의 인증페이지로 리다이렉트하는 URL을 제공합니다.
- 사용자는 자신의 Google의 계정으로 로그인하고 애플리케이션에 대한 접근 권한을 부여 받으며 인증 완료시 권한을 부여하게 되면 애플리케이션 서버로 리다이렉트 됩니다.
- 사용자 인증 완료시 JWT토큰이 생성되고 사용자에게 반환되며 사용자는 해당 JWT토큰을 통해 여러 API를 인증된 상태로 접근 가능합니다.
- 주요 서비스 : **Google 로그인, 사용자 정보 추가입력, MyPage**

### ⭐️ 실시간 경매
- 사용자가 경매 상품에 대해 실시간으로 입찰하고 경매 상태를 확인할 수 있습니다.
- 특정 경매 상품에 대한 상세 정보를 조회가 가능하며 경매 상품의 현재 가격, 최고 입찰자, 경매 종료 시간 등의 정보를 조회할 수 있습니다.
- 입찰 시도시 경매 종료시간 이전이어야 하며 최고 입찰가격보다 높은 입찰 가격으로만 입찰이 가능하고 입찰시 입찰자의 크레딧이 차감됩니다.
- 상위 입찰자가 등장할 경우 입찰한 크레딧은 자동 환불됩니다.
- 경매 종료 특정 시간 전부터(3분전) 새로운 입찰이 이루어진다면  경매 종료 시간을 일정 시간 연장됩니다.
- 경매가 종료되면 최고입찰가가 낙찰가가 됩니다.
- 사용자별 낙찰, 패찰 상태가 보여집니다.
- 주요 서비스 : **상품 입찰, 입찰 현황 조회, 경매시간 연장, 자동 크레딧 환불**
</br>

### ⭐️ 가상 결제
- 상품 구매시 사용자의 크래딧을 차감하여 상품을 결제할 수 있습니다.
- 현재 실제 결재시스템은 미 구현 상태로 히든로직을 통해 사용자의 크래딧을 컨트롤할 수 있습니다.
- 상품 구매 및 입찰 모두 사용자의 크래딧 보다 낮은 가격의 상품만 구매 가능합니다.
- 결제 시작 및 완료 시점까지 모두 별도의 로그를 DB에 저장하여 안전하게 관리할 수 있고 분석 및 오류처리를 유연하게 할 수 있습니다.
- 결제 완료 후 관련정보는 모두 DB에 저장되며 MyPage에서 조회 가능합니다.
- 주요 서비스 : **상품 구매/입찰, 결제시스템, 구매/입찰 내역 조회**
</br>

### ⭐️ 문의 게시판 
- 각 상품에 대해 별도의 문의게시판이 존재하며 사용자가 상품에 대한 질문을 올리고 다른 사용자나 관리자가 답변을 할 수 있습니다.
- 인증 인가가 완료된 사용자만 이용 가능하며 유효하지 않는 값(공백, 일정 길이 이상의 글)은 입력할 수 없습니다. 
- 주요 서비스 : **게시물 조회/등록/수정/삭제, 댓글 조회/등록/수정/삭제, 신고문의**
</br>

### ⭐️ 상품 검색
- 사용자가 입력한 키워드를 통해 유사도가 높은 결과값을 제공합니다.
- Elasticsearch를 활용하여 구현되었으며 Document 객체를 Elasticsearch에 색인화 하여 검색엔진에서 검색 가능하게 합니다.
- 검색 결과로 반환된 Document 객체들은 다시 사용자에게 보여질 수 있게 변환됩니다.
- 기존의 표준 데이터베이스 쿼리에 비해 매우 빠른 응답시간을 보입니다.
- 주요 서비스 : **상품 검색엔진**
</br>

### ⭐️ 상품 추천
- 사용자에게 인기 있는 상품과 최근에 등록된 상품을 추천하요 사용자가 다양한 상품을 쉽게 발견하고 탐색할 수 있게 합니다.
- 인기 상품의 경우 입찰자 수가 많은 상품을 상위 10개로 보여줍니다.
- 최신 상품의 경우 카테고리별 가장 최근에 등록된 상품 상위 10개를 보여줍니다.
- 주요 서비스 : **인기 상품 추천, 최신 상품 조회**
</br>


---
## 아키텍쳐

### 디렉토리 구조
```
main
└── java
└── com
└── example
└── aucison_service
├── AucisonServiceApplication.java
├── BaseTimeEntity.java
├── admin
│ └── AdminController.java
├── config
│ ├── AppConfig.java
│ ├── CorsConfig.java
│ ├── ElasticsearchConfig.java
│ ├── RedisConfig.java
│ └── S3Config.java
├── controller
│ ├── AddressController.java
│ ├── AuthController.java
│ ├── BoardController.java
│ ├── CreditController.java
│ ├── HomeController.java
│ ├── InquiryController.java
│ ├── MypageController.java
│ ├── PaymentsController.java
│ ├── ProductController.java
│ └── WishController.java
├── dto
│ ├── ApiResponse.java
│ ├── aucs_sale
│ │ ├── AucsProductResponseDto.java
│ │ └── SaleProductResponseDto.java
│ ├── auth
│ │ ├── AuthResponseDto.java
│ │ ├── GoogleTokenRequestDto.java
│ │ ├── GoogleTokenResponseDto.java
│ │ ├── MemberAdditionalInfoRequestDto.java
│ │ ├── MemberDto.java
│ │ └── MemberUpdateDto.java
│ ├── board
│ │ ├── CommentCRUDResponseDto.java
│ │ ├── CommentListResponseDto.java
│ │ ├── CommentRegistRequestDto.java
│ │ ├── CommentUpdateRequestDto.java
│ │ ├── PostCRUDResponseDto.java
│ │ ├── PostListResponseDto.java
│ │ ├── PostRegistRequestDto.java
│ │ └── PostUpdateRequestDto.java
│ ├── deliveries
│ │ ├── DeliveriesCreateDto.java
│ │ └── DeliveriesResponseDto.java
│ ├── home
│ │ ├── HomeResponseDto.java
│ │ └── ProductMainResponseDto.java
│ ├── inquiry
│ │ ├── InquiryRequestDto.java
│ │ └── InquiryResponseDto.java
│ ├── mypage
│ │ ├── RequestAddressDto.java
│ │ ├── RequestMembersInfoDto.java
│ │ ├── RequestUpdateAddressDto.java
│ │ ├── ResponseAddressDto.java
│ │ ├── ResponseBidsHistoryDto.java
│ │ ├── ResponseMemberProfileDto.java
│ │ ├── ResponseOrderDetailsDto.java
│ │ ├── ResponseOrderHistoryDto.java
│ │ └── ResponseSellHistoryDto.java
│ ├── orders
│ │ ├── OrdersCreateDto.java
│ │ └── OrdersResponseDto.java
│ ├── payments
│ │ ├── AddrInfoResponseDto.java
│ │ ├── PaymentsRequestDto.java
│ │ └── VirtualPaymentResponseDto.java
│ ├── product
│ │ ├── ProductAllResponseDto.java
│ │ ├── ProductDetailResponseDto.java
│ │ ├── ProductRegisterFinshResponseDto.java
│ │ ├── ProductRegisterRequestDto.java
│ │ └── UpdateOnlyCostResponseDto.java
│ ├── search
│ │ ├── ProductSearchRequestDto.java
│ │ └── ProductSearchResponseDto.java
│ └── wish
│ ├── ProductWishCountDto.java
│ ├── WishRequestDto.java
│ ├── WishResponseDto.java
│ └── WishSimpleResponseDto.java
├── elastic
│ ├── ProductsDocument.java
│ ├── ProductsDocumentRepository.java
│ └── ProductsIndexService.java
├── enums
│ ├── Category.java
│ ├── Kind.java
│ ├── OrderType.java
│ ├── OStatusEnum.java
│ ├── PageType.java
│ ├── PStatusEnum.java
│ ├── QStatusEnum.java
│ └── Role.java
├── exception
│ ├── AppException.java
│ └── ErrorCode.java
├── jpa
│ ├── member
│ │ ├── entity
│ │ │ ├── AddressesEntity.java
│ │ │ ├── HistoriesEntity.java
│ │ │ ├── HistoriesImgEntity.java
│ │ │ ├── InquirysEntity.java
│ │ │ ├── MembersEntity.java
│ │ │ ├── MembersImgEntity.java
│ │ │ ├── MembersInfoEntity.java
│ │ │ └── WishesEntity.java
│ │ └── repository
│ │ ├── AddressesRepository.java
│ │ ├── HistoriesImgRepository.java
│ │ ├── HistoriesRepository.java
│ │ ├── InquirysRepository.java
│ │ ├── MembersImgRepository.java
│ │ ├── MembersInfoRepository.java
│ │ ├── MembersRepository.java
│ │ └── WishesRepository.java
│ └── product
│ ├── entity
│ │ ├── AucsInfosEntity.java
│ │ ├── BidCountsEntity.java
│ │ ├── CommentsEntity.java
│ │ ├── PostsEntity.java
│ │ ├── ProductImgEntity.java
│ │ ├── ProductsEntity.java
│ │ └── SaleInfosEntity.java
│ └── repository
│ ├── AucsInfosRepository.java
│ ├── BidCountsRepository.java
│ ├── CommentsRepository.java
│ ├── PostsRepository.java
│ ├── ProductImgRepository.java
│ ├── ProductsRepository.java
│ └── SaleInfosRepository.java
│ └── shipping
│ ├── entity
│ │ ├── Bids.java
│ │ ├── Deliveries.java
│ │ ├── Orders.java
│ │ ├── PageAccessLogs.java
│ │ ├── Payments.java
│ │ └── Refunds.java
│ └── repository
│ ├── BidsRepository.java
│ ├── DeliveriesRepository.java
│ ├── OrdersRepository.java
│ ├── PageAccessLogsRepository.java
│ ├── PaymentsRepository.java
│ └── RefundsRepository.java
├── kafka
│ ├── KafkaConsumerService.java
│ └── config
│ └── KafkaProducerConfig.java
├── security
│ ├── JwtAuthenticationFilter.java
│ ├── JwtAuthenticationProvider.java
│ ├── JwtTokenProvider.java
│ └── SecurityConfig.java
├── service
│ ├── HomeService.java
│ ├── HomeServiceImpl.java
│ ├── address
│ │ ├── AddressService.java
│ │ └── AddressServiceImpl.java
│ ├── hidden
│ │ ├── CreditService.java
│ │ └── CreditServiceImpl.java
│ ├── member
│ │ ├── GoogleAuthService.java
│ │ ├── InquiryService.java
│ │ ├── InquiryServiceImpl.java
│ │ ├── MemberDetails.java
│ │ ├── MemberInfoService.java
│ │ ├── MemberInfoServiceImpl.java
│ │ ├── MypageService.java
│ │ ├── MypageServiceImpl.java
│ │ ├── UserDetailsServiceImpl.java
│ │ ├── WishService.java
│ │ └── WishServiceImpl.java
│ ├── product
│ │ ├── BoardService.java
│ │ ├── BoardServiceImpl.java
│ │ ├── ProductService.java
│ │ └── ProductServiceImpl.java
│ ├── s3
│ │ └── S3Service.java
│ └── shipping
│ ├── PaymentsService.java
│ └── PaymentsServiceImpl.java
└── util
└── resources
├── application.yml
├── application-aws.yml
├── application-google.yml
├── application-https.yml
├── application-test.yml
└── keystore.p12
```
