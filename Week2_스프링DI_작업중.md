스프링 DI를 이용한 객체 생성
=======================

목표 
-----------------------
#### 1. 스프링의 DI를 이해한다.
#### 2. 스프링 컨테이너의 역할을 이해한다.

## 1. DI(Dependency Injection)와 스프링
: 객체 자체가 아니라 Framework 의해 객체의 의존성이 주입되는 설계 패턴을 말한다.
객체가 어떤 의존성을 가지고 있으며, 어떡해 주입이 되는지 아래에 예제 코드를 보자.

- 1.1 DI(Dependency Injection) : 의존성 주입

```java
// [코드-1] : FilePrinter 객체는 BufferedReader 객체를 의존한다.
public class FilePrinter{
  public void print(String filePath) throws IOException{
   // 의존하는 타입을 로컬 변수(br 변수)로 정의
    try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
      String line = null;
      while((line = br.readLine()) != null){
        System.out.println(line);
      }
    }
  }
}
```
[코드-1] 예제를 보면 FilePrinter 객체가 print() 메서드를 사용하기 위해서 BufferedReader 객체를 생성하여 사용하는 것을 알 수 있다.
이렇게 기능을 실행하기 위해서 다른 객체(클래스)를 필요로 할 때 이를 의존(dependency)한다고 본다.

- 1.2 객체가 의존하는 방법 
  + 직접 생성
  ```java
  // [코드-2] : 의존 객체를 직접 생성하여 사용하는 방법 
  public class FileEncryptor {
    // 의존하는 타입의 객체를 직접 생성
    private Encryptor encryptor = new Encryptor();
  }
  
  public class Encryptor{
    public void encrypt(byte[] data, int offeset, int len){
      // 현재 메서드가 완성되지 않았으며, 테스트하기 위해서 익셉션 발생
      throws new UnsupportedOperationException(); 
    }
  }
  ```
  단점 : 개발 생산성이 낮아진다.
  응용프로그램은 소스가 한 두줄이 아니며, 하나의 클래스가 여러군데서 사용될 수 있다. 
  이러한 경우를 생각하면 [코드-2]에서 의존하는 객체를 직접 생성하는 방식은 변경 사항 발생하여 수정하는데 비용이 증가하고,
  개발 생산성이 낮아진다. 
  또, encrypt() 메서드가 UnsupportedOperationException을 발생하기 때문에 클래스가 Encryptor 클래스가 완성되기 전까지는 FileEncryptor 클래스를 테스트할 수 없게 된다.
  
  
  + 파라미터 전달
  ```java
  // [코드-3] : 의존 객체를 외부로부터 파라미터로 전달 받아 사용하는 방법
  public static void main(String[] args){
    Encryptor enc = enw Encryptor();  
    FileEncryptor fileEncryptor = new FileEncryptor(enc);
  }
  ```

