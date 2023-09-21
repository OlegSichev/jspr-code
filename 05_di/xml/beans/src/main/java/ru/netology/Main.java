package ru.netology;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import ru.netology.service.PostService;

public class Main {
  public static void main(String[] args) {
    final var factory = new DefaultListableBeanFactory(); //создаем фабрику бинов
    final var reader = new XmlBeanDefinitionReader(factory); //создаем ридер для xml файлов с описанием бинов и
    // передаем в параметр нашу фабрику, которую мы создали выше
    reader.loadBeanDefinitions("beans.xml"); //вызываем метод reader.loadBeanDefinitions и указываем в методе
    // имя xml файла в котором идет описание наших бинов

    // получаем по имени бина
    final var controller = factory.getBean("postController");

    // получаем по классу бина
    final var service = factory.getBean(PostService.class);

    // по умолчанию создаётся лишь один объект на BeanDefinition
    final var isSame = service == factory.getBean("postService");
  }
}
