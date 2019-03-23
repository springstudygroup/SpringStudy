빈 라이프사이클과 빈 범위
=======================

목표 
-----------------------
- 빈 객체의 라이프 사이클을 이해한다.
- 빈 객체의 범위를 이해한다.


## 1. 빈 객체의 라이프 사이클
: 스프링 컨테이너는 빈 객체 생성, 프로퍼티 할당, 초기화 수행, 소멸시키는 일련의 과정을 관리한다.
빈 객체의 초기화뿐만 아니라 빈 객체의 소멸 과정 등 빈의 라이프사이클을 관리할 수 있도록 스프링 인터페이스 설정방법을 알아보자.

- 1.1 빈 라이프 사이클 개요
빈의 라이프 사이클은 [객체 생성/프로퍼티 설정 > 초기화 > 사용 > 소멸]의 과정을 거친다. 컨테이너는 빈 객체를 설정하고 프로퍼티를 설정한 뒤에 빈의 초기화를 진행하며, 컨테이너를 종료하는 과정에서 생성한 빈 객체의 소멸 과정을 진행한다.
빈의 초기화와 소멸과정은 각각 세가지가 존재하며, 각 방식이 쌍을 이루며 함꼐 사용되곤 한다.

- 1.2 InitializingBean 인터페이스와 DisposableBean 인터페이스
스프링은 객체의 초기화 및 소멸 과정을 위해 다음의 두 인터페이스를 제공하고 있다.

-o.s.beans.factory.InitializingBean : 빈의 초기화 과정에서 실행될 메서드를 정의
-o.s.beans.factory.DisableBean : 빈의 소멸 과정에서 실행될 메서드를 정의

이 두 인터페이스는 각각 다음과 같이 정의되어 있다.
  
  ```java
  //[코드-1] : 빈의 초기화/소멸 메서드 정의
  //빈의 초기화 과정에서 실행될 메서드 정의 
  public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
  }
  
  //빈의 소멸 과정에서 실행될 메서드 정의
  public interface DisposableBean {
    void destoy() throws Exception;
  }
  ```
  스프링 빈 객체가 정상적으로 동작하기 위해 객체 생성 이외의 추가적인 초기화과정이 필요하다면 InitializingBean 인터페이스를 상속받고
  afterPropertiesSet() 메서드에서 초기화 작업을 수행한다. 또한 스프링 컨테이너가 종료될 때 DisposableBean 인터페이스를 상속받아
  destroy() 메서드에서 소멸 작업을 수행한다.
  
  초기화/소멸 과정이 필요한 전형적인 예가 데이터베이스 커넥션 풀 기능이다. 커넥션 풀은 미리 커넥션을 생성해 두었다가 커넥션이 필요할 때 제공하는 기능이기 때문에 초기화과정이 필요하고 더 이상 커넥션이 필요 없으면 생성한 커넥션을 모두 닫기 위해 소멸 과정을 필요로 한다. 이런 커넥션 풀 기능을 빈으로 사용하고 싶은 경우, InitializingBean 인터페이스와 DisposableBean 인터페이스를 상속받아 초기화/소멸 과정을 처리할 수 있다.
  
  + 데이터베이스 커넥션 풀 기능 
  ```java
  // [코드-2] : 커넥션 풀 기능을 스프링 빈으로 사용하는 경우
  import org.springframework.beans.DisposableBean;
  import org.springframework.beans.InitializingBean;
  
  public class ConnPool implements InitializingBean,DisposableBean {
    ...
    @Override
    public void afterPropertiesSet() throws Exception {
      //커넥션 풀 초기화 실행: DB 커넥션을 여는 코드
    }
    
    @Override
    public void destroy() throws Exception {
      //커넥션 풀 종료 실행 : 연 DB 커넥션을 닫는 코드
    }
  }
  ```
