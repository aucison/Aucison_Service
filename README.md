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
* 23.07 - 24.02

### 🧑‍🤝‍🧑 멤버구성
<!--깃허브 링크 달아주기, 담당 서비스 더 자세히 적으면 좋을듯-->
 - 팀장  : [김태현](https://github.com/hotcoa922) - 기획 및 백엔드 개발
 - 팀원1 : [오승미](https://github.com/seungmio) - 백엔드 개발
 - 팀원2 : 황동현 - 프론트 개발
 - 팀원3 : 손민재 - 프론트 개발
 - 팀원4 : 이정인 - 디자인

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

### ⭐️ 1기능
- ??

### ⭐️ 2기능
- ??

### ⭐️ 3기능
- ??

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
