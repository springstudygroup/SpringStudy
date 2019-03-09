스프링 DI를 이용한 객체 생성
=======================
## 1. DI(Dependency Injection)와 스프링
설명 : 
- 1.1 DI(Dependency Injection) : 의존성 주입

: 객체 자체가 아니라 Framework 의해 객체의 의존성이 주입되는 설계 패턴을 말한다.
객체가 어떤 의존성을 가지고 있으며, 어떡해 주입이 되는지 아래에 예제 코드를 보자.

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
String configLocation = "classpath:applicationContext.xml";
AbstractApplicationContext ctx = new GenericXmlApplicationContext(configLocation);
Project project = ctx.getBean("sampleProject", Project.class);
project.build();
ctx.close();
```

여기서 GenericXmlApplicationContext가 조립기 기능을 구현한 클래ㅅ다. 조립기에서 생성할 객체가 무엇이고, 각 객체를 어떻게 연결하는지에 대한 정보는 XML 파일에 정의 되어 있다. GenericXmlApplicationContext 클래스는 이 XML 파일에 정의된 설정 정보를 읽어와 객체를 생성하고 각각의 객체를 연결한 뒤에 내부적으로 보관하는데, 이러한 생성된 객체를 보관하는 것을 스프링 객체 컨테이너(Object Container) 라고 부른다.


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
- 4.1 객체 생성을 위한 정적 메서드 설정 


## 5. 애노테이션을 이용한 객체 간 의존 자동 연결

## 6. 컴포넌트 스캔을 이용한 빈 자동 등록

## 7. 스프링 컨테이너 추가 설명