위 클래스를 스프링 빈으로 등록하면, 스프링 컨테이너는 빈 생성 후 afterPropertiesSet() 메서드를 호출해서 초기화를 진행하고
destroy() 메서드를 호출해서 소멸을 진행한다.

 ```java
  // [코드-3] : 빈 설정 
  <!-- 스프링 컨테이너가 afterPropertiesSet() 메서드를 실행해서 초기화를 진행하고, 컨테이너를 종료할 때 빈의 destroy() 메서드를
   실행헤서 소멸을 진행한다. -->
   <bean id="connPool" class="new.madvirus.spring4.chap03.ConnPool">
   </bean>
  ```
  이 두 인터페이스를 모두 상속해야하는 것은 아니며 필요한 인터페이스만 상속받으면 된다.

- 1.3 @PostConstruct 애노테이션과 @PreDestroy 애노테이션

  : 스프링 컨테이너가 빈 객체의 초기화/소멸 메서드를 실행할 수 있는 또 다른 방법은 @PostConstruct 애노테이션과 @PreDestroy 애노테이션을
  사용하는 것이다. 각각 초기화를 실행하는 메서드와 소멸을 실행하는 메서드에 적용한다.
  
  
  ```java
  // [코드-4] : @PostConstruct, @PreDestroy 애노테이션 사용
  import javax.annotation.PostConstruct;
  import javax.annotation.PreDestroy;
  
  public class ConnPool2{
    
    @PostConstruct
    public void initPool() {
      //컨넥션 풀 초기화 실행: DB 커넥션을 여는 코드
    }
    
    @PreDestroy
    public void destroyPool() {
      //커넥션 풀 종료 실행 : 연 DB 커넥션을 닫는 코드
    }
  }
  ```
  
  ```java
  
  // [코드-5] : @PostConstruct와 @PreDestroy를 가진 빈 설정
<beans xmlns="http://www.springframework.org/schema/beans"
 xmlns:context="http://www.springframework.org/schema/context"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-insrance"
  http://www.springframework.org/schema/beans/spring-beans.xsd>
  http://www.springframework.org/schema/context
  http://www.springframework.org/schema/context/spring-context.xsd">
  
  <context:annotation-config />
  
  
  AnnotationConfigApplicationContext를 사용할 경우 애노테이션과 관련된 기능을 모두 활성시키므로 별도의 설정을 추가할 필요는 없다.
  초기화와 소멸 과정에서 사용될 메서드는 파라미터를 가져서는 안 된다.
  
- 1.4 커스텀 init 메서드와 커스텀 destory 메서드

스프링은 @PostConstruct 애노테이션을 갖고있지 않고 Initializing 인터페이스를 상속받지 않았을 경우 초기화 메서드를 실행할 수 있도록 
커스텀 초기화 메서드를 지정하는 방법을 제공하고 있으며 커스텀 소멸 메서드를 지정하는 방법도 제공하고 있다.

XML설정을 사용하여 다음과 같이 init-method 속성과 destroy-method  속성을 사용해서 초기화 및 소멸 과정에서 사용할 메서드의 이름을 지정할 수 있다.
  ```java
  // [코드-6] : XML설정을 통해 메서드를 지정
  <bean id="pool3" class="net.madvirus.spring4.chap03.CoonPool3" init-method="init" destroy-method="destroy" />
  ```
 자바 기반 설정을 사용한다면, @BEAN 애노테이션의 initMethod 속성과 destroyMethod 속성을 사용하면 된다.
  ```java
  // [코드-7] : 자바 기반 설정 사용할 경우
  @Bean(initMethod = "init", destroyMethod = "destroy")
  public ConnPool3 connPool3() {
    return new ConnPool3();
  }
  
  ```
  초기화와 소멸 과정에서 사용될 메서드는 파라미터를 가져서는 안 된다.
  
- 1.5 ApplicationContextAware 인터페이스와 BeanNameAware 인터페이스
빈으로 사용될 객체에서 스프링 컨테이너에 접근하거나 빈 객체에서 로그를 기록할 때 빈의 이름을 남기고 싶을 경우
o.s.context.ApplicationContextAware, o.s.beans.factory.BeanNameAware 인터페이스를 사용하면 된다.
-o.s.context.ApplicationContextAware : 이 인터페이스를 상속받은 빈 객체는 초기화 과정에서 컨테이너(AppicationContext)를 전달받는다.
-o.c.beans.factory.BeanNameAware : 이 인터페이스를 상속받은 빈 객체는 초기화 과정에서 빈 이름을 전달받는다.
```java
// [코드-8] : ApplicationContext 인터페이스의 정의
public interface ApplicationContextAware extends Aware {
  void setApplicationContext(ApplicationContext applicationContext)
  throws BeansException;
}
```
ApplicationContext 인터페이스를 상속받아 구현한 클래스는 setApplicationContext() 메서드를 통해서 컨테이너 객체(ApplicationConttext)를 전달받는다.
따라서 전달받은 컨테이너를 필드에 보관할 후, 이를 이용해서 다른 빈 객체를 구하거나 컨테이너가 제공하는 기능(이벤트 발생, 메세지 구하기)을 이용할 수 있다.
아래 코드는 ApplicationContextAware 인터페이스의 구현 예시이다.

```java
// [코드-9] : ApplicationContextAware 인터페이스 구현 예시
public interface ApplicationContextAware extends Aware {
  void setApplicationContext(ApplicationContext applicationContext)
  throws BeansException;
}
```
```java
// [코드-10] : ApplicationContext 인터페이스의 정의
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class WorkScheduler implements ApplicationContextAware {
  
  private WorkRunner workRunner;
  private ApplicationContext ctx;
  
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.cts = applicationContext;
  }
  
  public void makeAndRunWork() {
  for(long order = 1; order <=10; order++){
    Work work = ctx.getBean("workProto",Work.class);
    work.setOrder(order);
    workRunner.execute(work);
  }
 }
  
}
```
BeanNameAware 인터페이스는 다음과 같이 정의되어 있다.
```java
// [코드-11] : BeanNameAware 인터페이스의 정의
public interface BeanNameAware extends Aware {
  void setBeanName(String name);
}
```
BeanNameAware 인터페이스를 상속받아 구현한 클래스는 setBeanName() 메서드를 이용해서 빈의 이름을 전달받는다. 따라서 로그 메세지에 빈의 이름을 함께 기록해야할 때처럼 빈의 이름이 필요한 경우에 BeanNameAware 인터페이스를 사용하면 된다.
```java
// [코드-12] : BeanNameAware 인터페이스의 사용
import org.springframework.beans.factory.BeanNameAware;

