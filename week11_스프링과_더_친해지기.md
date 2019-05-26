## 계층형 아키텍쳐 ????

어플리케이션의 구성 요소를 역할에 따라 계층별로 나누고 각 계층 사이에 소통을 통해서 작업을 처리하는 애플리케이션 구조

<img width="429" alt="스크린샷 2019-05-25 오전 11 18 55" src="https://user-images.githubusercontent.com/20471248/58363565-44e35000-7ee1-11e9-89dc-11e651755c72.png">

## 왜? 계층적으로 나누어야 할까??
계층적으로 나누지 않고 Controller 클래스, 또는 특정 클래스 하나에 

* Http Request, Response 관리
* 서비스 로직 작성, 트랜잭션 관리, 자원 권한 관리
* 데이터 업데이트 조회 관리

에 대한 코드가 모두 있다고 생각해보자.

#### 문제점
간단한 어플리케이션이라면 문제 없음. 위와 같이 해도됨. 근데 어느정도 규모가 있는 어플리케이션에서 위와 같이 코드를 짠다면...

* 코드를 재사용하기 힘들다
    * 데이터 업데이트 조회 관리, 권한 관리 같이 어플리케이션 전반에 쓰이는 코드를 재사용 하기가 빡세짐
* 유지 보수하기 힘들어짐
    * 하나의 클래스가 여러 책임을 맡게 됨 (단일 책임 원칙 위배)
    * 특정 기능을 추가, 수정 해야 할 때, 그 기능과 상관없는 엉뚱한 코드를 수정해야 할 확률이 높아짐
    * 버그가 발생할 확률도 높아짐
* 버전관리 충돌 확률이 높아짐
    * 다수의 개발자가 MemberController.java 를 동시에 수정한다면?
    * 응 충돌 ^_^!!!!
    
이러한 이유들 때문에 어느정도 기능이 있는 스프링앱은 여러개의 계층으로 나누자 !!

## 여러계층으로 나눌때 규칙이 있습니다.

* 바로 인접한 계층과 소통하자!
* 위에서 아래로 소통하자!
* 인터페이스를 통해서 소통하자! (권장)

## 스프링의 핵심 컨테이너와 빈

Bean Container : ApplicationContext, BeanFactory  
Bean : @Controller, @Service, xml...

Bean Container 는 언제 만들어짐???

##### ContextLoaderListener

> ContextLoaderListener 는 서블릿에서 제공하는 Servlet ContextListener 를 확장하여 만든 것으로 웹 애플리케이션이 서블릿 컨테이너에 로딩될 때 실행되는 리스너입니다
> ContextLoaderListener 가 하는 일은 웹 애플리케이션이 로딩될 때 Web ApplicationContext 를 만드는 것입니다

코드로 봅시다. (web.xml)

<img width="941" alt="스크린샷 2019-05-25 오후 12 25 39" src="https://user-images.githubusercontent.com/20471248/58364040-c8ed0600-7ee8-11e9-8479-d22e0b840989.png">
<img width="1123" alt="스크린샷 2019-05-25 오후 12 32 06" src="https://user-images.githubusercontent.com/20471248/58364064-25502580-7ee9-11e9-83c1-1c2fa5741beb.png">

##### WebApplicationContext

>  WebApplicationContext 는 contextConfigLocation 에 설정한 빈 설정 파일을 사용해서 웹 애플리케이션 에서 사용할 객체를 관리해주는 역할을 합니다.

코드로 봅시다. (applicationContext.xml)

<img width="665" alt="스크린샷 2019-05-25 오후 12 35 31" src="https://user-images.githubusercontent.com/20471248/58364088-9e4f7d00-7ee9-11e9-9942-58016d7d8145.png">
<img width="1018" alt="스크린샷 2019-05-25 오후 12 35 26" src="https://user-images.githubusercontent.com/20471248/58364090-a0b1d700-7ee9-11e9-9400-5aa9e7f1f167.png">

##### DispatcherServlet

갑자기?? DispatcherServlet??

> DispatcherServlet 은 자바 Servlet 을 확장한 클래스로 특정 URL 패턴을 DispatcherServlet 으로 맵핑하여 스프링 MVC 를 사용할 수 있도록 해주는 일종의 대문 역할을 합니다

DispatcherServlet 도 ContextLoaderListener 처럼 WebApplicationContext 를 만듭니다. (web.xml)

##### DispatcherServlet 이 자신의 WebApplicationContext 를 만드는 규칙
1. ContextLoaderListener 가 WebApplicationContext 가 있다면, 그것을 상속 받는 WebApplicationContext 를 만든다.
2. ContextLoaderListener 는 Spring Application 당 한 개의 WebApplicationContext 를 만들고, DispatcherServlet 은 Servlet 설정 당 한개의 WebApplicationContext 를 만든다.

코드로 봅시다. (webmvc-config.xml)

##### 빈을 참조 하는 규칙
* 자식 WebApplicationContext 에서 부모 WebApplicationContext 의 빈을 참조할 수 있다.
* 부모 WebApplicationContext 에서는 자식 WebApplicationContext 의 빈을 참조할 수 없다.
* 자식 WebApplicationContext 에서 어떤 빈을 참조할 때, 먼저 자기자신 내부에 있는 빈을 참조한다.
  만약 자기 자신 내부에 없다면 부모 쪽에서 찾아보고, 부모 쪽에도 원하는 빈이 없다면 예외를 발생시킨다.

## 프로젝트 소스 설명 