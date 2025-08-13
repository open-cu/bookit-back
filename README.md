# bookit

## Оглавление
  * [Summary](/summary)
  * [Режимы запуска](/режимы-запуска)
  * [Настройка окружения](/настройка-окружения)
  * [Technology Stack](/technology-stack)

## Summary
Это - репозиторий с исходным кодом для серверной части приложения для удаленного бронирования мест в коворкинге

## Режимы запуска
* dev-mode

``` docker-compose -f docker-dev.yaml up```
* prod-mode

``` docker-compose -f docker-prod.yaml up```
* test-mode (используется только для ```Newman```)

``` docker-compose -f docker-test.yaml up```

## Настройка окружения
Переменные окружения, как правило, "пробрасываются" с помощью github secrets. В случае, если проект запускается локально, необходимо "пробросить" переменные окружения вручную (например, создав профиль запуска в Intellij idea или поместив все переменные окружения в .env и введя команду ```source .env```)

## Technology Stack
* ### Контейнеризация
  ![Docker](https://img.shields.io/badge/docker-257bd6?style=for-the-badge&logo=docker&logoColor=white)
  ![Docker Compose](https://img.shields.io/badge/docker--compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)

* ### Веб-фреймворк
  ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
  ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white)
  ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)

  Версия java (JDK) - 21

* ### Вспомогательныет утилиты
  ![Lombok](https://img.shields.io/badge/Lombok-%23FF6600?style=for-the-badge&logo=Lombok&logoColor=white)
  ![MapStruct](https://img.shields.io/badge/MapStruct-%23FF6600?style=for-the-badge&logo=MapStruct&logoColor=white)

* ### СУБД
  ![PostgreSQL](https://img.shields.io/badge/postgresql-4169e1?style=for-the-badge&logo=postgresql&logoColor=white)
  ![H2 Database](https://img.shields.io/badge/H2%20Database-2C3E50?style=for-the-badge&logo=h2Database&logoColor=white)

* ### Брокер сообщений (для почтовых рассылок)
  ![RabbitMQ](https://img.shields.io/badge/-rabbitmq-%23FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white)

* ### Сторонние инфраструктура и API
  ![Yandex Cloud](https://img.shields.io/badge/Yandex_Cloud-5282FF?style=for-the-badge&logo=yandexcloud&logoColor=white)
  ![Yandex Gpt](https://img.shields.io/badge/Yandex_Gpt-5282FF?style=for-the-badge&logo=yandexcloud&logoColor=white)
  ![Yandex Object Storage](https://img.shields.io/badge/Yandex_Object_Storage-5282FF?style=for-the-badge&logo=yandexcloud&logoColor=white)

* ### Автоматизация тестирования API приложения
  ![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
  ![Newman](https://img.shields.io/badge/Newman-007ACC?style=for-the-badge&logo=postman&logoColor=white)

* ### Документация API 
  ![Swagger](https://img.shields.io/badge/Swagger-6DB33F?style=for-the-badge&logo=swagger&logoColor=white)

* ### Инструменты автоматизации и командной работы от Github
  ![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)
  ![GitHub Projects](https://img.shields.io/badge/GitHub_Projects-181717?style=for-the-badge&logo=github&logoColor=white "Вместо Jira")

* ### Операционная система на сервере
  ![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=for-the-badge&logo=ubuntu&logoColor=white)