public class WorkRunner implements BeanNameAware {
  
  private String beanId;
  
  @Override
  public void setBeanName(String name) {
    this.beanId = name;
  }
  
  public void execute(Work work) {
  logger.debug(
  String.format("WorkRunner[%s] execute Work[%d]",beanId, work.getOrder()));
  work.run();
  }
}
```
주로 같은 타입을 가진 빈 객체들이 두 개 이상 존재하고, 각 빈을 이르으로 구분해야 할 떄, BeanNameAware 인터페이스를 사용한다.

## 2. 빈 객체 범위(scope)
: 스프링의 빈은 범위(scope)를 갖는데 주요 범위에는 다음의 두 가지가 있다.
-싱글톤(singleton) 범위
-프로토타입(prototype) 범위

이 범위는 빈 객체의 생명주기와 관련된다.

- 2.1 싱글톤 
: 스츠링은 기본적으로 빈 객체를 한 번만 생성한다. getBean() 메서드를 두 번 이상 호출해서 빈을 구하면 매번 동일한 빈 객체를 리턴한다.
```java
// [코드-13] : 빈 객체 설정 xml
<bean id="Pool1" class="net.madvirus.chap03.ConnPool1">
</bean>
```
이 xml 설정을 이용해서 컨테이너를 생성한 다음 "pool1" 빈 객체를 아래와 같이 구한다고 가정했을 때
```java
// [코드-14] : pool1 빈 객체 구현
ConnPool1 p1 = ctx.getBean("pool1", ConnPool1.class);
ConnPool1 p2 = ctx.getBean("pool1", ConnPool1.class);
//p1,p2는 동일한 객체를 참조
```
스프링 컨테이너는 이름이 "pool1"인 빈 객체를 한 개만 생성하고, getBean() 메서드는 매번 동일한 객체를 리턴하기 때문에 p1과 p2는 같은 객체를 참조하게 된다.
스프링 컨테이너가 초기화되고 종료되기 직전까지 빈 객체는 한 개만 생성되는데, 즉, 스프링 컨테이너를 기준으로 빈 객체는 1개만 존재하기 때문에 빈은 싱글톤(singleton) 범위를 갖는다고 한다.
스프링은 싱글톤을 기본으로 갖고 이를 명시적으로 표시하고 싶다면 다음 [코드-15]와 같이 scope 속성이나 @Scope 애노테이션을 추가해주면 된다.
```java
// [코드-15] : scope 속성 부여
--XML
<bean id="pool1" class="net.madvirus.chap03.ConnPool1" scope="singleton">
</bean>