- 1.3 DI를 사용하는 방식의 코드 : 의존 객체를 외부에서 조립함

  : 의존 객체를 직접 생성하는 방식과 달리 DI(Dependency Injection)는 의존 객체를 외부([코드-3])로부터 전달받는 구현 방식이다.
  
  ```java
  // [코드-4] : 생성자 파라미터를 외부로부터 Enctyptor 객체를 전달 받아 encryptor() 메서드에서 사용(DI) 
  public class FileEncryptor{
    private Encryptor encryptor;
    
    public FileEncryptor(Encryptor encryptor){
      // 생성자로 전달받은 객체를 필드에 할당
      this.encrypotr = encryptor;
    }
    
    public void encrypt(File src, File target) throws IOException{
    ...
    // DI 방식으로 전달받은 객체를 사용
    encryptor.encrypt(data, 0, len);
    ...
    }
  }
  ```
  [코드-4] 에서 encryptor() 메서드를 사용할 때, encryptor 객체가 필요한다 이 객체는 생성자 파라미터로 전달받게 된다. 
  이렇게 생성자로부터 객체가 생성될 때, 외부에서 필요한 객체를 전달 받아 사용할 수 있는데 이러한 것을 의존성 주입(DI) 또는 연결(wire)이라 한다.
  
  + 조립기
  : 객체를 생성하고 객체들을 서로 연결해주는 역할을 하는 것을 조립기라고 한다. 
  조립기는 의존 객체가 필요한 상황에서 객체를 생성해주고 연결(wire)해주는 일 DI를 역할을 하는데 이러한 역할을하는 Framework 가 스프링 컨테이너 이다.
  
  ```java
  // [코드-5] - 조립기 : 사용할 의존 객체를 생성하여 주입(연결)하는 역할
  public Assembler{
    private FileEncryptor fileEnc;
    private Encryptor enc;
    
    public Assembler(){
      enc = new Encryptor();
      fileEnc = new FileEncryptor(enc); // 사용할 객체를 생성 
    }
    
    // 필요한 객체를 전달하기 위한 메서드
    public FileEncryptor fileEncryptor(){
      return fileEnc;
    }
  }
  ```
  ```java
  // [코드-5-1] : [코드-5] 조립기를 통해서 의존성 주입
  public static void main(String[] args){
    // 조립기 생성 - 스프링 컨테이너 역할
    Assembler assembler = new Assembler();
    // 조립기에서 필요한 의존 객체를 생성하여 주입(DI)
    FileEncryptor fileEnc = assembler.fileEncryptor();
    // 의존 객체를 전달 받아 사용
    fileEnc.encrypt(srcFile, targetFiel);
  }
  ```
  조립기는 [코드-5-1] 처럼 의존성이 필요한 객체에서 의존 객체를 생성하여 주입하는 역할을 한다. 이처럼 앞으로 배울 스프링 컨테이너도 이러한 역할을 수행하게 된다.
  만약 FileEncryptor가 사용해야 할 Encryptor 객체가 FastEncryptor 객체로 변경된다면 조립기 Assembler 에서 코드를 FastEncryptor()로 변경하면 된다.
  이렇게 변경사항이 발생하더라도 변경되는 부분은 조립기로 제한되어 개발 생산성을 높일 수 있다.
  
  DI의 방식의 장점은 의존하는 클래스의 구현이 완성되지 않더라도 테스트를 할 수 있다.
  // TO-DO :   DI의 방식의 장점은 의존하는 클래스의 구현이 완성되지 않더라도 테스트를 할 수 있다.
  
- 1.4 생성자 방식과 프로퍼티 설정 방식
  + 생성자 방식
  ```java
  // [코드-6] : 객체를 외부로부터 생성자 파라미터로 전달 받음
  public class FileEncryptor{
    private Encryptor encryptor;
    
    public FileEncryptor(Encryptor encryptor){
      // 생성자로 전달받은 객체를 필드에 할당
      this.encrypotr = encryptor;
    }
  }
  ```
  + 프로퍼티 방식
  ```java
  // [코드-7] : 객체를 외부로부터 setter 메서드를 통해 프로퍼티에 전달 받음
  public class FileEncryptor{
    private Encryptor encryptor;
    
    // set 메서드를 통해서 의존 객체를 전달 받음
    public void setEncryptor(Encryptor encryptor){
      this.encryptor = encrypotr;
    }
  }
  ```
- 1.5 스프링은 객체를 생성하고 연결해주는 DI 컨테이너
: 스프링은 객체를 생성하고 각 객체를 연결해주는 조립기 역할을 한다.
```java
// [코드-8] : 스프링 컨테이너로 객체 생성
String configLocation = "classpath:applicationContext.xml";
AbstractApplicationContext ctx = new GenericXmlApplicationContext(configLocation);
Project project = ctx.getBean("sampleProject", Project.class);
project.build();
ctx.close();
```

여기서 GenericXmlApplicationContext가 조립기 기능을 구현한 클래스다. 조립기에서 생성할 객체가 무엇이고, 각 객체를 어떻게 연결하는지에 대한 정보는 XML 파일에 정의 되어 있다. GenericXmlApplicationContext 클래스는 이 XML 파일에 정의된 설정 정보를 읽어와 객체를 생성하고 각각의 객체를 연결한 뒤에 내부적으로 보관하는데, 이러한 생성된 객체를 보관하는 것을 스프링 객체 컨테이너(Object Container) 라고 부른다.


## 2. 스프링 컨테이너 종류
: 스프링은 컨테이너는 두 가지 종류의 컨테이너를 제공한다. 


