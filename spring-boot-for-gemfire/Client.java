import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Client {
  public static void main(String[] args) throws InterruptedException {

    ApplicationContext applicationContext =  new ClassPathXmlApplicationContext("application-context.xml");
    Object testRegion = applicationContext.getBean("TestRegion");
    System.out.println("testRegion = " + testRegion);
  }
} 
