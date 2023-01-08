<p align="center">
  <img src="images/ewm%20log.png">
</p>

## Назначение:
**Explore with me** — приложение, которое поможет найти человека для поездки в путешествие, похода в кино или другого совместного занятия.

## Инструкция по развертыванию проекта:
1. Скачать данный репозиторий
2. mvn clean
3. mvn package
4. docker-compose build
5. docker-compose up -d

## Сервисы:
* **Stats** — хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения.  
  Порт: 9090
     * Административный уровень доступа (доступен только для администратора проекта)
       * API для работы со статистикой посещений 
* **Main** — содержит всю основную логику приложения для каждого из трех уровней доступа (публичный, приватный, административный).  
  Порт: 8080
    * Публичный уровень доступа (доступен для всех пользователей)
      * API для работы с событиями
      * API для работы с категориями
      * для работы с подборками событий
    * Приватный уровень доступа (доступен только для зарегистрированных пользователей)
      * API для работы с событиями
      * API для работы с запросами текущего пользователя на участие в событиях
    * Административный уровень доступа (доступен только для администратора проекта)
      * API для работы с событиями
      * API для работы с категориями
      * API для работы с пользователями
      * API для работы с подборками событий

## Схема архитектуры проекта:
<p align="center">
  <img src="images/ewm%20architecture.png">
</p>

## Схема базы данных:
<p align="center">
  <img src="images/ewm%20db.png">
</p>