--자바
import org.springframework.context.annotation.Scope;

@Bean
@Scope("singleton")
public ConnPool1 pool1(){
  return new ConnPool1();
}
```

- 2.2 프로토 타입 범위

```java
// [코드-16] : 프로토타입을 사용하지 않을 경우
for(Long ordNum : orderNumbers){
  Work work = new Work();
  work.setTimeout(2000);
  work.setType(WorkType.SINGLE);
  work.setOrder(ordNum);
  workRunner.execute(work);
}
```
[코드-16]은 for 루프에서 매번 새로운 Work 객체를 생성하는데, Work 객체는 order 프로퍼티를 제외한 나머지가 동일한 값을 가진다.
이런 경우 해당 프로퍼티를 제외한 나머지가 동일한 객체를 생성해주는 기능이 있다면, 다음[코드-17]과 같이 만들 수 있다.
```java
// [코드-17] : 프로토타입을 사용할 경우
for(Long ordNum : orderNumbers){
  //createWork()가 동일 값을 갖는 Work 객체를 생성하는 기능이라고 가정할 경우
  Work work = createWork();
  work.setOrder(ordNum);
  workRunner.execute(work);
}
```
이렇게 동일한 값을 갖는 객체를 생성할 때 사용할 수 있는 것이 프로토타입 범위를 갖는 빈이다. 프로토타입(prototype) 범위의 빈을 getBean() 등을 이용하여 구할 경우 스프링 컨테이너는 매번 새로운 객체를 생성한다.

```java
// [코드-18] : 프로토타입에 scope 지정
<bean id="workProto" class="net.madvirus.spring4.chap03.Work" scope="prototype">
  <property name="timeout" value="2000 /">
  <property name="type" value="#T(nte.madvirus.spring4.chap03.Work$WorkType).SINGLE" />
</bean>  
```
프로토타입 범위로 설정하기 위해서는 다음과 같이 <bean> 태그의 scope 속성을 "prototype"으로 지정하면 된다.

```java
// [코드-19] : 프로토타입에 scope 애노테이션
import org.springframework.context.annotation.Scope;

@Configuration
public class ConfigForScope {

  @Bean
  @Scope("prototype")
  public Work workProto() {
    Work work = new Work();
    work.setTimeout(2000);
    work.setType(WorkType.SINGLE);
    return work;
  }
}
```
자바 설정을 사용한다면 @Bean 애노테이션을 가진 메서드에 @Scope 애노테이션을 적용해서 범위값을 지정해주면 된다.

```java
// [코드-20] : 프로토 타입에서 빈 객체 생성
Work work1 = ctx.getBean("workProto", Work.class);
Work work2 = ctx.getBean("workProto", Work.class);
Work work3 = ctx.getBean("workProto", Work.class);
//work1 == work2 false
//work2 == work3 false
//work1 == work3 false
```
프로토타입 범위를 가진 빈을 찾을 경우 스프링은 매번 새로운 객체를 생성해서 리턴한다. [코드-20]과 같이 getBean() 메서드로 프로토타입 범위를 가진 빈을 두 번 이상 구할 경우
스프링 컨테이너는 매번 새로운 객체를 생성하기 때문에 work1, work2, work3은 모두 다른 객체가 된다.


## 참고내용
- 서적 : 웹 개발자를 위한 Spring 4.0 프로그래밍 (최범균 저자)
- 참고예제소스 : https://github.com/madvirus/spring4











