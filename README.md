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

![image](https://user-images.githubusercontent.com/9324206/121386599-19771500-c985-11eb-8b7b-d6f0319cb875.png)

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

> 비기능적 요구사항은 아래 Check Point 의 해당 항목으로 대체.


### CheckPoint1. Saga   

이벤트 Pub / Sub 구현   

* Publish   
WTB :: Wtb.java / wtbAdded Event Publish 구현부
![image](https://user-images.githubusercontent.com/9324206/121379163-be422400-c97e-11eb-8245-fb338df727ec.png)   

* Subscribe   
WTS :: PolicyHandler.java / wtbAdded Event Subscribe 구현부   
![image](https://user-images.githubusercontent.com/9324206/121379492-08c3a080-c97f-11eb-9044-ce8b6fe48aeb.png)

### CheckPoint2. CQRS

CQRS 패턴에 따라 Command 와 Query 를 분리하여, myPage를 통해 구매요청건(WTB)의 조회가 가능하다.   
MyPage의 Content Id는 구매요청건(WTB)의 Id 를 그대로 사용하도록 하여 구매요청건 별로 조회가 가능하도록 하였다.
* 최초 구매요청 시 MyPage 1번 항목 조회 결과 (state : Requested)   
![image](https://user-images.githubusercontent.com/9324206/121380647-031a8a80-c980-11eb-8e22-6cf9b202a2d5.png)   

* 구매요청건 수락 시 MyPage의 1번 항목 조회 결과 (state : Accepted)   
![image](https://user-images.githubusercontent.com/9324206/121380847-2e04de80-c980-11eb-8a7f-ead065be7e9c.png)


### CheckPoint3. Correlation

데이터의 흐름이 구매요청건(WTB) 를 중심으로 이루어 지므로, wtbId를 Correlation Key 로 사용하여 처리건을 식별하였다.    
결제 서비스 :: 구매요청 완료 후 결제 금액을 판매자에게 전송하는 부분. wtbId 로 결제건을 식별하여 처리한다.   
![image](https://user-images.githubusercontent.com/9324206/121382157-4cb7a500-c981-11eb-9321-b39b5a47ed4a.png)


### CheckPoint4. Req/Resp    

비기능적 요구사항 \[1. 구매자는 구매 요청시에 결제를 끝내야 한다.] 를 만족시키기 위해   
주문요청 생성 --> 결제 처리 간의 처리방식을 Req/Resp 로 구현하였으며, RestRepository를 이용하였다.   
* WTB :: payment 신규 생성 Req/Resp 방식 호출부   
![image](https://user-images.githubusercontent.com/9324206/121382876-fac34f00-c981-11eb-9280-8435810b89a7.png)

* WTB :: PaymentService.java / RESTful 함수 FeignClient 정의부   
![image](https://user-images.githubusercontent.com/9324206/121383231-4249db00-c982-11eb-95b6-8a04a72ac50e.png)


### CheckPoint5. Gateway   
API Gateway를 적용하여, MicroService의 진입점을 단일화 하였다.   
* Default Profile : 8088 Port, http://URL:8088/{context}
* Docker Profile : 8080 Port, http://URL:8080/{context}   

```
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: WTS
          uri: http://localhost:8081
          predicates:
            - Path=/wtbInboxes/**,/wts/** 
        - id: WTB
          uri: http://localhost:8082
          predicates:
            - Path=/wtbs/** 
        - id: Pay
          uri: http://localhost:8083
          predicates:
            - Path=/payments/** 
        - id: Viewer
          uri: http://localhost:8084
          predicates:
            - Path= /myPages/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: WTS
          uri: http://WTS:8080
          predicates:
            - Path=/wtbInboxes/**,/wts/** 
        - id: WTB
          uri: http://WTB:8080
          predicates:
            - Path=/wtbs/** 
        - id: Pay
          uri: http://Pay:8080
          predicates:
            - Path=/payments/** 
        - id: Viewer
          uri: http://Viewer:8080
          predicates:
            - Path= /myPages/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080

```

### CheckPoint6. Polyglot

타 서비스와 별개로 Viewer 서비스는 sql db를 사용하였다.
그 외 서비스는 (h2 사용)

* Viewer 의 application.yml   
![image](https://user-images.githubusercontent.com/9324206/121798404-0f913280-cc61-11eb-84be-ec1abb358d28.png)

* MySQL 조회 결과   
![image](https://user-images.githubusercontent.com/9324206/121798279-4fa3e580-cc60-11eb-9362-af6dd2f8307f.png)


## 운영

### CheckPoint7. Deploy/ Pipeline

* git 으로부터 소스 pull   

```
git clone https://github.com/HyunhoSon/CarrotMarket.git
```

* Maven Packaging / Docker Build, Push
```

cd /gateway
mvn package         # Maven Packaging
docker build -t skcchhson.azurecr.io/gateway:latest .     # Docker Build
docker push skcchhson.azurecr.io/gateway:latest           # Docker Push to Azure Container Registry

cd ../WTB
mvn package         # Maven Packaging
docker build -t skcchhson.azurecr.io/wtb:latest .     # Docker Build
docker push skcchhson.azurecr.io/wtb:latest           # Docker Push to Azure Container Registry

cd ../WTS
mvn package         # Maven Packaging
docker build -t skcchhson.azurecr.io/wts:latest .     # Docker Build
docker push skcchhson.azurecr.io/wts:latest           # Docker Push to Azure Container Registry

cd ../Pay
mvn package         # Maven Packaging
docker build -t skcchhson.azurecr.io/pay:latest .     # Docker Build
docker push skcchhson.azurecr.io/pay:latest           # Docker Push to Azure Container Registry

cd ../Viewer
mvn package         # Maven Packaging
docker build -t skcchhson.azurecr.io/viewer:latest .     # Docker Build
docker push skcchhson.azurecr.io/viewer:latest           # Docker Push to Azure Container Registry

```

* Yaml 파일을 이용한 Deployment   

```
# WTB/kubernetes/deployment.yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: wtb
  labels:
    app: wtb
spec:
  replicas: 1
  selector:
    matchLabels:
      app: wtb
  template:
    metadata:
      labels:
        app: wtb
    spec:
      containers:
        - name: wtb
          image: skcchhson.azurecr.io/wtb:latest
          ports:
            - containerPort: 8080
          readinessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 10
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 10
          livenessProbe:
            httpGet:
              path: '/actuator/health'
              port: 8080
            initialDelaySeconds: 120
            timeoutSeconds: 2
            periodSeconds: 5
            failureThreshold: 5
          resources:
            requests:
              cpu: 300m
              # memory: 256Mi
            limits:
              cpu: 500m
```

* Deploy 완료   
![image](https://user-images.githubusercontent.com/9324206/121811705-260aae80-cca0-11eb-9060-d090830400e6.png)



### CheckPoint8. Circuit Breaker   

결제 시스템에 과부하가 생길 경우 Circuit Breaker 를 작동시켜, 구매요청을 진행할 수 없도록 설정하였다.   
이때 시도한 요청은 Pay_Failed 라는 상태값으로 저장되어 추후 식별이 가능하도록 하였다.   
테스트를 수행하기 위해 아래와 같이 price 조건을 만족 할 때에 시간지연이 

* Application.yml 설정   
![image](https://user-images.githubusercontent.com/9324206/121879726-1f377680-cd48-11eb-8e0b-69831216c8c1.png)

* Pay Microservice 내에 시간지연 설정부분   
![image](https://user-images.githubusercontent.com/9324206/121892065-3af64900-cd57-11eb-8903-351d60a93293.png)

* 구매요청 시 Pay_Failed 발생 화면     
price==2000 --> Requested   
price==1500 --> Pay_Failed   
![image](https://user-images.githubusercontent.com/9324206/121891931-100bf500-cd57-11eb-999e-de4602310396.png)



### CheckPoint9. Autoscale (HPA)

구매요청이 다수 발생할 경우 Autoscale을 이용하여 Pod Replica를 3개까지 확장하도록 하였다.   
테스트를 위하여 pod 확장의 조건은 부하 50%로 지정하였다.

* hpa.yml   
```
apiVersion: autoscaling/v1
kind: HorizontalPodAutoscaler
metadata:
  name: wtb-hpa
spec:
  maxReplicas: 3
  minReplicas: 1
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: wtb
  targetCPUUtilizationPercentage: 50
```

* hpa 정상 생성 확인   
![image](https://user-images.githubusercontent.com/9324206/121895300-071d2280-cd5b-11eb-8a88-963d47b881d9.png)

* siege 부하 테스트 결과 (100%)    
siege 부하 테스트 command
```
siege -c100 -t60S -r10 --content-type "application/json" 'http://wtb:8080/wtbs POST {"productId":"1","price":"3000"}'
```
![image](https://user-images.githubusercontent.com/9324206/121897297-2fa61c00-cd5d-11eb-963b-6cc704f607a5.png)

* siege 부하 테스트 중 pod 확장 모니터링   
![image](https://user-images.githubusercontent.com/9324206/121897399-49dffa00-cd5d-11eb-8a19-12901b89004f.png)

* siege 부하 테스트 후 pod 상태 및 hpa 상태 조회   
![image](https://user-images.githubusercontent.com/9324206/121897601-827fd380-cd5d-11eb-9f59-af3f9d567659.png)


### CheckPoint10. Zero-downtime deploy (Readiness Probe)

WTB(구매요청) Microservice 내에 Readiness Probe를 설정, siege를 이용하여 부하를 준 후 image 버전 교체를 수행하여   
Availability를 확인한다.

* 이미지 변경 전 버전 확인   
![image](https://user-images.githubusercontent.com/9324206/121902475-5155d200-cd62-11eb-9642-844cab6500a5.png)


* 무정지 재배포를 위한 Readiness Probe 설정   
![image](https://user-images.githubusercontent.com/9324206/121899373-549b8e80-cd5f-11eb-8f64-6c59d9996f6d.png)

* siege 를 이용하여 -C1의 약한 부하를 가함    
```
siege -c1 -t180S -r100 --content-type "application/json" 'http://wtb:8080/wtbs POST {"productId":"1","price":"3000"}'
```

* image 버전을 변경   
```
kubectl set image deployment wtb wtb=skcchhson.azurecr.io/wtb:v2
```

* deploy 모니터링 수행
```
kubectl get deploy -l app=wtb -w
```   
![image](https://user-images.githubusercontent.com/9324206/121900662-9bd64f00-cd60-11eb-9801-4600b97b2b1c.png)

* siege 부하 결과 확인 (100% Availability)   
![image](https://user-images.githubusercontent.com/9324206/121901079-0e472f00-cd61-11eb-8990-312e1fd936b1.png)

* 이미지 정상 변경 확인 (describe pod)   
![image](https://user-images.githubusercontent.com/9324206/121901685-93324880-cd61-11eb-96b3-63a4aa5d72fa.png)



### CheckPoint11. Config Map/ Persistence Volume

WTS 서비스의 h2 DB를 파일 형태로 저장하기 위해 PVC 를 사용하였다.
이를 위해 PVC 를 Yaml 파일을 통해 생성하고, deployment.yaml 파일에 Mount 경로를 정의하였으며   
application.yaml 파일 내에 file 경로를 Mount Path 로 지정하였다.

* PVC 생성 Yaml 파일   
```
# pvc.yaml

kind: PersistentVolumeClaim
apiVersion: v1
metadata:
  name: wts-pvc
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
```

* deployment.yaml   
  * Mount Path 지정   
![image](https://user-images.githubusercontent.com/9324206/121813035-4d17af00-cca5-11eb-9ab2-cbc962ec435c.png)
  * Mount 할 Volume 정보 지정   
![image](https://user-images.githubusercontent.com/9324206/121813061-720c2200-cca5-11eb-980e-78f2e6f7f430.png)


* application.yaml
  * Docker Profile 의 h2 DB 경로 설정   
![image](https://user-images.githubusercontent.com/9324206/121813097-8b14d300-cca5-11eb-9bbc-a6e2597b8d9f.png)

* 정상 Mount 확인 (kubectl exec 이용)
  * ls 로 디렉터리 확인   
![image](https://user-images.githubusercontent.com/9324206/121813174-cfa06e80-cca5-11eb-9deb-53c7716b01f1.png)
  * df 로 Mount 확인   
![image](https://user-images.githubusercontent.com/9324206/121813189-e6df5c00-cca5-11eb-916d-863982bd7eb9.png)
  * 해당 경로 내 db 파일 생성 확인   
![image](https://user-images.githubusercontent.com/9324206/121813214-08d8de80-cca6-11eb-956e-650ee5d0c362.png)



### CheckPoint12. Self-healing (Liveness Probe)

Self-healing 확인을 위한 Liveness Probe 옵션 변경 (Port 변경)

* WTB Service Liveness Probe Port 변경 --> 9090   
![image](https://user-images.githubusercontent.com/9324206/121811923-f1e3bd80-cca0-11eb-8eee-269535bab61c.png)

* 재배포(Deploy) 후 Pod Restart 확인   
![image](https://user-images.githubusercontent.com/9324206/121812093-9a921d00-cca1-11eb-83e9-64c41ef03860.png)


* Restart 원인 Liveness Probe 확인   
![image](https://user-images.githubusercontent.com/9324206/121812124-b4336480-cca1-11eb-8b66-ae5f11b5624e.png)