- BeanFactory : 컨테이너에서 객체를 생성하고 DI를 처리해주는 기능만 제공
- ApplicationContext : 트랜잭션, 자바 코드 기반 스프링 설정, 애노테이션을 사용한 빈 설정, 스프링을 이용한 웹 개발, 메시지 처리 등 기능을 제공

여기서 살펴볼 스프링 컨테이너는 ApplicationContext 인터페이스를 구현한 클래스를 살펴 본다.
  + GenericXmlApplicationContext : XML 파일을 설정 정보로 사용하는 스프링 컨테이너. 독립형 어플리케이션 개발에 사용
  + AnnotationConfigApplicationContext : 자바 코드를 설정 정보로 사용하는 스프링 컨테이너. 독립형 어플리케이션 개발에 사용
  + GenericGroovyApplicationContext : 그루비 언어로 작성된 설정 정보를 사용하는 스프링 컨테이너. 독립형 어플리케이션 개발에 사용
  + XmlWebApplicationContext : 웹 어플리케이션을 개발할 때 사용하는 스프링 컨테이너. XML 파일을 설정 정보로 사용
  + AnnotationConfigWebApplicationContext : 웹 어플리케이션을 개발 할 때 사용하는 스프링 컨테이너. 자바 코드를 설정 정보로 사용

## 3. 스프링 DI 설정
-  


## 4. 팩토리 방식의 스프링 빈 설정
: XML 설정으로는 이 두가지 경우 즉, 정적 메서드를 이용해서 객체를 생성하는 경우와 객체 생성 과정이 다소 복잡한 경우를 처리할 수 없는데, 이런 경우를 위해 스프링은 다음과 같은 두 가지 방식의 객체 생성 방식을 제공한다.

- 4.1 객체 생성을 위한 정적 메서드 설정
```java
// [코드-9] : 정적 메서드(instance)를 사용하여 객체 생성
public abstract class ErpClientFactory {
	public static ErpClientFactory instance() {
		Properties props = new Properties();
		props.setProperty("server", "localhost");
		return instance(props);
	}

	public static ErpClientFactory instance(Properties props) {
		return new DefaultErpClientFactory(props);
	}

	protected ErpClientFactory() {
	}

	public abstract ErpClient create();
}

```

스프링은 기본적으로 객체를 생성할 때 생성자를 사용하기 때문에, 위와 같이 static 메서드를 이용해서 객체를 생성해야할 경우 <bean> 태그 안에 factory-method 속성을 추가로 설정한다.

```xml
<!-- [xml-1] : 정적 메서드를 사용하여 빈 객체 생성 -->
<bean id="factory" class="net.madvirus.spring4.chap02.erp.ErpClientFactory"
		factory-method="instance">
	<constructor-arg> <!-- static 메서드의 파라미터로 전달 -->
		<props>
			<prop key="server">10.50.0.101</prop>
		</props>
	</constructor-arg>
</bean>
```

[xml-1] 와 같이 factory-method 속성을 지정하면, 스프링은 ErpClientFactory 클래스의 생성자가 아닌 정적 메서드인 instance() 메서드를 이용해 빈 객체를 생성한다. 파라미터가 필요할 경우에는 <constructor-arg> 태그를 이용해서 필요한 값이나 빈 객체를 전달하면 된다.
  
- 4.2 FactoryBean 인터페이스를 이용한 객체 생성 처리 


## 5. 애노테이션을 이용한 객체 간 의존 자동 연결
: 스프링은 개발자가 일일이 의존 정보를 설정하지 않아도(예를 들어, XML에서 <ref> 태그를 사용하지 않아도) 자동으로 스프링 빈 객체 간의 의존을 설정해주는 기능을 제공하고 있는데, 이 기능을 사용하면 스프링 코드 설정을 짧게 유지할 수 있게 된다. 의존 자동 설정을 위해서 자주 사용되는 애노테이션을 살펴 본다.
  
  - 5.1 애노테이션 기반 의존 자동 연결 위한 설정
    + @Autowired : 의존 관계를 자동으로 설정
    + @Qualifiler : 두 개 이상의 동일한 빈 객체가 정의되어 생성될 경우 한정하여 연결해줄 객체를 생성하기 위한 설정
    + @Inject : DI 목적으로 만들어진 애노테이션 
    + @Resource : 이름을 기준으로 빈 객체를 자동으로 설정
    
    
