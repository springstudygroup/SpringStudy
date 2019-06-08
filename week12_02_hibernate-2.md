# 레거시 스프링 시작하기



#### 코드랩 환경

- Mac, brew, iTerm2
- Visual Studio Code
- Maven, Tomcat

# 1. 배포하기

## 1.1. 톰캣설치

```bash
brew install tomcat

# result: /usr/local/Cellar/tomcat/9.0.20: 638 files, 14.6MB, built in 9 seconds
```

## 1.2. legacy-sample war 만들기

```bash
git clone https://github.com/keesun/legacy-sample
cd legacy-sample
mvn package
cd target
ls -al
```

![image](https://user-images.githubusercontent.com/33514304/59150259-9fd98300-8a5b-11e9-993d-2bc933a5a85f.png)

## 1.3. 배포하기

```bash
cp legacy-sample-0.0.1-SNAPSHOT.war /usr/local/Cellar/tomcat/9.0.20/libexec/webapps/legacy-sample.war
cd /usr/local/Cellar/tomcat/9.0.20/
./catalina start
```

## 1.4. 확인

![image](https://user-images.githubusercontent.com/33514304/59150262-c39cc900-8a5b-11e9-8c3c-3021868ab6de.png)




## 1.5 요약

```bash
mvn package
cp target/legacy-sample-0.0.1-SNAPSHOT.war /usr/local/Cellar/tomcat/9.0.20/libexec/webapps/legacy-sample.war
/usr/local/Cellar/tomcat/9.0.20/bin/catalina stop 
/usr/local/Cellar/tomcat/9.0.20/bin/catalina start
```

# 2. 하이버네이트

## 2.1 iBatis 걷어내기

아이바티스 걷어내기 1: embedded-database 설정 간소화 (applicationContext.xml)

> 하이버네이트가 제공하는 hibernate.hbm2ddl.auto 옵션을 update로 설정하였기 때문에 애플리케이션을 실행할 때 테이
> 블이 자동으로 생성됩니다.

아이바티스 걷어내기 2: transactionManager 교체하기 (applicationContext.xml)

> DataSourceTransactionManager -> HibernateTransactionManager
>
> p:sessionFactory ref="sessionFactory", 그런데 이 값은 사실 기본값이라서 AnnotationSessionFactoryBean을 sessionFatory라는 id를 가진 빈으로 등록했다 면, 위 설정처럼 명시적으로 sessionFactory-ref 설정을 추가할 필요가 없습니다.
>
> => 구라! 톰캣에서 오류를 발생시키니 꼭 적어주시기 바랍니다~!

![image](https://user-images.githubusercontent.com/33514304/59150283-dc0ce380-8a5b-11e9-9a1a-7c7830223a6e.png)

아이바티스 걷어내기 3: 도메인 클래스 맵핑 (Member.java)

> 이제 더 이상 SqlMapConfig.xml이나 Member.xml은 필요 없습니다. 그러한 설정 파일 대신 하이버네이트에서는 도메인 클래스에 애노테이션을 사용해서 맵핑 정보를 추가할 수 있습니다. 

아이바티스 걷어내기 4: MemberDaoHibernate로 DAO 교체 (MemberDaoIbatis.java, MemberDaoHibernate.java)

> MemberDaoIbatis에서 @Repository 애노테이션을 삭제합니다. 그럼 더 이상 빈으로 등록되지 않습니다. 대신 MemberDaoHibernate에 @Repository 애노테이션을 추가하여 MemberDaoHibernate가 빈으로 등록되도록 수정해 주세요.

## 2.2 SessionFactory

- openSession() :		 새로운 하이버네이트 Session을 만드는 것
- getCurrentSession() :	어디선가 이미 만들어 둔 Session을 가져오는 것.

> 스프링 트랜잭션을 사용할 계획이라면 스프링이 관리하는 Session을 사용하도록 getCurrentSession()을 사용해야 합니다. 

### Session을 가져왔다면, 본격적으로 데이터를 저장하거나 조회할 수 있습니다.

 (? 하이버네이트는 세션이 없으면 데이터를 저장하거나 조회할 수 없나요?)

```java
public class MemberDaoHibernate implements MemberDao{
	@Autowired SessionFactory sessionFactory;
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	public void add(Member member) {
	    getSession().save(member);
	}
	public void update(Member member) {
	    getSession().update(member);
	} 
}
```



## 2.3 회원 검색 기능 추가

### 검색폼을 추가 ( list.jsp )

```html
<h2>Member List</h2>
	<form action="/member/search">
		<input type="text" name="name"/>
		<input type="submit" value="search"/> 
	</form>
<ul>
```

### 핸들러 추가 ( MemberController.java )

```java
@RequestMapping(value = "/search")
public String search(String name, Model model) {
	model.addAttribute("list", memberService.listByName(name));
	return "member/list"; 
}
```

### 검색함수 추가 ( MemberService.java, MemberServiceImpl.java, MemberDao.java, MemberDaoHibernate.java )

```java
// MemberService.java
List<Member> listByName(String name);

// MemberServiceImpl.java
public List<Member> listByName(String name) {
	return dao.listByName(name);
}

// MemberDao.java
List<Member> listByName(String name);

// MemberDaoHibernate.java
@SuppressWarnings("unchecked")
public List<Member> listByName(String name) {
	return getCriteria()
	.add(Restrictions.like("name", name, MatchMode.ANYWHERE))
	.list();
}
```

### 책이 또 구라를?!

MemberDao 인터페이스를 구현하고 있는 MemberDaoIbatis.java에도 함수를 구현해줘야 오류가 안납니다..!

![image](https://user-images.githubusercontent.com/33514304/59150289-f5ae2b00-8a5b-11e9-84bb-4b08da140cda.png)

어차피 안쓰는데.. Member.sql 수정없이 함수 구현만..!

```java
// MemberDaoIbatis.java
@SuppressWarnings("unchecked")
public List<Member> listByName(String name) {
	return null;
}
```
