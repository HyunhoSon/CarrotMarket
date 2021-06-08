# CarrotMarket

> 중고 물품을 거래하기 위한 OpenMarket 을 단순화 하여 Microservice 를 이용하여 구현. ( 개인과제 )

## Check Point

- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW

## Table Of Content

- [TOP](#CarrotMarket)
- [설계](#설계)
  - [시나리오](#시나리오)
  - [EventStorming](#eventStorming)
  - [HexagonalArchitecture](#hexagonalarchitecture)
- [구현](#구현)
  - [CheckPoint1. Saga](#checkpoint1-saga)
  - [CheckPoint2. CQRS](#checkpoint2-cqrs)
  - [CheckPoint3. Correlation](#checkpoint3-correlation)
  - [CheckPoint4. Req/Resp](#checkpoint4-reqresp)
  - [CheckPoint5. Gateway](#checkpoint5-gateway)
  - [CheckPoint6. Polyglot](#checkpoint6-polyglot)
- [운영](#운영)
  - [CheckPoint7. Deploy/ Pipeline](#checkpoint7-deploy-pipeline)
  - [CheckPoint8. Circuit Breaker](#checkpoint8-circuit-breaker)
  - [CheckPoint9. Autoscale (HPA)](#checkpoint9-autoscale-hpa)
  - [CheckPoint10. Zero-downtime deploy (Readiness Probe)](#checkpoint10-zero-downtime-deploy-readiness-probe)
  - [CheckPoint11. Config Map/ Persistence Volume](#checkpoint11-config-map-persistence-volume)
  - [CheckPoint12. Self-healing (Liveness Probe)](#checkpoint12-self-healing-liveness-probe)


## 설계

### 시나리오

기능적 요구사항  
1. 판매자는 물건을 등록한다.
2. 구매자는 판매자가 등록한 물건의 구매 요청 / 결제를 한다.
3. 판매자는 구매자의 구매 요청을 수락 또는 거절 한다.
4. 판매자가 수락한 요청은 '수락됨' 상태로 변경된다.
5. 판매자가 거절한 요청은 '거절됨' 상태로 변경된다.
6. 구매자는 '수락됨' 상태의 요청의 완료처리를 할 수 있다.
7. 구매요청이 완료처리되면 결제금액이 판매자에게 지급된다.
8. 판매자가 거절한 요청의 결제금액은 환불된다.

비기능적 요구사항
1. 구매자는 구매 요청시에 결제를 끝내야 한다. (Sync)
2. 결제 시스템이 과중되면 사용자를 잠시 받지 않는다. (Circuit Break)
3. 판매자의 물건 등록은 구매자측 기능과 관계없이 가능해야 한다. (장애 격리)

### EventStorming

#### 이벤트 도출

* 각 도메인에 필요한 이벤트를 도출

![image](https://user-images.githubusercontent.com/9324206/121125349-62ce4400-c861-11eb-8dcf-fb6965f97d75.png)

* 일부 불필요한 이벤트 삭제

![image](https://user-images.githubusercontent.com/9324206/121126685-885c4d00-c863-11eb-8e10-49af269cfed6.png)

* Policy 부착

![image](https://user-images.githubusercontent.com/9324206/121127432-bbeba700-c864-11eb-8cc9-6e19833e4b9c.png)

* Aggregate / Boundary Context 추가 (1차 완성)

![image](https://user-images.githubusercontent.com/9324206/121127514-d7ef4880-c864-11eb-93e4-69981f4082a9.png)

* 기능 요구사항 검증   

![image](https://user-images.githubusercontent.com/9324206/121128562-8cd63500-c866-11eb-9eea-b1315c9fcef9.png)

|번호 | 요구사항 | 만족 |
|---|---|---|
|1| 판매자는 물건을 등록한다.                               |  O |
|2| 구매자는 판매자가 등록한 물건의 구매 요청 / 결제를 한다.   |  O |
|3| 판매자는 구매자의 구매 요청을 수락 또는 거절 한다.         |  O |
|4| 판매자가 수락한 요청은 '수락됨' 상태로 변경된다.           |  O |
|5| 판매자가 거절한 요청은 '거절됨' 상태로 변경된다.           |  O |
|6| 구매자는 '수락됨' 상태의 요청의 완료처리를 할 수 있다.     |  O |
|7| 구매요청이 완료처리되면 결제금액이 판매자에게 지급된다.     |  O |
|8| 판매자가 거절한 요청의 결제금액은 환불된다.               |  O |

* 비기능 요구사항 검증

![image](https://user-images.githubusercontent.com/9324206/121128747-d6bf1b00-c866-11eb-8866-056ba2b70062.png)

| 번호| 요구사항 | 만족 | 비고 |
 |---|---|---|---|
 | 1| 구매자는 구매 요청시에 결제를 끝내야 한다. (Sync)  | O | Req / Resp 호출|
 | 2| 결제 시스템이 과중되면 사용자를 잠시 받지 않는다. (Circuit Break) | O | 서킷 브레이커 구현 가능 |
 | 3| 판매자의 물건 등록은 구매자측 기능과 관계없이 가능해야 한다. (장애 격리) | O | Microservice 분리 및 Async 통신|

### Hexagonal Architecture

![image](https://user-images.githubusercontent.com/9324206/121131408-b6915b00-c86a-11eb-8e1a-2abb7c67f83f.png)

## 구현

### 요구사항 충족여부 검증

1. 판매자는 물건을 등록한다. (확인)   
   ![image](https://user-images.githubusercontent.com/9324206/121140890-53f18c80-c875-11eb-92ef-50010e3f167f.png)

2. 구매자는 판매자가 등록한 물건의 구매 요청 / 결제를 한다.   
   ![image](https://user-images.githubusercontent.com/9324206/121141764-4c7eb300-c876-11eb-8452-2af8f7ad7f1b.png)
```
# kafka 
{"eventType":"WtbAdded","timestamp":"20210608162608","wtbId":1,"paymentId":null,"state":"Requested","price":3000,"productId":1}
```
   
3. 판매자는 구매자의 구매 요청을 수락 또는 거절 한다.   
   ![image](https://user-images.githubusercontent.com/9324206/121146599-0bd56880-c87b-11eb-8fd1-f695a8323055.png)
   
4. 판매자가 수락한 요청은 '수락됨' 상태로 변경된다.   
   ![image](https://user-images.githubusercontent.com/9324206/121146660-1c85de80-c87b-11eb-857f-794667c527ab.png)

5. 판매자가 거절한 요청은 '거절됨' 상태로 변경된다.   
   ![image](https://user-images.githubusercontent.com/9324206/121148487-ca45bd00-c87c-11eb-8ec7-e7c73ef92fb1.png)

6. 구매자는 '수락됨' 상태의 요청의 완료처리를 할 수 있다.    
   ![image](https://user-images.githubusercontent.com/9324206/121148909-26a8dc80-c87d-11eb-993c-318a7ed62e75.png)

7. 구매요청이 완료처리되면 결제금액이 판매자에게 지급된다.   
   ![image](https://user-images.githubusercontent.com/9324206/121150956-ee0a0280-c87e-11eb-8ac0-0e5d84d51790.png)   
   Pay Log 에 판매자 지급 사항 표기   
   ![image](https://user-images.githubusercontent.com/9324206/121151041-ff530f00-c87e-11eb-9afe-e74b6ca8d7f2.png)

8. 판매자가 거절한 요청의 결제금액은 환불된다.   
   ![image](https://user-images.githubusercontent.com/9324206/121150429-7dfb7c80-c87e-11eb-94f7-3cea8e26fed3.png)   
   Pay Log 에 환불 사항 표기   
   ![image](https://user-images.githubusercontent.com/9324206/121150283-560c1900-c87e-11eb-88a6-3d1d7ca31f28.png)


### CheckPoint1. Saga

### CheckPoint2. CQRS

### CheckPoint3. Correlation

### CheckPoint4. Req/Resp

### CheckPoint5. Gateway

### CheckPoint6. Polyglot

## 운영

### CheckPoint7. Deploy/ Pipeline

### CheckPoint8. Circuit Breaker

### CheckPoint9. Autoscale (HPA)

### CheckPoint10. Zero-downtime deploy (Readiness Probe)

### CheckPoint11. Config Map/ Persistence Volume

### CheckPoint12. Self-healing (Liveness Probe)