```java
// [코드-10] : 애노테이션을 이용한 객체 간 의존 자동 연결     
public class OrderService {
  ...
  @Autowired
  private ErpClientFactory erpClientFactory;
  
  @Autowired
  public void init(ErpClientFactory erpClientFactory) {
    ...
  }
  ...
}
```

```xml
  <!-- [xml-2] : @Autowired 이용한 자동 연결 -->
  <bean id="factory" class="net.madvirus.spring4.chap02.erp.ErpClientFactory"
		factory-method="instance">
	<constructor-arg>
		<props>
			<prop key="server">10.50.0.101</prop>
		</props>
	</constructor-arg>
</bean>
```
 [코드-10]과 [xml-2]처럼 설정하고 @Autowired 사용하면 스프링은 자동으로 빈 객체를 생성하여 연결해 준다. @Autowired는 필드, 메서드, 생성자 등에서 사용 가능하다. @Autowired 속성 중에 required 속성이 있는데 이것은 주입 객체가 필수인지를 설정하는 속성(기본으로는 true)이다. 이 속성 값을 false로하면 객체가 정의되지 않아 생성하지 못하더라도 익셉션이 발생하지 않으며, 객체의 값이 null일 수도 있을 경우에 사용(@Autowired(required=false))한다. 

```java
// [코드-11] : 애노테이션을 이용한 객체 간 의존 자동 연결     
public class OrderService {
  ...
  @Autowired
  public void setSearchClientFactory(@Qualifier("order") SearchClientFactory searchClientFactory) {
	this.searchClientFactory = searchClientFactory;
  }
  ...
}
```
```xml
  <!-- [xml-3] : @Qualifier 이용한 자동 연결 -->
  <bean id="factory" class="net.madvirus.spring4.chap02.search.SearchClientFactoryBean">
    <qualifier value="order"/>
    ...
  </bean>
```
 
@Qualifiler 애노테이션은 빈을 정의할 때 빈의 한정하여 사용하도록 설정하는 기능이다. XML 설정에서 <qualifier> 태그를 이용해 한정자를 지정하여 사용한다.
  
```java
// [코드-12] : 애노테이션을 이용한 객체 간 의존 자동 연결     
@Component
public class ProductService {
	...
	@Resource(name = "productSearchClientFactory")
	public void setSearchClientFactory(SearchClientFactory searchClientFactory) {
		this.searchClientFactory = searchClientFactory;
	}
	...
}

```
```xml
  <!-- [xml-4] : @Resource 이용한 자동 연결 -->
 <bean id="productSearchClientFactory"
		class="net.madvirus.spring4.chap02.search.SearchClientFactoryBean">
	<property name="server" value="10.20.30.41" />
	<property name="port" value="9999" />
	<property name="contentType" value="json" />
 </bean>

```  
 
@Resource 애노테이션은 이름을 기준으로 빈 객체를 선택하여 자동으로 설정 해준다. [xml-4]에 name 속성 값을 [코드-12]에서 @Resource 애노테이션의 속성 값으로 사용한다. @Resource 애노테이션은 name 속성으로 정의된 객체가 없거나 두 개 이상이면 익셉션을 발생시키며, name 속성을 지정하지 않을 경우에는 @Autowired 애노테이션 기능과 동일하게 일치하는 타입으로 스프링 빈을 선택하여 설정 해준다.

```java
// [코드-13] : 애노테이션을 이용한 객체 간 의존 자동 연결     
@Inject
public void setErpClientFactory(ErpClientFactory erpClientFactory) {
	this.erpClientFactory = erpClientFactory;
}

@Inject
public void setClientFactory(@Named("orderSearchClientFactory") SearchClientFactory searchClientFactory){
	this.searchClientFactory = searchClientFactory;
}
```
@Inject 애노테이션은 @Autowired 애노테이션과 마찬가지로 필드, 메서드, 생성자에 적용할 수 있다.
[코드-13] 두 번째 @Inject 애노테이션의 적용된 메서드의 파라미터에 @Named 애노테이션을 적용했다. @Named 애노테이션은 자동 설정 대상이 두 개 이상일 경우 특정한 빈을 선택할 목적으로 사용된다는 점에서 @Qualifier 애노테이션과 유사하다. 차이점은 @Named 애노테이션은 사용할 빈의 이름을 지정한다는 점이다.

    
## 6. 컴포넌트 스캔을 이용한 빈 자동 등록
: 특정 패키지에 위치한 클래스를 스프링 빈으로 자동으로 등록하고 의존 자동 설정을 통해서 각 빈 간의 의존을 처리해 주는 기능을 컴포넌트 스캔이라 한다.

