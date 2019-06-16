# Chaper 04 Environment, 프로퍼티, 프로필, 메시지
## 01 Environment 소개
Environment는 외부에서 입력한 정보로 설정 값을 변경하는 방법 중 하나이다. 동일한 코드를 사용하면서 일부 정보만 바꾸기를 원할 경우 사용할 수 있다.
Environment는 프로퍼티를 PropertySource라는 것으로 통합 관리한다. 따라서 설정 파일이나 클래스 수정없이 이 시스템 프로퍼티나 프로퍼티 파일 등을 이용해서 설정 정보의 일부를 변경할 수 있다.
또한, 여러 프로필(profile) 중에서 특정 프로필을 활성화하는 기능을 제공한다. 덕분에 서비스 환경에 따라 다른 스프링 빈 설정을 선택하여 서로 다른 환경을 위한 설정 정보를 편리하게 관리할 수 있다.
### 1.1 Environment 구하기
Environment에 새로운 PropertySource를 직접 추가할 때 getEnvironment() 메서드를 이용해 Environmet를 구할 수 있다.
```java
import org.springframework.core.env.ConfigurableEnvironment;
...
ConfigurableApplicationContext context = new AnnotationConfigApplicationContext();
ConfigurableEnvironment environment = context.getEnvironment();
environment.setActiveProfiles("dev");
```
ConfigurableEnvironment 타입은 사용할 프로필을 선택하는 기능과 PropertySource를 추가하는데 필요한 기능을 제공한다.
## 02 Environmet와 PropertySource
Environment는 프로퍼티 값을 제공하는 기능을 가진다. Environment 구현체는 여러 PropertySource로부터 값을 읽어온다. 그 과정은 [그림-1]과 같다.
![pic_1](./img/그림-1.png)
[그림-1] Environment의 프로퍼티 읽는 과정
스프링은 기본적으로 시스템 프로퍼티와 환경 변수를 사용하는 두 개의 PropertySource를 사용한다. 우선순위는 시스템 프로퍼티를 사용하는 PropertySource가 높다.
### 2.1 Environment에서 프로퍼티 읽기
Environment를 구현한 뒤 Environment가 제공하는 프로퍼티 관련 메서드를 이용하여 프로퍼티를 읽는다. 다음은 이름이 "java.version"인 프로퍼티의 값을 구하는 코드를 보여준다.
```java
ConfigurableApplicationContext context = new GenericXmlApplicationContext();
ConfigurableEnvironment env = context.getEnvironment();
String javaVersion = env.getProperty("java.version");
system.out.printf("Java version is %s", javaVersion);
```
Environment는 시스템 프로퍼티에서 값을 가져온다. "java.version"은 시스템 프로퍼티로서 사용중인 자바 버전을 값으로 갖는다. 자바 1.8 버전에서 위 코드를 실행하면 다음과 같은 결과가 출력된다.
```
Java version 1.8
```
Environment가 제공하는 프로퍼티 관련 주요 메서드는 다음과 같다.
* boolean containsProperty(String key) : 지정한 key에 해당하는 프로퍼티가 존재하는지 확인한다.
* String getProperty(Stirng key) : 지정한 key에 해당하는 프로퍼티 값을 구한다. 존재하지 않으면 null을 리턴한다.
* String getProperty(String key, String defaultValue) : 지정한 key에 해당하는 프로퍼티 값을 구한다. 존재하지 않으면 defaultValue를 리턴한다.
* String getRequiredProperty(String key) throws IllegalStateException : 지정한 key에 해당하는 프로퍼티 값을 구한다. 존재하지 않으면 익셉션을 발생시킨다.
* <T> T getProperty(Stirng key, Class<T> targetType) : 지정한 key에 해당하는 프로퍼티의 값을 targetType으로 변환해서 구한다. 존재하지 않을 경우 null을 리턴한다.
* <T> T getProperty(String key, Class<T> targetType, T defaultValue) : 지정한 key에 해당하는 프로퍼티의 값을 targetType으로 변환해서 구한다. 존재하지 않을 경우 defaultValue를 리턴한다.
* <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException : 지정한 key에 해당하는 프로퍼티의 값을 targetType으로 변환해서 구한다. 존재하지 않을 경우 익셉션을 발생시킨다.
### 2.2 Environment에 새로운 PropertySource 추가하기
어떤 파일에 있는 프로퍼티를 Environment의 프로퍼티로 사용하고 싶다면 적절한 PropertySource를 추가해야 한다. 스프링은 기본적으로 두 개의 PropertySource를 사용한다. 메서드를 사용하여 이 밖의 특정 프로퍼티 파일을 사용하는 PropertySource를 Environment에 추가할 수 있다.
클래스패스에 위차한 db.properties 파일을 다음과 같이 작성했다고 하자.
```
db.driver=com.mysql.jdbc.Driver
db.jdbcUrl=jdbc:mysql://host/test
db.user=madvirus
db.password=bkchoi
```
이 파일을 사용하는 PropertySource를 Environment에 추가하는 예제코드이다.
```java
ConfigurableEnvironment env = context.getEnvironment();
MutablePropertySources propertySources = env.getPropertySources();
propertySources.addLast(new Resource PropertySource("classpath:/db.properties"));
System dbUser = env.getProperty("db.user"); // madvirus return
```
addLast()대신 addFirst() 메서드를 사용하면 첫 번째 PropertySource가 되어 가장 높은 우선순위를 갖는다.

