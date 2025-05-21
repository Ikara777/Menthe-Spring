# Menthe

## Spring Boot + Java를 활용한 실시간 미술품 경매 서비스

**"그림으로 채우는 나만의 공간"**

예술을 가까이, 일상에 감동을 더하세요.
'몽트' 는 실시간 그림 경매를 통해 원하는 작가의 예술작품을 손쉽게
만나볼 수 있는 플랫폼 입니다.
또한 그림 렌탈을 통해 값비싼 그림을 렌탈하여 부담없이 나만의 특별한
공간을 만들고 일상에 색다른 감동을 줄 수 있습니다.


## 👨‍💻👨🏻‍💻 프로젝트 Time 구성

### FRONTEND & BACKEND

<table>
  <tr>
    <th>최동훈</th>
    <th>심현철</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/66037df4-f7e3-433e-92a5-17b40556ae2f" width="100"></td>
    <td><img src="https://github.com/user-attachments/assets/a70b3182-d9d1-47c8-a0fc-711353348915" width="100"></td>
  </tr>
</table>

## 🖱 사용 기술

**Back-end**
   - 언어: Java
   - 프레임워크: Spring Boot, Spring Security, Spring Data JPA
   - 서버: Tomcat 9.0
   - 데이터베이스: MySQL 8.0
   - 템플릿 엔진: Thymeleaf

**Front-end**
   - 언어 : JavaScript, HTML5, CSS
   - 반응형 웹 디자인 구현 : Bootstrap
   - RESTful API 연동
     
**개발 도구 및 환경**
   - IDE : IntelliJ IDEA
   - 버전 관리 : Git, GitHub
   - 빌드 도구 : Maven
   - 운영체제 : Windows 10
     
**배포 및 인프라**
  - AWS EC2 클라우드 서버 활용
  - PuTTYgen을 통한 SSH 접속
  - FileZilla를 통한 파일 전송


## 🛠 주요 기능

**📉 실시간 경매 응찰 기능**
 - 사용자가 응찰한 시간을 실시간 반영하여 그래프가 변동됨

**🗓️ 캘린더 경메 일정 관리**
 - 메인 페이지의 캘린더를 통해 경매 일정을 등록하고 사용자가 확인할 수 있음 

**🖼️ 렌탈 기능**
 - 미술품을 대여하면 대여 시작일과 반납일이 표기되며 일주일 이내로 대여 취소 가능

**📋 미술품 상품 크롤링 구현**
 - 경매가 진행 중인 상품과 렌탈이 가능한 상품을 외부 사이트에서 크롤링하여 DB에 추가

**💰 캐시 충전 및 환불**
 - 금액을 사전에 미리 충전 시켜 이를 통한 경매와 렌탈이 이루어지도록 구현
 
**🗒️ 1대1 고객 상담 기능**
 - 관리자와 1대1 채팅이 가능하도록 하여 사이트 운영의 편의성을 증가
   
**💼관리자 전용 페이지**
 - 상품 수정, 상품 추가, 채팅 관리 등 사이트 관리에 필요한 기능 구현

**🖼️상품 목록 페이지**
 - 상품 검색 & 조건부 정렬 기능을 통해 사용자가 편하게 상품을 볼 수 있도록 구현

## 👨‍💻 담당 기능 및 구현 상세

**🖼️ 상품 목록 페이지**
- 파라미터 요청을 통하여 상품 검색 & 조건부 정렬 기능 구현
- Pageable 기능을 통하여 상품 페이지를 넘길 수 있도록 구현

**📋 미술품 상품 크롤링 구현**
- Chrome Driver 라이브러리를 사용하여 조건을 부여하여 경매가 진행 중인 상품만 크롤링할 수 있도록 구현
- 팝업 광고로 인해 크롤링이 멈추는 현상이 없도록 팝업 분기점을 확인 후 광고 HTML을 자동으로 끌 수 있도록 구현

**💰 캐시 충전 및 환불**
- JavaScript 라이브러리에 Iamport 추가 후 회원 정보를 Ajax로 전송 후 정상적인 승인이 완료되면 DB에 결제 정보와
Imp 결제 번호를 저장
- 악의적인 Script 코드 수정을 막기 위한 CSRF 보안 추가
- DB에 저장된 결제 정보를 Imp 서버로 전달하여 환불 엑세스 토큰을 받아 Imp 서버에 보안을 맡기도록 설정
- 환불 혹은 결제가 일어나면 DB에 정보가 저장되며 보유 캐시를 계산
- 보유 캐시가 응찰 금액보다 낮을 경우 응찰이 되지 않게 구현

**🗒️ 1대1 고객 상담 기능**
- SpringSecurity를 활용하여 로그인된 유저만 채팅 버튼이 보이게 함
- 비동기 통신을 통하여 관리자와 유저가 메시지를 주고받을 수 있으며 사용자가 편하게 볼 수 있도록 무한 스크롤을 구현함
- 채팅 상담을 상세히 기록하기 위해 입력 시간을 저장할 수 있도록 함

**💼관리자 전용 페이지**
- 상품 등록 수정, 삭제 시 INSERT, UPDATE, DELETE 동작이 되도록 구현
- 관리자가 각각의 상담 고객을 관리할 수 있도록 채팅 목록 구현

**📸이미지 저장 기능**
- 업로드 경로를 사용자가 수정하지 못하도록 파일명을 UUID로 변환
- 악성 파일 업로드를 방지하기 위해 업로드 용량 제한 및 jpg 형식만 허용

## 사이트 흐름 구성도
<img src="![image](https://github.com/user-attachments/assets/3b696ef3-9cd7-465e-b89f-ffec9c80cb58)" width="400">

## DB 다이어그램
<img src="https://github.com/user-attachments/assets/afd4478c-5bed-4b22-847e-ac9092aa1118" width="400">

## 🖥️ 화면 구성

<table>
  <tr>
    <th>메인 화면</th>
    <th>로그인 </th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/1279557b-64e0-4984-b4ce-d51a253e7268" width="400"></td>
    <td><img src="https://github.com/user-attachments/assets/3b2548e7-7e78-40b4-b2a0-9610937eb866" width="400"></td>
  </tr>
</table>

<table>
  <tr>
    <th>상품 페이지</th>
    <th>응찰 현황</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/77fb4f2e-e8ab-4f03-baa2-654a74b00787" width="400"></td>
    <td><img src="https://github.com/user-attachments/assets/34196532-d271-4f1c-8941-b301c87a090e" width="400"></td>
  </tr>
</table>

<table>
  <tr>
    <th>1대1 상담</th>
    <th>캐시 잔액조회</th>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/df7e9092-8b81-4e09-b2db-07498d1244f3" width="400"></td>
    <td><img src="https://github.com/user-attachments/assets/561b8f7b-d1ae-4a32-847e-e01aa09dbaeb" width="400"></td>
  </tr>
</table>