- 6.1 자동 검색된 빈의 이름과 범위
```xml
<!-- [xml-5] : 패키지 경로에 객체들을 스캔(하위 패키지 포함)하여 스프링 빈으로 등록 및 각 빈 간의 의존을 처리 -->
<context:component-scan base-package="net.madvirus.spring4.chap02.shop">
</context:component-scan>
```

[xml-5]에서와 같이 설정 파일에 <context:component-sca> 태그를 사용하여 스캔할 패키지 경로를 적어 준다. 그러면 스프링 컨테이너가 해당 경로에 하위 폴더까지 포함하여 모든 클래스들을 읽어 들여 @ 설정을 통해 스프링 빈으로 등록하고 각 빈 간의 의존을 처리한다.
스프링은 기본적으로 검색된 클래스를 빈으로 등록할 때, 클래스의(첫 글자를 소문자로 바꾼) 이름을 빈 이름으로 사용한다.

```java
// [코드-14] : ProductService는 @Component 애노테이션 설정으로 스프링 빈 대상이므로, 컴포넌트 스캔 시 빈 이름을 productService로 지정 된다.
@Component
public class ProductService{
...
}
```

- 6.2 스캔 대상 클래스 범위 지정하기
: <context:component-scan> 태그를 사용할 때, 스캔 대상에 포함시킬 클래스와 제외시킬 클래스를 구체적으로 명시할 수 있다.

```xml
<!-- [xml-6] : 스캔 시 클래스 포함 설정 -->
<context:component-scan base-package="net.madvirus.spring4.chap02.shop">
	<context:include-filter type="regex" expression="*.Service"/>
	<context:exclude-filter type="aspectj" expression="net..*Dao"/>
</context:component-scan>
```

[xml-6]에서 컴포넌트 스캔 시 포함할 클래스가 있다면 <context:include-filter>를 사용하고, 제외할 클래스가 있다면 <context:exclude-filter>를 사용한다.
이 두 개의 태그는 type 속성에 따라 expressioin 속성에 올 수 있는 값이 달라진다. 

[표 - 1 ] : type 속성에 따라 expressioin 속성표

|type 속성|설명|
|------|------|
|annotation|클래스에 지정한 애노테이션이 적용됐는지 여부, expression 속성에는 "org.example.SomeAnnotation"와 같이 애노테이션 이름을 입력한다.|
|assignable|클래스가 지정한 타입으로 할당 가능한지의 여부. expression 속성에는 "org.example.SomeClass"와 같이 타입 이름을 입력한다.|
|regex|클래스 이름이 정규 표현식에 매칭되는지의 여부. expression 속성에는 "org\.example\.Deault.*"와 같이 정규 표현식을 입력한다.|
|aspectj|클래스 이름이 AspectJ의 표현식에 매칭되는지의 여부. expression 속성에는 "org.example..*Service+"와 같이 AspectJ의 표현식을 입력한다.|

## 7. 스프링 컨테이너 추가 설명
: 지금까지는 ApplicationContext가 제공하는 메서드 중에 getBean() 메서드만 주로 사용했는데, 여기서는 getBean() 메서드를 포함해서 ApplicationContext가 제공하는 기능 및 스프링 컨테이넝 대한 추가적인 내용을 설명한다.