@Configuration 애노테이션 기반 자바 설정을 사용하고 있다면, 애노테이션을 이용하여 프로퍼티 파일의 내용을 PropertySource에 추가할 수 있다.
```java
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/db.properties")
public class ConfigByEnv{

	@Autowired
	private Environment env;
```
두 개 이상의 프로퍼티 파일을 사용하려면 @PropertySource의 값을 배열로 지정하면 된다. 자원이 없는 경우 익셉션을 발생하지 않고 무시하고 싶다면 ignoreResourceNotFound 속성을 true로 지정한다. (ignoreResourceNotFound 속성의 기본값은 false이다.)
```java
@PropertySource(
	value={"classpath:/db.properties", "classpath:/app.ptoperties"},
	ignoreResourceNotFound=true
)
public class ConfigByEnv{
	...
```
@PropertySource 자체를 두 개 이상 설정할 때에는 @PropertySources 애노테이션을 사용하면 된다.
```java
@PropertySources({
	@PropertySource("classpath:/db.properties"),
	@PropertySource(value="classpath:/app.properties", ignoreResourceNotFound)
})
public class ConfigByEnv{
	...
```
@PropertySource 애노테이션은 자바 8의 @Repeatable을 적용하고 있으므로, 자바 8을 사용하면 다음과 같이 @PropertySource 애노테이션을 여러 번 사용해도 된다.
```java
@PropertySource("classpath:/db.properties"),
@PropertySource(value="classpath:/app.properties", ignoreResourceNotFound = true)
public class ConfigByEnv{
	...
```
## 03 Environment를 스프링 빈에서 사용하기
스프링 빈은 Environment에 직접 접근하여 Environment가 제공하는 프로퍼티를 사용할 수 있다. 접근 방법에는 두 가지가 있다.
* o.s.context.EnvironmentAware 인터페이스 구현
* @Autowired 애노테이션을 Environment 필드에 적용
EnvironmentAware 인터페이스는 다음과 같이 정의되어 있다.
```java
package org.springframework.context;

import org.springframework.bean.factory.Aware;
import org.springframework.core.env.Environment;

public interface EnvironmentAware extends Aware {
	void setEnvironment(Environment environment);
}
```
스프링 빈 객체가 EnvironmentAware 인터페이스를 구현하고 있을 경우, 스프링 컨테이너는 빈 객체를 생성한 뒤에 빈 객체의 setEnvironment() 메서드를 호출해서 컨테이너가 사용하는 Environment 객체를 전달한다. 따라서 EnvironmentAware 인터페이스를 구현한 스프링 빈은 아래 코드처럼 setEnvironment() 메서드에서 전달받은 Environment 객체를 필드에 보관하고, Environment의 프로퍼티가 필요한 곳에서 이 필드를 사용하면 된다.
```java
public class ConnectionProvider implements EnvironmentAware {

	private String driver;
	private String user;
	private String password;
	private String url;
	private Environment env;
	
	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}
	
	public void init() {
		driver = env.getProperty("db.driver");
		url = env.getProperty("db.jdbcUrl");
		user = env.getProperty("db.user");
		password = env.getProperty("db.password");
	}
	...
```
애노테이션 기반 의존 설정 기능이 활성화되어 있다면, 다음과 같이 @Autowired 애노테이션을 이용해서 Environment에 접근할 수 있다.
```java
public class ConnectionProvider {
	@Autowired
	private Environment environment;
	
	public void init() {
		driver = env.getProperty("db.driver");
		url = env.getProperty("db.jdbcUrl");
		user = env.getProperty("db.user");
		password = env.getProperty("db.password");
	}
	...
```
## 04 프로퍼티 파일을 이용한 프로퍼티 설정
프로퍼티나 생성자 값을 설정하기 위해 Environment를 직접 사용하는 경우는 거의 없다. 스프링은 외부의 프로퍼티 파일을 이용해 스프링 빈을 설정하는 방법을 제공한다. 앞서 언급했던 db.properties 파일을 다시 보자.
```
db.driver=com.mysql.jdbc.Driver
db.jdbcUrl=jdbc:mysql://host/test
db.user=madvirus
db.password=bkchoi
```
이 프로퍼티 파일의 내용을 XML과 자바 설정에서 Environment 없이 직접 사용하는 방법을 살펴보자.
### 4.1 XML에서의 프로퍼티 설정 : <context:property-placeholder> 사용
다음은 XML 설정에서 프로퍼티 파일을 이용하는 방법이다.
```xml
<beans ...생략>

	<context:property-placeholder location="classpath:/db.properties" />
	
	<bean id="connProvider"
		class-"net.madvirus.spring4.chap04.ConnectionProvider"
		init-method="init">
		<property name="driver" value="${db.driver}" />
		<property name="url" value="${db.jdbcUrl}" />
		<property name="user" value="${db.user}" />
		<property name="password">
			<value>${db.password}</value>
		</property>
	</bean>
	

</beans>
```
<context:property-placeholder> 태그는 location 속성으로 지정한 프로퍼티 파일로부터 정보를 읽어와 빈 설정에 입력한 플레이스홀더의 값을 프로퍼티 파일에 존재하는 값으로 변경한다. 따라서 위 코드는 다음과 같은 XML 설정을 사용한 것과 같은 결과를 만든다.
```xml
<beans ...생략>

	<context:property-placeholder location="classpath:/db.properties" />
	
	<bean id="connProvider"
		class-"net.madvirus.spring4.chap04.ConnectionProvider"
		init-method="init">
		<property name="driver" value="com.mysql.jdbc.Driver" />
		<property name="url" value="jdbc:mysql://host/test" />
		<property name="user" value="madvirus" />
		<property name="password">
			<value>bkchoi</value>
		</property>
	</bean>
	

</beans>
```
<context:property-placeholder> 태그는 내부적으로 PropertySourcesPlaceholderConfigurer를 bean으로 등록한다. PropertySourcesPlaceholderConfigurer는 location으로 지정한 파일에서 값을 찾을 수 없는 경우 Environment의 프로퍼티를 확인한다.
전체 설정에서 이 태그를 두 번 이상 사용할 경우 첫 번째로 사용한 태그의 값이 우선순위를 갖는다. 서로 다른 n개의 XML 설정에서 서로 다른 프로퍼티 파일을 사용하면 그 중 딱 한 개만 읽어온다. 이런 문제가 있어 다수의 XML 파일에서 사용되는 프로퍼티 파일이 두 개 이상이라면 별도의 XML 파일에서 태그를 설정하고 다른 XML 파일에서 플레이스홀더를 사용하도록 구성하는 것이 좋다.
### 4.2 Configuration 애노테이션을 이용하는 자바 설정에서의 프로퍼티 사용
Configuration을 사용하는 자바 설정에서 프로퍼티 파일을 사용하고 싶을 때는 PropertySourcesPlaceholderConfigurer와 @Value 애노테이션을 함께 사용한다. @Value 애토네이션이 플레이스홀더를 값으로 가질 경우 PropertySourcesPlaceholderConfigurer는 값을 프로퍼티의 값으로 치환한다.
```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class ConfigByProp {

	@Value("${db.driver}")
	private String driver;
	@Value("${db.jdbcUrl}")
	private String jdbcUrl;
	@Value("${db.user}")
	private String user;
	@Value("${db.password}")
	private String password;

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
		configurer.setLocation(new ClassPathResource("db.properties"));
		return configurer;
	}

	@Bean(initMethod = "init")
	public ConnectionProvider connectionProvider() {
		JdbcConnectionProvider connectionProvider = new JdbcConnectionProvider();
		connectionProvider.setDriver(driver);
		connectionProvider.setUrl(jdbcUrl);
		connectionProvider.setUser(user);
		connectionProvider.setPassword(password);
		return connectionProvider;
	}
}
```
## 05 프로필을 이용한 설정
여러 환경이 있을 때 각 환경에 맞는 설정 정보를 따로 만들어 알맞은 설정 정보를 사용하기 위해 프로필을 이용한다. 컨테이너를 생성할 때 적절한 프로필을 선택할 수 있다.
### 5.1 XML 설정에서 프로필 사용하기
XML 설정에서 프로필을 사용하려면 <beans> 태그의 profile 속성으로 이름을 지정해야 한다. 예를 들어 다음과 같이 테스트 환경과 실제 제품 환경에 대한 DB 연결 설정을 별도 파일로 작성하고, 각각의 파일에 profile 속성 값을 준다.
```xml
-- confprofile.datasource-dev.xml 파일이라 가정
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
    profile="dev">

	<bean id="connProvider" class="net.madvirus.spring4.chap04.JdbcConnectionProvider"
		init-method="init">
		<property name="driver" value="${db.driver}" />
		<property name="url" value="${db.jdbcUrl}" />
		<property name="user" value="${db.user}" />
		<property name="password">
			<value>${db.password}</value>
		</property>
	</bean>

</beans>

-- confprofile.datasource-prod.xml 파일이라 가정
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans.xsd"
    profile="prod">

	<bean id="connProvider" class="net.madvirus.spring4.chap04.JndiConnectionProvider">
		<property name="jndiName" value="java:/comp/env/jdbc/db" />
	</bean>

</beans>
```
위 설정을 보면 각 설정 파일이 같은 이름의 빈 객체를 설정하고 있는 것을 알 수 있고, <beans> 태그의 profile 속성에 각각 "dev"와 "prod" 값을 갖고있다.
XML 파일이 profile 속성을 갖고 있을 경우, 그 파일은 해당 프로필을 선택한 경우에만 사용된다. 특정 프로필을 선택하려면 ConfigurableEnvironment에 정의되어있는 setActiveProfiles() 메서드를 사용하면 된다.
XML 설정을 사용할 경우 프로필에 해당하는 <beans> 태그를 중첩하여 사용할 수 있다. 이 경우 같은 목적을 위해 사용하는 bean 설정을 XML 파일에 모을 수 있어 관리가 쉽다.
### 5.2 자바 @Configuration 설정에서 프로필 사용하기
@Profile 애노테이션을 이용하여 프로필을 지정할 수 있다. 활성화 방법은 XML 설정에서와 마찬가지로 메서드를 이용하거나 시스템 프로퍼티에 값을 설정하는 것이 있다.
### 5.3 다수 프로필 설정
스프링 설정은 두 개 이상의 프로필 이름을 가질 수 있다. 특정 프로필이 사용되지 않을 때 기본으로 사용될 설정을 지정하는 경우 "!프로필" 형식을 사용할 수 있다.
##06 MessageSource를 이용한 메시지 국제화 처리
MessageSource 인터페이스에는 지역 및 언어에 따라 알맞은 메시지를 구할 수 있는 getMessage() 메서드가 정의되어있다. getMassage() 메서드는 locale에 따라 알맞은 언어를 기준으로 메시지를 읽어온다.
ApplicationContext는 등록된 빈 객체 중에서 이름이 'messageSource'인 객체를 이용해 메시지를 가져온다. 따라서 ApplicationContext를 이용해 메시지를 가져오려면 스프링 설정 파일에 이름이 messageSource인 빈 객체를 정의해야 한다.
### 6.1 프로퍼티 파일과 MessageSource
MessageSource 인터페이스에서 주로 사용하는 메서드는 다음의 두 가지이다.
* String getMessage(String code, Object[] arg, String defaultMessage, Locale locale)
* String getMessage(String ccode, Object[] args, Locale locale)
여기서 code는 메시지를 식별하기 위해 사용하는 code이다. 이 값은 메시지로 사용될 프로퍼티 파일의 프로퍼티 이름과 연결된다.
args 파라미터는 메시지에 플레이스홀더의 값을 지정할 때 사용한다. 플레이스홀더의 숫자는 메서드에 전달되는 args 파라미터의 인덱스이다.
```
welcome={0}님, 환영합니다.
```
```java
String[] args = {"정도전"}
String welcome = messageSource.getMessage("welcome", args, Locale.getDefault());
```
실제 스프링이 어떤 위치의 프로퍼티 파일을 사용하는가는 인터페이스를 구현한 클래스에 따라 다르다. 스프링이 기본으로 제공하는 두 개의 구현 클래스의 사용법을 살펴본다.
### 6.2 ResourceBundleMessageSource를 이용한 설정
ResourceBundleMessageSource 클래스는 MessageSource 인터페이스의 구현 클래스로서 java.util.ResourceBundle을 이용해서 메시지를 읽어온다. basename 프로퍼티의 값은 메시지를 로딩할 때 사용할 ResourceBundle의 베이스 이름을 의미한다. 베이스 이름은 패키지를 포함한 완전한 이름이어야 한다. 따라서 basename 프로퍼티의 값이 message.greeting일 경우, message 패키지에 위치한 greeting 프로퍼티 파일로부터 메시지를 ㅏ져온다는 것을 의미한다.
```xml
<bean id="messageSource"
		class="org.springframework.context.support.ResourceBundleMessageSource">
		<property name="basenames">
			<list>
				<value>message.greeting</value>
				<value>message.error</value>
			</list>
		</property>
	</bean>
```
java 6 버전부터는 defaultEncoding 프로퍼티의 값을 "UTF-8"로 지정하면 유니코드를 입력할 필요 없이 UTF-8로 작성된 메시지 파일을 올바르게 읽어온다.
```xml
<bean id="messageSource"
		class="org.springframework.context.support.ResourceBundleMessageSource">
		<property name="basenames">
			<list>
				<value>message.greeting</value>
				<value>message.error</value>
			</list>
		</property>
		<property name="defaultEncoding" value="UTF-8" />
	</bean>
```
스프링의 ApplicationContext는 MessageSource 인터페이스를 상속하고 있으며, 스프링 빈 중에서 이름이 'messageSource'인 MessageSource가 있는 경우 이를 이용해 메시지를 처리한다.
### 6.3 ReloadableResourceBundleMessageSource를 이용한 설정
ResourceBundleMessageSource 방식의 단점인 메시지 파일을 클래스 패스 외에 다른 곳에 위치시키지 못하는 것, 한 번 메시지 파일을 읽어오면 파일이 변경돼도 변경사항이 반영되지 않는다는 단점을 보완한다.
ReloadableResourceBundleMessageSource는 클래스패스뿐만 아니라 특정 디렉토리의 파일을 사용할 수 있고, 클래스패스 자원이 아닌 경우 파일 내용이 변경되었을 때 그 내용이 반영된다.
설정 방법은 ResourceBundleMessageSource와 유사하나 basenames를 지정할 때 스프링의 자원 경로를 지정한다.
```xml
<bean id="messageSource"
		class="org.springframework.context.support.ReloadableResourceBundleMessageSource">
		<property name="basenames">
			<list>
				<value>file:src/message.greeting</value>
				<value>classpath:message.error</value>
			</list>
		</property>
		<property name="defaultEncoding" value="UTF-8" />
		<property name="cacheSeconds" value="10" />
	</bean>
```
cacheSeconds 프로퍼티는 캐싱할 시간을 초단위로 기록하여 주기적으로 메시지의 변경을 반영한다.
### 6.4 bean 객체에서 메시지 이용하기
빈 객체에서 스프링이 제공하는 MessageSource를 사용하려면 다음의 두 가지 방법 중 한 가지르 사용하면 된다.
* ApplicationContextAware 인터페이스를 구현한 뒤, setApplicationContext() 메서드를 통해 전달받은 ApplicationContext의 getMassage() 메서드를 이용하여 메시지 사용
* MessageSourceAware 인터페이스를 구현한 뒤, setMessageSource() 메서드를 통해 전달받은 MessageSource의 getMessage() 메서드를 이용하여 메시지를 사용
