
Chapter 07 스프링 MVC : 기본기 

01 스프링 MVC 일단 해보기 
02 기본 흐름과 주요 컴포넌트 
03 스프링 MVC 설정 기초 
04 컨트롤러 구현 
05 커맨드 객체 값 검증과 에러 메시지 
06 요청 파라미터의 값 변환 처리 
07 HTTP 세션 사용하기 

## Spring - MVC Framework Overview


The Spring Web MVC framework provides a **model-view-controller architecture** and ready components that can be used to develop flexible and loosely coupled web applications. The MVC pattern results in separating the different aspects of the application (input logic, business logic, and UI logic), while providing **a loose coupling** between these elements.

- The **Model** encapsulates the application data and in general, they will consist of POJO.

- The **View** is responsible for rendering the model data and in general, it generates HTML output that the client's browser can interpret.

- The **Controller** is responsible for processing User Requests and Building Appropriate Model and passes it to the view for rendering.

### The DispatcherServlet

The Spring Web model-view-controller (MVC) framework is designed around a **DispatcherServlet** that handles all the HTTP requests and responses. The request processing workflow of the Spring Web MVC DispatcherServlet is shown in the following illustration.

![image](https://user-images.githubusercontent.com/33514304/56087161-3fd1c080-5ea0-11e9-815f-582825a92f28.png)
Spring DispatcherServlet


Following is the sequence of events corresponding to an incoming HTTP request to DispatcherServlet −

After receiving an HTTP request, DispatcherServlet consults the HandlerMapping to call the appropriate Controller.

The Controller takes the request and calls the appropriate service methods based on used GET or POST method. The service method will set model data based on defined business logic and returns view name to the DispatcherServlet.

The DispatcherServlet will take help from ViewResolver to pick up the defined view for the request.

Once view is finalized, The DispatcherServlet passes the model data to the view, which is finally rendered, on the browsers.

All the above-mentioned components, i.e. **HandlerMapping**, **Controller** and **ViewResolver** are parts of **WebApplicationContext**, which is an extension of the plain **ApplicationContext** with some extra features necessary for web applications.

### Required Configuration

We need to map requests that you want the DispatcherServlet to handle, by using a URL mapping in the **web.xml** file. The following is an example to show declaration and mapping for HelloWeb DispatcherServlet −

```xml
<web-app id = "WebApp_ID" version = "2.4"
   xmlns = "http://java.sun.com/xml/ns/j2ee" 
   xmlns:xsi = "http://www.w3.org/2001/XMLSchema-instance"
   xsi:schemaLocation = "http://java.sun.com/xml/ns/j2ee 
   http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd">
 
   <display-name>Spring MVC Application</display-name>

   <servlet>
      <servlet-name>HelloWeb</servlet-name>
      <servlet-class>
         org.springframework.web.servlet.DispatcherServlet
      </servlet-class>
      <load-on-startup>1</load-on-startup>
   </servlet>

   <servlet-mapping>
      <servlet-name>HelloWeb</servlet-name>
      <url-pattern>*.jsp</url-pattern>
   </servlet-mapping>
</web-app>
```

The **web.xml** file will be kept in the **WebContent/WEB-INF** directory of your web application. Upon initialization of the HelloWeb DispatcherServlet, the framework will try to load the application context from a file named **[servlet-name]-servlet.xml** located in the application's WebContent/WEB-INF directory. In this case, our file will be **HelloWeb-servlet.xml.**

Next, the <servlet-mapping> tag indicates which URLs will be handled by which DispatcherServlet. Here, all the HTTP requests ending with **.jsp** will be handled by the HelloWeb DispatcherServlet.

If you do not want to go with the default filename as [servlet-name]-servlet.xml and default location as WebContent/WEB-INF, you **can customize this file name and location by adding the servlet listener ContextLoaderListener in your web.xml** file as follows −

```xml
<web-app...>

   <!-------- DispatcherServlet definition goes here----->
   ....
   <context-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>/WEB-INF/HelloWeb-servlet.xml</param-value>
   </context-param>

   <listener>
      <listener-class>
         org.springframework.web.context.ContextLoaderListener
      </listener-class>
   </listener>
</web-app>
```

https://www.tutorialspoint.com/springmvc/springmvc_quick_guide.htm