- 7.1 컨테이너 빈 객체 구하기 위한 기본 메서드
  + BeanFactory
  
    * <T> T getBean(String name, Class<T> requiredType)
      : 이름이 name이고, 타입이 requiredType인 빈을 구한다. 일치하는 빈이 존재하지 않을 경우 NoSuchBeanDefinitionException이 발생한다.
    * <T> T getBean(Class<T> rquiredType)
      : 타입이 requiredType인 빈을 구한다. 일치하는 타입을 가진 빈이 존재하지 않을 경우 NoSuchBeanDefinitionException이 발생하고, 같은 타입의 빈이 두 개 이상일 경우 NoUniqueBeanDefinitionException이 발생한다.
    * boolean containsBean(String name)
      : 지정한 이름을 가진 빈이 존재할 경우 true를 리턴한다.
    * boolean isTypeMatch(String name, Class<?> targetType)
      : 지정한 이름을 가진 빈의 타입이 targetType인 경우 true를 리턴한다. 해당 이름을 가진 빈이 존재하지 않을 경우 NoSuchBeanDefinitionException이 발생한다.
    = Class<?> getType(String name) 
      : 이름 name인 빈의 타이블 구한다. 해당 이름을 가진 빈이 존재하지 않을 경우 NoSuchBeanDefinitionExcption이 발생한다.

- 7.2 스프링 컨테이너의 생성과 종료
: 스프링 컨테이너는 주기를 갖는다.

1. 컨테이너 생성
2. 빈 메타 정보(XML이나 자바 기반 설정)를 이용해서 빈 객체 생성
3. 컨테이너 사용
4. 컨테이너 종료(빈 객체가 제거)

1번과 2번 과정은 컨테이너를 생성할 때 함께 진행된다.
```java
// [코드-15] : 메타 정보(xml)를 읽어 빈 객체 생성하여 스프링 컨테이너 생성
GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:config.xml");
```
또는 컨테이너를 먼저 생성 후 메타 정보를 컨테이너에게 제공할 수도 있다.
```java
// [코드-16] : 스프링 컨테이너 생성 후 메타 정보를 제공
// 스프링 컨테이너 생성
GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
// 메타 정보 제공 
ctx.load("classpath:config.xml");
// 빈 객체 생성(읽어 온 메타 정보로 빈 객체재생성)
ctx.refresh();
// 컨테이너 종료
ctx.close();
```
컨테이너를 생성 후 메타정를 제공할 때 refresh() 메서드를 호출하여 빈 객체를 초기화해야만 익셉션을 방지할 수 있다.

- 7.3 스프링 컨테이너 계층 구조
: 스프링 컨테이너는 부모-자식 관계의 계층 구조를 가질 수 있다. 
자식에 속하는 컨테이너 빈은 부모 컨테이너에 속한 빈을 참조할 수 있다. 즉, 의존 객체로 사용할 수 있다. 하지만 부모 컨테이너에 속한 빈은 자식 컨테이너에 속한 빈을 참조할 수 없다.

```java
// [코드-17] : 부모-자식 관계 설정
GenericXmlApplicationContext parent = new GenericXmlApplicationContext("classpath:parent-xml");
GenericXmlApplicationContext child = new GenericXmlApplicationContext();
child.setParent(parent);
child.load("classpath:child-xml");
child.refresh();
```

```xml
<!-- [xml-7] : 부모-자식 관계 
-- conf-parent.xml
<bean id="svc" class="net.some.svc.Svc">
</bean>

-- conf-child.xml
<bean id="ui" class="net.some.ui.UI">
	<property name="service" ref="svc"/> <!-- 부모 컨테이너의 svc 빈을 참조 -->
</bean>
```
[코드-17]에서는 setParent() 메서드를 이용하여 부모-자식 관계를 설정하였다. 여기서 자식 컨테이너의 빈을 초기화하기 전에 부모 컨테이너를 지정해야만 익셉션을 방지할 수 있다. 
부모-자식 관계 설정은 일반적으로 컨테이너 계층 구조를 사용할 일이 많지 않지만, 서로 다른 두 개의 컨테이너에서 공통 기능을 필요로 할 때 계층 구조를 유용하게 쓸 수 있다. 예를 들어, 메일 발송, 메시지 전송 등의 공통 기능을 제공하는 빈을 부모 컨테이너에 생성하고, 여러 자식 컨테이너에서 이들 빈을 사용해서 기능을 사용할도록 구성할 수 있을 것이다.

## 참고내용
- 서적 : 웹 개발자를 위한 Spring 4.0 프로그래밍 (최범균 저자)
- 블로그 : https://gmlwjd9405.github.io/2018/11/09/dependency-injection.html










